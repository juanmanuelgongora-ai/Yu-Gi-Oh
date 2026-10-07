package com.yugioh.duellite.api;

import com.yugioh.duellite.model.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Cliente HTTP para consultar la API pública YGOProDeck.
 * Se encarga del consumo de endpoints REST y el parseo JSON.
 */
public class YgoApiClient {
    private static final String API_URL = "https://db.ygoprodeck.com/api/v7/randomcard.php";
    private final HttpClient httpClient;

    public YgoApiClient() {
        // Configurar HttpClient habilitando redirecciones automáticas (301/302)
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Obtiene una carta aleatoria de la API y valida que sea de tipo "Monster".
     * Soporta la respuesta de la API envuelta en un arreglo "data".
     *
     * @return Una carta válida tipo Monster
     * @throws IOException          Si ocurre un error de red o supera el límite de
     *                              intentos
     * @throws InterruptedException Si se interrumpe la petición
     */
    public Card getRandomMonsterCard() throws IOException, InterruptedException {
        final int maxAttempts = 20;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Respuesta fallida de la API. Código HTTP: " + response.statusCode());
            }

            JSONObject root = new JSONObject(response.body());
            JSONArray data = root.optJSONArray("data");

            if (data == null || data.length() == 0) {
                throw new IOException("La API devolvió una respuesta sin cartas");
            }

            // La carta viene dentro del elemento 0 del arreglo "data"
            JSONObject json = data.getJSONObject(0);
            String type = json.optString("type", "");

            // Validar que el tipo contenga la palabra "Monster"
            if (type.toLowerCase().contains("monster")) {
                int id = json.optInt("id");
                String name = json.optString("name", "Desconocido");
                int atk = json.optInt("atk", 0);
                int def = json.optInt("def", 0);

                String imageUrl = "";
                JSONArray images = json.optJSONArray("card_images");
                if (images != null && images.length() > 0) {
                    imageUrl = images.getJSONObject(0).optString("image_url", "");
                }

                return new Card(id, name, type, atk, def, imageUrl);
            }

            // Pequeña pausa entre reintentos para no saturar la API
            Thread.sleep(150);
        }

        throw new IOException("No se pudo obtener una carta Monster tras " + maxAttempts + " intentos");
    }
}

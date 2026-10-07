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
        // Inicializar HttpClient con tiempo de espera máximo de 10 segundos
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Obtiene una carta aleatoria de la API y valida que sea de tipo "Monster".
     * En caso de recibir una carta de Magia/Trampa, realiza reintentos automáticos.
     *
     * @return Una carta válida tipo Monster
     * @throws IOException          Si ocurre un error de red
     * @throws InterruptedException Si se interrumpe la petición
     */
    public Card getRandomMonsterCard() throws IOException, InterruptedException {
        while (true) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                String type = json.optString("type", "");

                // Validar que el tipo contenga la palabra "Monster"
                if (type.toLowerCase().contains("monster")) {
                    int id = json.optInt("id");
                    String name = json.optString("name", "Desconocido");
                    int atk = json.optInt("atk", 0);
                    int def = json.optInt("def", 0);

                    // Extraer la URL de la imagen del arreglo JSON
                    String imageUrl = "";
                    if (json.has("card_images")) {
                        JSONArray images = json.getJSONArray("card_images");
                        if (images.length() > 0) {
                            imageUrl = images.getJSONObject(0).optString("image_url", "");
                        }
                    }

                    return new Card(id, name, type, atk, def, imageUrl);
                }
            } else {
                throw new IOException("Respuesta fallida de la API. Código HTTP: " + response.statusCode());
            }

            // Pequeña pausa entre reintentos para no saturar la red
            Thread.sleep(150);
        }
    }
}

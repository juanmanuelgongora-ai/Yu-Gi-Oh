package com.yugioh.duellite.test;

import com.yugioh.duellite.api.YgoApiClient;
import com.yugioh.duellite.logic.BattleListener;
import com.yugioh.duellite.logic.Duel;
import com.yugioh.duellite.model.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de prueba ejecutable por consola para el Backend de Yu-Gi-Oh! Duel
 * Lite.
 * Permite validar el consumo de la API, el filtro de cartas Monster, el
 * desacoplamiento
 * mediante BattleListener y la ejecución del duelo independientemente de la
 * interfaz gráfica Swing.
 */
public class ConsoleBattleTest implements BattleListener {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   PRUEBA TÉCNICA BACKEND: YU-GI-OH! DUEL LITE    ");
        System.out.println("==================================================\n");

        ConsoleBattleTest tester = new ConsoleBattleTest();
        tester.ejecutarPruebaBackend();
    }

    public void ejecutarPruebaBackend() {
        YgoApiClient apiClient = new YgoApiClient();
        Duel duel = new Duel();
        duel.setBattleListener(this);

        try {
            System.out.println("[API] Solicitando 3 cartas tipo Monstruo para el Jugador...");
            List<Card> cartasJugador = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Card c = apiClient.getRandomMonsterCard();
                cartasJugador.add(c);
                System.out.println("  -> Jugador Carta " + (i + 1) + ": " + c);
            }

            System.out.println("\n[API] Solicitando 3 cartas tipo Monstruo para la Máquina...");
            List<Card> cartasMaquina = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Card c = apiClient.getRandomMonsterCard();
                cartasMaquina.add(c);
                System.out.println("  -> Máquina Carta " + (i + 1) + ": " + c);
            }

            System.out.println("\n==================================================");
            System.out.println("             ¡INICIANDO EL DUELO!                 ");
            System.out.println("==================================================");

            duel.startDuel(cartasJugador, cartasMaquina);

            // Simular turnos de forma secuencial mientras el duelo esté activo
            int turno = 1;
            while (duel.isDuelActive() && !duel.getPlayerDeck().isEmpty()) {
                System.out.println("\n--- TURNO " + turno + " ---");
                // Seleccionar siempre la primera carta disponible del jugador para la prueba
                duel.playTurn(0);
                turno++;
            }

        } catch (Exception e) {
            System.err.println("Error durante la ejecución del backend: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Implementación de los eventos de BattleListener para consola ---

    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        System.out.println("Jugador jugó: " + playerCard);
        System.out.println("Máquina jugó: " + aiCard);
        System.out.println("Resultado Turno: " + winner);
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        System.out.println(">> Puntaje Actual -> Jugador: " + playerScore + " | Máquina: " + aiScore);
    }

    @Override
    public void onDuelEnded(String winner) {
        System.out.println("\n==================================================");
        System.out.println("             FIN DEL DUELO                        ");
        System.out.println("  " + winner);
        System.out.println("==================================================");
    }
}

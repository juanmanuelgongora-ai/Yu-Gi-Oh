package com.yugioh.duellite.logic;

import com.yugioh.duellite.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Administra el estado del duelo, las manos de cartas de los jugadores,
 * la comparación de estadísticas (ATK vs DEF) y la condición de victoria (2 de
 * 3 rondas).
 */
public class Duel {
    private final List<Card> playerDeck;
    private final List<Card> aiDeck;
    private final Random random;

    private int playerScore;
    private int aiScore;
    private boolean duelActive;
    private BattleListener listener;

    public Duel() {
        this.playerDeck = new ArrayList<>();
        this.aiDeck = new ArrayList<>();
        this.random = new Random();
        this.playerScore = 0;
        this.aiScore = 0;
        this.duelActive = false;
    }

    /**
     * Asigna el listener de eventos para notificar cambios a la UI o consola.
     */
    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
    }

    /**
     * Inicia un nuevo duelo con 3 cartas para el jugador y 3 para la máquina.
     */
    public void startDuel(List<Card> playerCards, List<Card> aiCards) {
        this.playerDeck.clear();
        this.playerDeck.addAll(playerCards);

        this.aiDeck.clear();
        this.aiDeck.addAll(aiCards);

        this.playerScore = 0;
        this.aiScore = 0;
        this.duelActive = true;

        if (listener != null) {
            listener.onScoreChanged(playerScore, aiScore);
        }
    }

    /**
     * Ejecuta un turno del duelo. El jugador elige su carta por índice y la máquina
     * selecciona una al azar.
     *
     * @param playerCardIndex Índice de la carta en la mano del jugador
     */
    public void playTurn(int playerCardIndex) {
        if (!duelActive || playerDeck.isEmpty() || aiDeck.isEmpty()) {
            return;
        }

        if (playerCardIndex < 0 || playerCardIndex >= playerDeck.size()) {
            return;
        }

        // Extraer la carta elegida por el jugador
        Card playerCard = playerDeck.remove(playerCardIndex);

        // La máquina selecciona una carta al azar de su mano disponible
        int aiIndex = random.nextInt(aiDeck.size());
        Card aiCard = aiDeck.remove(aiIndex);

        // Reglas de combate: Comparar ATK vs ATK (desempate por DEF)
        String winnerText;
        if (playerCard.getAtk() > aiCard.getAtk()) {
            playerScore++;
            winnerText = "¡Ganó el Jugador! (" + playerCard.getAtk() + " ATK vs " + aiCard.getAtk() + " ATK)";
        } else if (aiCard.getAtk() > playerCard.getAtk()) {
            aiScore++;
            winnerText = "¡Ganó la Máquina! (" + aiCard.getAtk() + " ATK vs " + playerCard.getAtk() + " ATK)";
        } else {
            // Desempate por DEF
            if (playerCard.getDef() > aiCard.getDef()) {
                playerScore++;
                winnerText = "¡Empate en ATK! Jugador gana por DEF (" + playerCard.getDef() + " vs " + aiCard.getDef()
                        + ")";
            } else if (aiCard.getDef() > playerCard.getDef()) {
                aiScore++;
                winnerText = "¡Empate en ATK! Máquina gana por DEF (" + aiCard.getDef() + " vs " + playerCard.getDef()
                        + ")";
            } else {
                winnerText = "¡Empate técnico absoluto! Ninguno suma puntos.";
            }
        }

        // Notificar el turno y el nuevo puntaje al listener
        if (listener != null) {
            listener.onTurn(playerCard.toString(), aiCard.toString(), winnerText);
            listener.onScoreChanged(playerScore, aiScore);
        }

        // Verificar si alguien ganó el duelo (2 de 3 rondas)
        checkDuelEnd();
    }

    private void checkDuelEnd() {
        if (playerScore >= 2) {
            duelActive = false;
            if (listener != null) {
                listener.onDuelEnded("¡EL JUGADOR ES EL GANADOR DEL DUELO!");
            }
        } else if (aiScore >= 2) {
            duelActive = false;
            if (listener != null) {
                listener.onDuelEnded("¡LA MÁQUINA ES LA GANADORA DEL DUELO!");
            }
        } else if (playerDeck.isEmpty() || aiDeck.isEmpty()) {
            duelActive = false;
            String finalWinner;
            if (playerScore > aiScore) {
                finalWinner = "¡EL JUGADOR GANA POR MAYOR PUNTAJE ACUMULADO!";
            } else if (aiScore > playerScore) {
                finalWinner = "¡LA MÁQUINA GANA POR MAYOR PUNTAJE ACUMULADO!";
            } else {
                finalWinner = "¡EL DUELO HA TERMINADO EN EMPATE!";
            }
            if (listener != null) {
                listener.onDuelEnded(finalWinner);
            }
        }
    }

    public boolean isDuelActive() {
        return duelActive;
    }

    public List<Card> getPlayerDeck() {
        return playerDeck;
    }

    public List<Card> getAiDeck() {
        return aiDeck;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getAiScore() {
        return aiScore;
    }
}

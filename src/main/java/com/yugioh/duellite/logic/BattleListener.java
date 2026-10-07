package com.yugioh.duellite.logic;

/**
 * Interfaz de eventos para el duelo de Yu-Gi-Oh!
 * Permite desacoplar totalmente la lógica del negocio (Backend)
 * de cualquier interfaz de usuario (Swing GUI, consola, etc.).
 */
public interface BattleListener {

    /**
     * Notifica el resultado del turno de combate.
     *
     * @param playerCard Texto descriptivo de la carta del jugador
     * @param aiCard     Texto descriptivo de la carta de la máquina
     * @param winner     Texto con el resultado/ganador del turno
     */
    void onTurn(String playerCard, String aiCard, String winner);

    /**
     * Notifica el cambio en el puntaje del duelo.
     *
     * @param playerScore Puntos acumulados por el jugador
     * @param aiScore     Puntos acumulados por la máquina
     */
    void onScoreChanged(int playerScore, int aiScore);

    /**
     * Notifica la finalización del duelo cuando un jugador alcanza 2 victorias.
     *
     * @param winner Nombre/mensaje del ganador final
     */
    void onDuelEnded(String winner);
}

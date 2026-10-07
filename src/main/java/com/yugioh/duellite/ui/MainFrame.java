package com.yugioh.duellite.ui;

import com.yugioh.duellite.api.YgoApiClient;
import com.yugioh.duellite.logic.BattleListener;
import com.yugioh.duellite.logic.Duel;
import com.yugioh.duellite.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Ventana principal: cartas del jugador, carta de la máquina, marcador,
 * log de batalla y botones.
 * Implementa BattleListener para recibir los eventos de la lógica del duelo.
 */
public class MainFrame extends JFrame implements BattleListener {

    private final Duel duel = new Duel();
    private final YgoApiClient apiClient = new YgoApiClient();

    private final List<CardPanel> cardPanels = new ArrayList<>();
    private final CardPanel aiCardPanel = new CardPanel();
    private final JTextArea logArea = new JTextArea(10, 40);
    private final JLabel scoreLabel = new JLabel("Jugador 0 - 0 Máquina", SwingConstants.CENTER);
    private final JButton startButton = new JButton("Iniciar duelo");
    private final JButton chooseButton = new JButton("Elegir carta");
    private CardPanel selectedPanel;

    public MainFrame() {
        super("Yu-Gi-Oh! Duel Lite");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        duel.setBattleListener(this);

        // Marcador (arriba)
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 18f));
        add(scoreLabel, BorderLayout.NORTH);

        // Cartas del jugador (centro)
        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardsRow.setBorder(BorderFactory.createTitledBorder("Tus cartas"));
        for (int i = 0; i < 3; i++) {
            CardPanel panel = new CardPanel();
            cardPanels.add(panel);
            cardsRow.add(panel);
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCard(panel);
                }
            });
        }
        add(cardsRow, BorderLayout.CENTER);

        // Carta jugada por la máquina (derecha)
        JPanel aiColumn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        aiColumn.setBorder(BorderFactory.createTitledBorder("Carta de la máquina"));
        aiColumn.add(aiCardPanel);
        add(aiColumn, BorderLayout.EAST);

        // Log desplazable + botones (abajo)
        logArea.setEditable(false);
        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        chooseButton.setEnabled(false);
        buttons.add(startButton);
        buttons.add(chooseButton);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // ActionListeners obligatorios
        startButton.addActionListener(e -> startDuel());
        chooseButton.addActionListener(e -> playTurn());

        pack();
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    // Acciones de la UI
    // ------------------------------------------------------------------

    private void selectCard(CardPanel panel) {
        if (!duel.isDuelActive() || panel.getCard() == null || !panel.isEnabled()) return;
        if (selectedPanel != null) selectedPanel.setSelected(false);
        selectedPanel = panel;
        panel.setSelected(true);
    }

    /**
     * Descarga 6 cartas Monster (3 jugador + 3 máquina) en segundo plano.
     * El duelo solo inicia cuando las 6 están cargadas.
     */
    private void startDuel() {
        startButton.setEnabled(false);
        chooseButton.setEnabled(false);
        selectedPanel = null;
        logArea.setText("");

        // Limpia las cartas del duelo anterior
        for (CardPanel p : cardPanels) p.clear();
        aiCardPanel.clear();

        log("Cargando cartas desde la API...");

        new SwingWorker<List<Card>, String>() {
            @Override
            protected List<Card> doInBackground() throws Exception {
                List<Card> cards = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    cards.add(apiClient.getRandomMonsterCard());
                    publish("Carta " + (i + 1) + "/6 cargada");
                }
                return cards;
            }

            @Override
            protected void process(List<String> messages) {
                for (String m : messages) log(m);
            }

            @Override
            protected void done() {
                try {
                    List<Card> cards = get();
                    List<Card> playerCards = new ArrayList<>(cards.subList(0, 3));
                    List<Card> aiCards = new ArrayList<>(cards.subList(3, 6));

                    for (int i = 0; i < 3; i++) {
                        CardPanel p = cardPanels.get(i);
                        p.setUsed(false);
                        p.setSelected(false);
                        p.setCard(playerCards.get(i));
                    }

                    duel.startDuel(playerCards, aiCards);
                    chooseButton.setEnabled(true);
                    log("¡Duelo iniciado! Selecciona una carta y pulsa \"Elegir carta\".\n");
                } catch (ExecutionException e) {
                    showError("Error de red: no se pudo cargar la carta. " + e.getCause().getMessage());
                    startButton.setEnabled(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showError("La carga de cartas fue interrumpida.");
                    startButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void playTurn() {
        if (!duel.isDuelActive()) return;
        if (selectedPanel == null) {
            JOptionPane.showMessageDialog(this, "Primero selecciona una carta.");
            return;
        }

        CardPanel played = selectedPanel;
        selectedPanel = null;

        // Duel elimina la carta de su mazo al jugarla, por eso se busca el índice actual
        int index = duel.getPlayerDeck().indexOf(played.getCard());
        played.setUsed(true);

        // Copia del mazo de la máquina antes de jugar el turno
        List<Card> aiBefore = new ArrayList<>(duel.getAiDeck());
        duel.playTurn(index);

        // La carta que ya no está en el mazo es la que jugó la máquina
        for (Card c : aiBefore) {
            if (!duel.getAiDeck().contains(c)) {
                aiCardPanel.setCard(c);
                break;
            }
        }
    }

    // ------------------------------------------------------------------
    // BattleListener (eventos que envía Duel)
    // ------------------------------------------------------------------

    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        SwingUtilities.invokeLater(() ->
                log("Jugador jugó: " + playerCard
                        + "\nMáquina jugó: " + aiCard
                        + "\n→ " + winner));
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("Jugador " + playerScore + " - " + aiScore + " Máquina");
            log("Puntaje: Jugador " + playerScore + " - " + aiScore + " Máquina\n");
        });
    }

    @Override
    public void onDuelEnded(String winner) {
        SwingUtilities.invokeLater(() -> {
            log(winner);
            chooseButton.setEnabled(false);
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(this, winner, "Fin del duelo",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    /** Mensaje de error visible (log + diálogo). */
    private void showError(String message) {
        log("ERROR: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
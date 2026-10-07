package com.yugioh.duellite;

import com.yugioh.duellite.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal para la aplicación Yu-Gi-Oh! Duel Lite.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

package com.yugioh.duellite.ui;

import com.yugioh.duellite.model.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class CardPanel extends JPanel {

    private static final int IMG_W = 160;
    private static final int IMG_H = 233;

    private final JLabel imageLabel = new JLabel("Sin carta", SwingConstants.CENTER);
    private final JLabel nameLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel statsLabel = new JLabel(" ", SwingConstants.CENTER);
    private Card card;

    public CardPanel() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setPreferredSize(new Dimension(190, 340));

        imageLabel.setPreferredSize(new Dimension(IMG_W, IMG_H));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.add(nameLabel);
        info.add(statsLabel);

        add(imageLabel, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);
    }

    public Card getCard() {
        return card;
    }


    public void setCard(Card card) {
        this.card = card;
        nameLabel.setText("<html><div style='text-align:center'>" + card.getName() + "</div></html>");
        statsLabel.setText("ATK " + card.getAtk() + " / DEF " + card.getDef());
        loadImage(card.getImageUrl());
    }


    public void setSelected(boolean selected) {
        setBorder(BorderFactory.createLineBorder(selected ? Color.RED : Color.GRAY, 3));
    }


    public void setUsed(boolean used) {
        setEnabled(!used);
        imageLabel.setEnabled(!used);
        nameLabel.setEnabled(!used);
        statsLabel.setEnabled(!used);
        if (used) setSelected(false);
    }

    private void loadImage(String url) {
        imageLabel.setIcon(null);
        imageLabel.setText("Cargando...");

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                Image img = ImageIO.read(new URL(url));
                if (img == null) throw new Exception("Imagen no válida");
                return new ImageIcon(img.getScaledInstance(IMG_W, IMG_H, Image.SCALE_SMOOTH));
            }

            @Override
            protected void done() {
                try {
                    imageLabel.setText("");
                    imageLabel.setIcon(get());
                } catch (Exception e) {
                    imageLabel.setIcon(null);
                    imageLabel.setText("No se pudo cargar la carta");
                }
            }
        }.execute();
    }
}
package com.yugioh.duellite.model;

/**
 * Representa una carta del juego Yu-Gi-Oh! obtenida desde la API YGOProDeck.
 * Diseñada para ser utilizada por el motor del juego y la interfaz gráfica.
 */
public class Card {
    private final int id;
    private final String name;
    private final String type;
    private final int atk;
    private final int def;
    private final String imageUrl;

    public Card(int id, String name, String type, int atk, int def, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return String.format("%s [ATK: %d | DEF: %d]", name, atk, def);
    }
}

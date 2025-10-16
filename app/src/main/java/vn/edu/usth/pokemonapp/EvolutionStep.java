package vn.edu.usth.pokemonapp;

import java.io.Serializable;

public class EvolutionStep implements Serializable {
    public String name;
    public String imageUrl;

    public EvolutionStep(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}

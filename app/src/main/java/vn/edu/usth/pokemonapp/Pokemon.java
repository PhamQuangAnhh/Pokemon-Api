package vn.edu.usth.pokemonapp;

import java.io.Serializable; // THÊM IMPORT NÀY
import java.util.List;

public class Pokemon implements Serializable { // THÊM "implements Serializable"
    public String name;
    public String imageUrl;
    public int hp;
    public int attack;
    public int defense;
    public int speed;
    public List<String> types;
    public String description;

    // LƯU Ý: Chúng ta sẽ truyền cả danh sách tiến hóa
    public List<EvolutionStep> evolutions;

    public Pokemon(String name, String imageUrl, int hp, int attack, int defense, int speed, List<String> types, String description, List<EvolutionStep> evolutions) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.types = types;
        this.description = description;
        this.evolutions = evolutions;
    }
}
package com.example.quiz.model.entity.Joker;

public class JokerMetaData {
    private final String idString;
    private final String name;
    private final String description;
    private final Integer cost;
    private final String type;
    private final Integer rarity; // Rarity of a joker (1 = common, 5 = legendary)
    private final Integer rarityWeight; // Weight for picking the rarity
    private final Integer uses; // number of uses for the joker. (this is just for the default value, might scale with tier on instantiation)

    public JokerMetaData(String idString, String name, String description, Integer cost, String type, Integer rarity, Integer rarityWeight, Integer uses) {
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.type = type;
        this.rarity = rarity;
        this.rarityWeight = rarityWeight;
        this.uses = uses;
    }

    public String getIdString() { return idString; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getCost() { return cost; }
    public String getType() { return type; }
    public Integer getRarity() { return rarity; }
    public Integer getRarityWeight() { return rarityWeight; }
    public Integer getUses() { return uses; }
}

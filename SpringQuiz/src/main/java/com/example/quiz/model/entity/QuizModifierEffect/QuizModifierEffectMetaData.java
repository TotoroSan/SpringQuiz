package com.example.quiz.model.entity.QuizModifierEffect;

// Metadata holder class
// Note: instead of using a class like this, we could've also stored the information static in the QUizModifierEffect classes
// but this would maintanability worse
public class QuizModifierEffectMetaData {

    private final String idString;
    private final String name;
    private final String description;
    private Integer duration; // this is depreceated since we roll the durations (still keep for completeness)
    private String type;
    private Boolean isPermanent;

    private Integer rarity; // describes the rarity of a effect class

    //possible weights: Common: 50, Uncommon: 30, Rare: 15, Epic: 4, Legendary: 1
    // effect picker rolls a random number X € (1,SUM(rarityWeight)).  the effect in which ragne this number falls is picked (e.g. first uncommon effect (1,50) second uncommon effect (51,100), rare effect (101, 115)
    // this way the probability of drawing an effect is proportional to its rarityWeight
    private int rarityWeight; // describes the weight for picking the effect class

    public QuizModifierEffectMetaData(String idString, String name, String description, Integer duration, String type, Boolean isPermanent, Integer rarity, Integer rarityWeight) {
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.type = type;
        this.isPermanent = isPermanent;
        this.rarity = rarity;
        this.rarityWeight = rarityWeight;
    }

    public String getIdString() {
        return idString;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPermanent() {
        return isPermanent;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }

    public Integer getRarity() {
        return rarity;
    }

    public void setRarity(Integer rarity) {
        this.rarity = rarity;
    }


    public int getRarityWeight() {
        return rarityWeight;
    }

    public void setRarityWeight(int rarityWeight) {
        this.rarityWeight = rarityWeight;
    }

}

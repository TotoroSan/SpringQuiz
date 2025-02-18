package com.example.quiz.model.entity.Joker;

import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "joker_type")
public abstract class Joker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    private String idString;
    private String name;
    private String description;
    private Integer cost;
    private Integer uses; // Anzahl der Verwendungen, default 1 (skalierbar mit Tier)
    private Integer rarity;
    private Integer tier; // Joker-Tier


    @ManyToOne
    @JoinColumn(name = "quiz_state_id")
    private QuizState quizState;

    public Joker() {
    }

    public Joker(String idString, String name, String description, Integer cost, Integer uses, Integer rarity, Integer tier) {
        this.idString = idString;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.uses = uses;
        this.rarity = rarity;
        this.tier = tier;
    }

    // Getter & Setter

    public UUID getId() {
        return id;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Integer getTier() {
        return tier;
    }

    public void setTier(Integer tier) {
        this.tier = tier;
    }

    public Integer getRarity() {
        return rarity;
    }

    public QuizState getQuizState() {
        return quizState;
    }

}

package com.example.quiz.model.entity;

import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// contains all modifiable quiz parameters
@Entity
public class QuizModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double scoreMultiplier = 1.0;
    private int difficultyModifier = 1; // default difficulty that is applied there is no min or max multiplier

    //TODO only one of either minDifficultyModifier or maximumDifficulty modifier can be in effect currently
    private Integer minDifficultyModifier = null; // min difficulty multiplier that is applied when set via effect
    private Integer maxDifficultyModifier = null;  // max difficulty multiplier that is applied when set via effect

    private String topicModifier = null; // null meaning all topics are possible // todo conver to a list for multiple topics possible

    private Integer lifeCounter = 3;

    private int cash = 0;
    private double cashMultiplier = 1.0;
    private int baseCashReward = 10;

    // orphanRemoval = true because if a effect is no longer active (= expired), it can be deleted
    @OneToMany(mappedBy = "quizModifier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizModifierEffect> activeQuizModifierEffects = new ArrayList<>();

    @OneToOne
    private QuizState quizState;

    // Default Constructor
    QuizModifier() {
    }

    // Each QuizModifier belongs to one QuizsState (Jpa needs this to set both sides of OneToOne relationship in table)
    public QuizModifier(QuizState quizState) {
        this.quizState = quizState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizState getQuizState() {
        return quizState;
    }

    public void setQuizState(QuizState quizState) {
        this.quizState = quizState;
    }

    // Getters and setters for scoreMultiplier, difficultyModifier, and activeModifiers
    public double getScoreMultiplier() {
        return scoreMultiplier;
    }

    public void setScoreMultiplier(double scoreMultiplier) {
        this.scoreMultiplier = scoreMultiplier;
    }

    public int getDifficultyModifier() {
        return difficultyModifier;
    }

    public void setDifficultyModifier(int difficultyModifier) {
        this.difficultyModifier = difficultyModifier;
    }

    public Integer getMinDifficultyModifier() {
        return minDifficultyModifier;
    }

    public void setMinDifficultyModifier(Integer minDifficultyModifier) {
        this.minDifficultyModifier = minDifficultyModifier;
    }

    public Integer getMaxDifficultyModifier() {
        return maxDifficultyModifier;
    }

    public void setMaxDifficultyModifier(Integer maxDifficultyModifier) {
        this.maxDifficultyModifier = maxDifficultyModifier;
    }

    public String getTopicModifier() {
        return topicModifier;
    }

    public void setTopicModifier(String topicModifier) {
        this.topicModifier = topicModifier;
    }

    public List<QuizModifierEffect> getActiveQuizModifierEffects() {
        return activeQuizModifierEffects;
    }

    public void setActiveQuizModifierEffects(List<QuizModifierEffect> activeQuizModifierEffects) {
        this.activeQuizModifierEffects = activeQuizModifierEffects;
    }

    public void addActiveQuizModifierEffect(QuizModifierEffect quizModifierEffect) {
        this.activeQuizModifierEffects.add(quizModifierEffect);
    }

    public void clearActiveQuizModifierEffects() {
        this.activeQuizModifierEffects.clear();
    }

    public Integer getLifeCounter() {
        return lifeCounter;
    }

    public void setLifeCounter(Integer lifeCounter) {
        this.lifeCounter = lifeCounter;
    }

    // Getter and Setter
    public double getCashMultiplier() {
        return cashMultiplier;
    }

    public void setCashMultiplier(double cashMultiplier) {
        this.cashMultiplier = cashMultiplier;
    }

    // Getter and Setter
    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    // Increment method
    public void addCash(int amount) {
        this.cash += amount;
    }

    public int getBaseCashReward() {
        return baseCashReward;
    }

    public void setBaseCashReward(int baseCashReward) {
        this.baseCashReward = baseCashReward;
    }
}
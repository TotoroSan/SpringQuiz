package com.example.quiz.model.dto;

public class QuizStateDto {
    private double score;
    private int currentRound;
    private String currentQuestionText;
    private QuizModifierDto quizModifierDto;



    private Boolean isActive;

    // Constructors
    public QuizStateDto() {}

    public QuizStateDto(double score, int currentRound, String currentQuestionText, QuizModifierDto quizModifierDto, Boolean isActive) {
        this.score = score;
        this.currentRound = currentRound;
        this.currentQuestionText = currentQuestionText;
        this.quizModifierDto = quizModifierDto;
        this.isActive = isActive;
    }

    // Getters and Setters
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getCurrentQuestionText() {
        return currentQuestionText;
    }

    public void setCurrentQuestionText(String currentQuestionText) {
        this.currentQuestionText = currentQuestionText;
    }
    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
    public QuizModifierDto getQuizModifierDto() {
        return quizModifierDto;
    }

    public void setQuizModifierDto(QuizModifierDto quizModifierDto) {
        this.quizModifierDto = quizModifierDto;
    }
}

package com.example.quiz.model.dto;

public class QuizStateDto {
    private double score;
    private int currentRound;
    private String currentQuestionText;
    private QuizModifierDto quizModifierDto;

    // Constructors
    public QuizStateDto() {}

    public QuizStateDto(double score, int currentRound, String currentQuestionText, QuizModifierDto quizModifierDto) {
        this.score = score;
        this.currentRound = currentRound;
        this.currentQuestionText = currentQuestionText;
        this.quizModifierDto = quizModifierDto;
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

    public QuizModifierDto getQuizModifierDto() {
        return quizModifierDto;
    }

    public void setQuizModifierDto(QuizModifierDto quizModifierDto) {
        this.quizModifierDto = quizModifierDto;
    }
}

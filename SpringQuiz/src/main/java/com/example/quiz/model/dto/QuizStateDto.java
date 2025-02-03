package com.example.quiz.model.dto;

import com.example.quiz.model.entity.Joker.Joker;

import java.util.List;

public class QuizStateDto {
    private double score;
    private int currentRound;
    private String currentQuestionText;
    private QuizModifierDto quizModifierDto;
    private List<JokerDto> activeJokerDtos;


    private Boolean isActive;

    // Constructors
    public QuizStateDto() {
    }

    public QuizStateDto(double score, int currentRound, String currentQuestionText, QuizModifierDto quizModifierDto, List<JokerDto> activeJokerDtos, Boolean isActive) {
        this.score = score;
        this.currentRound = currentRound;
        this.currentQuestionText = currentQuestionText;
        this.quizModifierDto = quizModifierDto;
        this.activeJokerDtos = activeJokerDtos;
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

    public List<JokerDto> getActiveJokerDtos() {
        return activeJokerDtos;
    }

    public void setActiveJokerDtos(List<JokerDto> activeJokerDtos) {
        this.activeJokerDtos = activeJokerDtos;
    }


    @Override
    public String toString() {
        return "QuizStateDto{" +
                "score=" + score +
                ", currentRound=" + currentRound +
                ", currentQuestionText='" + currentQuestionText + '\'' +
                ", quizModifierDto=" + quizModifierDto +
                ", isActive=" + isActive +
                '}';
    }
}

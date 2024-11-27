package com.example.quiz.model.dto;

import com.example.quiz.model.entity.QuizState;

// wrapper class dto to provide everything needed for loading a save game
public class QuizSaveDto {
    private QuizStateDto quizStateDto;
    private GameEventDto gameEventDto;

    public QuizSaveDto(QuizStateDto quizStateDto, GameEventDto gameEventDto) {
        this.quizStateDto = quizStateDto;
        this.gameEventDto = gameEventDto;
    }

}

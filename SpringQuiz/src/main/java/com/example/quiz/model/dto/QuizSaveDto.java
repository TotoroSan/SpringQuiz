package com.example.quiz.model.dto;

import com.example.quiz.model.entity.QuizState;

// Learning regarding serialization of parent-sub classes:
// If you assign a subclass object (e.g., QuestionGameEventDto) to a GameEventDto field in your QuizSaveDto and
// then serialize it, it will serialize all attributes from the subclass (not just the parent class's attributes)
// When using a polymorphic field (e.g., GameEventDto), the actual type of the object (the subclass) will determine which attributes are serialized.
// For instance, if GameEventDto is a superclass, and you assign a QuestionGameEventDto (subclass),
// Jackson will detect that the object is of type QuestionGameEventDto and serialize all its fields.


// wrapper class dto to provide everything needed for loading a save game in one call (quizstate + last game event)
public class QuizSaveDto {
    private QuizStateDto quizStateDto;
    private GameEventDto gameEventDto;

    public QuizSaveDto(QuizStateDto quizStateDto, GameEventDto gameEventDto) {
        this.quizStateDto = quizStateDto;
        this.gameEventDto = gameEventDto;
    }

    @Override
    public String toString() {
        return "QuizSaveDto{" +
                "quizStateDto=" + quizStateDto +
                ", gameEventDto=" + gameEventDto +
                '}';
    }

    public QuizStateDto getQuizStateDto() {
        return quizStateDto;
    }

    public GameEventDto getGameEventDto() {
        return gameEventDto;
    }

}

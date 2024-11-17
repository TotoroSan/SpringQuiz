package com.example.quiz.model.dto;

import java.util.List;

// this is a "Wrapper" Dto for Game Events
// the frontend uses eventType to dynamically choose the correct components
// for simplicitys sake and because its "only" a dto we instantiate it like this isntead of using proper parentclass - subclass structure
public class GameEventDto {
    private final String eventType;
    private QuestionWithShuffledAnswersDto question;
    private List<QuizModifierEffectDto> quizModifierEffects;

    // Constructor for a question event
    public GameEventDto(QuestionWithShuffledAnswersDto question) {
        this.eventType = "QUESTION";
        this.question = question;
    }

    // Constructor for a modifier effects event
    public GameEventDto(List<QuizModifierEffectDto> quizModifierEffects) {
        this.eventType = "MODIFIER_EFFECTS";
        this.quizModifierEffects = quizModifierEffects;
    }

    // Getters and setters
    public String getEventType() {
        return eventType;
    }

    public QuestionWithShuffledAnswersDto getQuestion() {
        return question;
    }

    public List<QuizModifierEffectDto> getQuizModifierEffects() {
        return quizModifierEffects;
    }
}


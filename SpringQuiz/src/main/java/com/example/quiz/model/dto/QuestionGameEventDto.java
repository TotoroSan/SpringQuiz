package com.example.quiz.model.dto;

import com.example.quiz.model.enums.GameEventType;

import java.util.List;

// todo maybe do parent / child class with gameventdto
public class QuestionGameEventDto extends GameEventDto {
// this class isa DTO (Data Transfer Object) and solely serves as business logic class.
// it is used as a response class and is not persisted
// we use Strings for the answer instead of answer objects to avoid cheating on client side. 
// if we sent answer objects, one could inspect the payload and see the flag if a answer is correct.


    private String questionText;        // The question itself
    private Long questionId;        // Id of the question
    private List<AnswerDto> shuffledAnswers;  // The shuffled list of answers


    // A list of answer IDs (or identifiers) that have been eliminated.
    private List<Long> eliminatedAnswerIds;

    // Empty constructor for testing and framework purposes
    public QuestionGameEventDto() {
        super(GameEventType.QUESTION);
    }


    // Constructor
    public QuestionGameEventDto(String questionText, Long questionId, List<AnswerDto> shuffledAnswers, List<Long> eliminatedAnswerIds) {
        super(GameEventType.QUESTION);
        this.questionText = questionText;
        this.questionId = questionId;
        this.shuffledAnswers = shuffledAnswers;
        this.eliminatedAnswerIds = eliminatedAnswerIds;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AnswerDto> getShuffledAnswers() {
        return shuffledAnswers;
    }

    public void setShuffledAnswers(List<AnswerDto> shuffledAnswers) {
        this.shuffledAnswers = shuffledAnswers;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getEliminatedAnswerIds() {
        return eliminatedAnswerIds;
    }

    public void setEliminatedAnswerIds(List<Long> eliminatedAnswerIds) {
        this.eliminatedAnswerIds = eliminatedAnswerIds;
    }


    @Override
    public String toString() {
        return "QuestionGameEventDto{" +
                "questionText='" + questionText + '\'' +
                ", questionId=" + questionId +
                ", shuffledAnswers=" + shuffledAnswers +
                '}';
    }
}

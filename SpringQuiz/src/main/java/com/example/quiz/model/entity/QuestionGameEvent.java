package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("QUESTION")
@Table(name = "question_game_event") // Creates a dedicated table
public class QuestionGameEvent extends GameEvent {

    @Column(nullable = false)
    private Long questionId;

    private String questionText;        // The question itself

    @ElementCollection
    @Column(name = "shuffled_answer_id") // Define the column for the Answer ID
    private List<Answer> shuffledAnswers;  // The shuffled list of answers


    // Represents the IDs of answers that have been eliminated via Joker usage
    @ElementCollection
    @OrderColumn(name = "eliminated_order")
    private List<Long> eliminatedAnswerIds = new ArrayList<>();

    // If true, the question was skipped via SkipQuestionJoker
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean skipUsed = false;

    // Constructors
    public QuestionGameEvent() {
        super();
    }

    public QuestionGameEvent(QuizState quizState, Long questionId, String questionText, List<Answer> shuffledAnswers) {
        super(quizState);
        this.questionId = questionId;
        this.questionText = questionText;
        this.shuffledAnswers = shuffledAnswers;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Answer> getShuffledAnswers() {
        return shuffledAnswers;
    }

    public void setShuffledAnswers(List<Answer> shuffledAnswers) {
        this.shuffledAnswers = shuffledAnswers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Long> getEliminatedAnswerIds() {
        return eliminatedAnswerIds;
    }

    public void setEliminatedAnswerIds(List<Long> eliminatedAnswerIds) {
        this.eliminatedAnswerIds = eliminatedAnswerIds;
    }

    public boolean isSkipUsed() {
        return skipUsed;
    }

    public void setSkipUsed(boolean skipUsed) {
        this.skipUsed = skipUsed;
    }



}

package com.example.quiz.model.entity;

import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.Quiz;
import com.example.quiz.model.entity.QuizState;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("QUESTION")
public class QuestionGameEvent extends GameEvent {

    @Column(nullable = false)
    private Long questionId;

    private String questionText;        // The question itself

    @ElementCollection
    @Column(name = "shuffled_answer_id") // Define the column for the Answer ID
    private List<Answer> shuffledAnswers;  // The shuffled list of answers

    // Constructors
    public QuestionGameEvent() {
        super();
    }

    public QuestionGameEvent(QuizState quizState, Long questionId, String questionText,   List<Answer> shuffledAnswers) {
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


}

package com.example.quiz.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CORRECT")
public class CorrectAnswer extends Answer {
	
	public CorrectAnswer() {
		super.setCorrect(true); // TODO keep?
	};
	
    // You can add specific behavior for correct answers if needed
    public CorrectAnswer(String answerText, Question question) {
    	super(answerText, true, question);
    }
}

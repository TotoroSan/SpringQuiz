package com.example.quiz.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MOCK")
public class MockAnswer extends Answer {
	
	public MockAnswer() {
		super.setCorrect(false); // TODO keep?
	}
	
    // You can add specific behavior for mock answers if needed
    public MockAnswer(String answerText, Question question) {
    	super(answerText, false, question);
    }  
}

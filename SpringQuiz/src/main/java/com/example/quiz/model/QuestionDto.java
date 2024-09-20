package com.example.quiz.model;
import java.util.List;

public class QuestionDto {
	// this is the data transfer object view of a question. 
	// we use it to post a question with answers in string form. 
	// the data will be processed to regular model question and answer objects that will be persisted.
	
	
    private String questionText;
    private String realAnswer;  // This will be a string from JSON
    private List<String> mockAnswers;  // List of strings from JSON

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getRealAnswer() {
        return realAnswer;
    }

    public void setRealAnswer(String realAnswer) {
        this.realAnswer = realAnswer;
    }

    public List<String> getMockAnswers() {
        return mockAnswers;
    }

    public void setMockAnswers(List<String> mockAnswers) {
        this.mockAnswers = mockAnswers;
    }
}

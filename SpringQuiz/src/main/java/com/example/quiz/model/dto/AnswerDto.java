package com.example.quiz.model.dto;



public class AnswerDto {
	// DTO to transfer answer text + id. 
	//text is not sufficient because we need to validate answer against the backend.
	
	
    private Long id;        // Unique ID for the answer
    private String text;    // The text of the answer
    
    // No-argument constructor for testing and framework purposes
    public AnswerDto() {}

    public AnswerDto(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}

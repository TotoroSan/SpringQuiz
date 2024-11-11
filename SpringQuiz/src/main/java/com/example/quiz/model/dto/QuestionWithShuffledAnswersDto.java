package com.example.quiz.model.dto;
import java.util.List;


public class QuestionWithShuffledAnswersDto {
// this class isa DTO (Data Transfer Object) and solely serves as business logic class.
// it is used as a response class and is not persisted
// we use Strings for the answer instead of answer objects to avoid cheating on client side. 
// if we sent answer objects, one could inspect the payload and see the flag if a answer is correct.
	
	
    private String questionText;        // The question itself
    private Long questionId; 		// Id of the question 
    private List<AnswerDto> shuffledAnswers;  // The shuffled list of answers
    
    // Empty constructor for testing and framework purposes
    public QuestionWithShuffledAnswersDto() {};

    // Constructor
    public QuestionWithShuffledAnswersDto(String questionText, Long questionId, List<AnswerDto> shuffledAnswers) {
        this.questionText = questionText;
        this.questionId = questionId; 
        this.shuffledAnswers = shuffledAnswers;
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
}

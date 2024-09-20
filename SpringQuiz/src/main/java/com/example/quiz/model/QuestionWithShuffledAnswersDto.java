package com.example.quiz.model;
import java.util.List;


public class QuestionWithShuffledAnswersDto {
// this class isa DTO (Data Transfer Object) and solely serves as business logic class.
// it is used as a response class and is not persisted
// we use Strings for the answer instead of answer objects to avoid cheating on client side. 
// if we sent answer objects, one could inspect the payload and see the flag if a answer is correct.
	
	
    private String questionText;        // The question itself
    private List<AnswerDto> shuffledAnswers;  // The shuffled list of answers

    // Constructor
    public QuestionWithShuffledAnswersDto(String questionText, List<AnswerDto> shuffledAnswers) {
        this.questionText = questionText;
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
}

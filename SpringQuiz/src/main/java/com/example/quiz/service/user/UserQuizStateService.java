package com.example.quiz.service.user;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizState;
import com.example.quiz.repository.QuizStateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQuizStateService {
	// responsible for actions that modify the state, like incrementing the question index.
	
	
	// by stating autowired the object creation gets handed over to spring. 
	// it is an "automatic" connection to another class that is needed and has a connection to our class. we basically "wire" the classes -> if we create a quizservice we always reate a quizrepository via that wire
	// without spring i would need to create the repository in the constructor and delete it after the operation or on end of connection
	
    @Autowired 
    private QuizStateRepository quizStateRepository;
    
    @Autowired
    private UserQuestionService userQuestionService;
    
    // Initialize quiz with questions fetched from the QuestionService
    public QuizState startNewQuiz() {
    	// TODO persist 
        return new QuizState();
    }

    // Get the next question that hasn't been answered yet
    public Question getNextQuestion(QuizState quizState) {
        if (!hasMoreQuestions(quizState)) {
            return null;  // No more questions
        }
        Question currentQuestion = getCurrentQuestion(quizState);
        quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1);  // Move to the next question
        return currentQuestion;
    }
    
    public void addQuestion(QuizState quizState, Question question) {
    	quizState.getAllQuestions().add(question);
    }
    
     
    // Get the current question
    public Question getCurrentQuestion(QuizState quizState) {
        if (quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size()) {
            return quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex());
        }
        return null;  // No more questions
    }

    // Move to the next question
    public void incrementCurrentQuestionIndex(QuizState quizState) {
        //if (hasMoreQuestions(quizState)) {
            quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1); // Move to the next question
        //} else {
            //System.out.println("No more questions available.");
        //}
    }

    // Increment the score
    public void incrementScore(QuizState quizState) {
        quizState.setScore(quizState.getScore() + 1);
    }
    
    // Increment the round
    public void incrementCurrentRound(QuizState quizState) {
        quizState.setCurrentRound(quizState.getCurrentRound() + 1);
    }
    
    // Check if a question is completed
    public boolean isCompleted(QuizState quizState, Long questionId) {
        return quizState.getCompletedQuestionIds().contains(questionId);
    }

    // Mark a question as completed
    public void markQuestionAsCompleted(QuizState quizState, Long questionId) {
    	quizState.getCompletedQuestionIds().add(questionId);
    }
    
    // Check if quiz has more questions 
    public boolean hasMoreQuestions(QuizState quizState) {
        return quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size();
    }

}

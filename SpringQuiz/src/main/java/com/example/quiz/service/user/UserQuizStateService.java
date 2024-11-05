package com.example.quiz.service.user;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.entity.Question;
import com.example.quiz.model.entity.Quiz;
import com.example.quiz.model.entity.QuizState;
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
    public QuizState startNewQuiz(Long userId) {
        QuizState quizState = new QuizState(userId);
        return quizStateRepository.save(quizState);
    }
    
    // Method to get the latest quiz state by user ID
    public Optional<QuizState> getLatestQuizStateByUserId(Long userId) {
        return quizStateRepository.findFirstByUserIdOrderByIdDesc(userId);
    }
    
    // Get all QuizStates by user ID
    public Optional<QuizState> getAllQuizStatesByUserId(Long userId) {
        return quizStateRepository.findByUserId(userId);
    }
    
    // Save changes to the QuizState
    public void saveQuizState(QuizState quizState) {
        quizStateRepository.save(quizState);
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
    	saveQuizState(quizState);
    }
    
     
    // Move to the next question
    public void incrementCurrentQuestionIndex(QuizState quizState) {
        //if (hasMoreQuestions(quizState)) {
            quizState.setCurrentQuestionIndex(quizState.getCurrentQuestionIndex() + 1); // Move to the next question
        //} else {
            //System.out.println("No more questions available.");
        //}
            saveQuizState(quizState);
    }

    // Increment the score
    public void incrementScore(QuizState quizState) {
        quizState.setScore(quizState.getScore() + 1);
        saveQuizState(quizState);
    }
    
    // Increment the round
    public void incrementCurrentRound(QuizState quizState) {
        quizState.setCurrentRound(quizState.getCurrentRound() + 1);
        saveQuizState(quizState);
    }
    
    // Mark a question as completed
    public void markQuestionAsCompleted(QuizState quizState, Long questionId) {
    	quizState.getCompletedQuestionIds().add(questionId);
    	saveQuizState(quizState);
    }
    
    // Get the current question
    public Question getCurrentQuestion(QuizState quizState) {
        if (quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size()) {
            return quizState.getAllQuestions().get(quizState.getCurrentQuestionIndex());
        }
        return null;  // No more questions
    }
    
    // Check if a question is completed
    public boolean isCompleted(QuizState quizState, Long questionId) {
        return quizState.getCompletedQuestionIds().contains(questionId);
    }
    
    // Check if quiz has more questions 
    public boolean hasMoreQuestions(QuizState quizState) {
        return quizState.getCurrentQuestionIndex() < quizState.getAllQuestions().size();
    }

}

package com.example.quiz.service.user;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.AnswerDto;
import com.example.quiz.model.Question;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.QuizState;
import com.example.quiz.repository.QuizRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserQuizService {
	
	// by stating autowired the object creation gets handed over to spring. 
	// it is an "automatic" connection to another class that is needed and has a connection to our class. we basically "wire" the classes -> if we create a quizservice we always reate a quizrepository via that wire
	// without spring i would need to create the repository in the constructor and delete it after the operation or on end of connection
	
    @Autowired 
    private QuizRepository quizRepository;
    
    @Autowired
    private UserQuestionService userQuestionService;
    
    // Initialize quiz with questions fetched from the QuestionService
    public QuizState startNewQuiz() {
        return new QuizState();
    }

    // Get the next question that hasn't been answered yet
    public Question getNextQuestion(QuizState quizState) {
        if (!quizState.hasMoreQuestions()) {
            return null;  // No more questions
        }
        Question currentQuestion = quizState.getCurrentQuestion();
        quizState.incrementQuestionIndex();  // Move to the next question
        return currentQuestion;
    }

}

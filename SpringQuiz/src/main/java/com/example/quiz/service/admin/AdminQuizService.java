//package com.example.quiz.service.admin;
//
//import com.example.quiz.exception.ResourceNotFoundException;
//import com.example.quiz.model.dto.AnswerDto;
//import com.example.quiz.model.entity.Question;
//import com.example.quiz.model.entity.Quiz;
//import com.example.quiz.model.entity.QuizState;
//import com.example.quiz.repository.QuizRepository;
//import com.example.quiz.repository.QuizStateRepository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class AdminQuizService {
//	
//	// TODO FIX 
//	// by stating autowired the object creation gets handed over to spring. 
//	// it is an "automatic" connection to another class that is needed and has a connection to our class. we basically "wire" the classes -> if we create a quizservice we always reate a quizrepository via that wire
//	// without spring i would need to create the repository in the constructor and delete it after the operation or on end of connection
//	
//    @Autowired 
//    private QuizRepository quizRepository;
//    
//    @Autowired
//    private AdminQuestionService adminQuestionService;
//    
//    // Create a new Quiz
//    public Quiz createQuiz(Quiz quiz) {
//        return quizRepository.save(quiz);  // Save quiz to the database
//    }
//
//    // Get all quizzes
//    public List<Quiz> getAllQuizzes() {
//        return quizRepository.findAll();  // Retrieve all quizzes
//    }
//
//    // Get a quiz by its ID
//    public Optional<Quiz> getQuizById(Long id) {
//        return quizRepository.findById(id);  // Find quiz by ID
//    }
//
//    // Update an existing quiz
//    public Quiz updateQuiz(Long id, Quiz quizDetails) {
//        Quiz quiz = quizRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found for this id :: " + id));
//        
//        quiz.setTitle(quizDetails.getTitle());
//        quiz.setQuestions(quizDetails.getQuestions());
//
//        return quizRepository.save(quiz);  // Save updated quiz
//    }
//
//    // Delete a quiz by ID
//    public void deleteQuiz(Long id) {
//        Quiz quiz = quizRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found for this id :: " + id));
//        
//        quizRepository.delete(quiz);  // Delete the quiz
//    }
//}

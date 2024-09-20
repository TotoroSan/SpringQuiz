//package com.example.quiz.config;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.example.quiz.model.Answer;
//import com.example.quiz.model.Question;
//import com.example.quiz.repository.QuestionRepository;
//
//import java.util.Arrays;
//
//@Configuration
//public class DataLoader {
//	// Insert mock data for testing
//	// The loadData method will run at application startup and use the QuestionRepository to insert mock data into the H2 database.
//	
//    @Bean
//    public CommandLineRunner loadData(QuestionRepository questionRepository) {
//        return args -> {
//            // Create first question
//            Question question1 = new Question();
//            question1.setQuestionText("What is the capital of France?");
//            question1.setRealAnswer(new Answer("Paris", true, question1));
//            question1.setMockAnswers(Arrays.asList(
//                    new Answer("London", false, question1),
//                    new Answer("Berlin", false, question1),
//                    new Answer("Rome", false, question1)
//            ));
//
//            // Create second question
//            Question question2 = new Question();
//            question2.setQuestionText("Who developed the theory of relativity?");
//            question2.setRealAnswer(new Answer("Einstein", true, question2));
//            question2.setMockAnswers(Arrays.asList(
//                    new Answer("Newton", false, question2),
//                    new Answer("Tesla", false, question2),
//                    new Answer("Galileo", false, question2)
//            ));
//
//            // Save questions to the repository
//            questionRepository.save(question1);
//            questionRepository.save(question2);
//        };
//    }
//}

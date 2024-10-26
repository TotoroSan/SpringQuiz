package com.example.quiz.config;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
=======
=======
>>>>>>> parent of 1a2ebc3 (Update)

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

<<<<<<< HEAD
>>>>>>> parent of 1a2ebc3 (Update)
=======
>>>>>>> parent of 1a2ebc3 (Update)
import com.example.quiz.model.Answer;
import com.example.quiz.model.CorrectAnswer;
import com.example.quiz.model.MockAnswer;
import com.example.quiz.model.Question;
import com.example.quiz.repository.QuestionRepository;
<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Arrays;
=======

import java.util.Arrays;

>>>>>>> parent of 1a2ebc3 (Update)
=======

import java.util.Arrays;

>>>>>>> parent of 1a2ebc3 (Update)
@Configuration
public class DataLoader {
	// Insert mock data for testing
	// The loadData method will run at application startup and use the QuestionRepository to insert mock data into the H2 database.
	
    @Bean
    public CommandLineRunner loadData(QuestionRepository questionRepository) {
        return args -> {
            // Create first question
            Question question1 = new Question();
            question1.setQuestionText("What is the capital of France?");
            question1.setCorrectAnswer(new CorrectAnswer("Paris", question1));
            question1.setMockAnswers(Arrays.asList(
                    new MockAnswer("London",  question1),
                    new MockAnswer("Berlin",  question1),
                    new MockAnswer("Rome", question1)
            ));
<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> parent of 1a2ebc3 (Update)
=======

>>>>>>> parent of 1a2ebc3 (Update)
            // Create second question
            Question question2 = new Question();
            question2.setQuestionText("Who developed the theory of relativity?");
            question2.setCorrectAnswer(new CorrectAnswer("Einstein", question2));
            question2.setMockAnswers(Arrays.asList(
                    new MockAnswer("Newton", question2),
                    new MockAnswer("Tesla", question2),
                    new MockAnswer("Galileo", question2)
            ));
<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> parent of 1a2ebc3 (Update)
=======

>>>>>>> parent of 1a2ebc3 (Update)
            // Save questions to the repository
            questionRepository.save(question1);
            questionRepository.save(question2);
        };
    }
<<<<<<< HEAD
<<<<<<< HEAD
}
=======
}
>>>>>>> parent of 1a2ebc3 (Update)
=======
}
>>>>>>> parent of 1a2ebc3 (Update)

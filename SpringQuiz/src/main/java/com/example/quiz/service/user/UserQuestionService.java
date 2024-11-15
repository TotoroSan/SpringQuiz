package com.example.quiz.service.user;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.dto.AnswerDto;
import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.dto.QuestionWithShuffledAnswersDto;
import com.example.quiz.model.entity.Answer;
import com.example.quiz.model.entity.MockAnswer;
import com.example.quiz.model.entity.Question;
import com.example.quiz.repository.QuestionRepository;

@Service
public class UserQuestionService {
    private static final Logger logger = LoggerFactory.getLogger(UserQuestionService.class);

    @Autowired
    private QuestionRepository questionRepository;
    

    public QuestionDto convertToDto(Question question) {
        String realAnswerText = question.getCorrectAnswer().getAnswerText();

        // extract mockAnswer text as string from answer objects
        List<String> mockAnswersText = question.getMockAnswers()
                                               .stream()
                                               .map(Answer::getAnswerText)
                                               .collect(Collectors.toList());

        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText(question.getQuestionText());
        questionDto.setRealAnswer(realAnswerText);
        questionDto.setMockAnswers(mockAnswersText);

        return questionDto;
    }

    // Get a random question
	public Question getRandomQuestion() {
		// get random question from repository
		Question question = questionRepository.findRandomQuestion();

        // Return the question
        return question; 
	}
	
    // Get a random question exluding the ones that have already been answered
	public Question getRandomQuestionExcludingCompleted(Set<Long> completedQuestionIds) {
        // call overloaded method without diffculty parameter
        return getRandomQuestionExcludingCompleted(completedQuestionIds, null);
	}

    public Question getRandomQuestionExcludingCompleted(Set<Long> completedQuestionIds, Integer difficulty) {
        logger.info("Picking random uncompleted question");

        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> questionsPage;

        // get random question from repository that hasn't been answered
        // this means we get a page with 0 to 1 objects
        // if no difficulty is given, we just get any random question
        if (difficulty != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompletedAndDifficulty(completedQuestionIds, difficulty, pageable);
        } else {
            questionsPage = questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable);
        }

        if (questionsPage.isEmpty()) {
            logger.error("No available question found");
            throw new RuntimeException("No available question found.");
        }

        Question uncompletedQuestion = questionsPage.getContent().get(0);
        logger.debug("Successfully picked random uncompleted questio: ", uncompletedQuestion.getId());
        return uncompletedQuestion;
    }



    // Get a Question with Shuffled Answers
	public QuestionWithShuffledAnswersDto createQuestionWithShuffledAnswersDto(Question question) {
		// TODO check where to add exception for findRandomQuestion (e.g. there are no questions left)
		// we use this datatype and not questionDto because we do not want to reveal real true answer to the client

        logger.info("Creating QuestionWithShuffledAnswersDto for question: ", question.getId());

		// Prepare a list to hold the final answers (including the real one)
        List<AnswerDto> finalAnswers = new ArrayList<>();

        // Add the correct answer
        finalAnswers.add(new AnswerDto(question.getCorrectAnswer().getId(), question.getCorrectAnswer().getAnswerText()));

        // Create a copy of the mock answers list to shuffle
        List<MockAnswer> mockAnswersCopy = new ArrayList<>(question.getMockAnswers());

        Collections.shuffle(mockAnswersCopy);  // Shuffle the copied list

        // Add up to 3 mock answers from the shuffled copy
        for (int i = 0; i < Math.min(3, mockAnswersCopy.size()); i++) {
        	finalAnswers.add(new AnswerDto(mockAnswersCopy.get(i).getId(), mockAnswersCopy.get(i).getAnswerText()));
        }

        // Shuffle the final list of answers (real + selected mock answers)
        Collections.shuffle(finalAnswers);

        logger.info("Created QuestionWithShuffledAnswersDto for question: ", question.getId(), "with answers {}", finalAnswers);

        // Return the question with the shuffled answers
        return new QuestionWithShuffledAnswersDto(question.getQuestionText(), question.getId(), finalAnswers);
	}
	

	
	
	
}

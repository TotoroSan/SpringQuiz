package com.example.quiz.service.user;


import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.model.entity.*;
import com.example.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return getRandomQuestion(null);
    }

    // Get a random question by topic
    public Question getRandomQuestion(String topic) {

        // get random question from repository
        Question question;
        if (topic != null) {
            logger.info("Retreiving random question with topic", topic);
            question = questionRepository.findRandomQuestion(topic);
        } else {
            logger.info("Retreiving random question");
            question = questionRepository.findRandomQuestion();
        }

        logger.debug("Successfully retreived random question");
        // Return the question
        return question;
    }


    // Get a random question exluding the ones that have already been answered
    public Question getRandomQuestionExcludingCompleted(Set<Long> completedQuestionIds) {
        // call overloaded method without diffculty parameter
        return getRandomQuestionExcludingCompleted(completedQuestionIds, null, null);
    }

    public Question getRandomQuestionExcludingCompleted(Set<Long> completedQuestionIds, Integer difficulty) {
        return getRandomQuestionExcludingCompleted(completedQuestionIds, difficulty, null);
    }

    public Question getRandomQuestionExcludingCompleted(Set<Long> completedQuestionIds, Integer difficulty, String topic) {
        logger.info("Picking random uncompleted question");

        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> questionsPage;

        // get random question from repository that hasn't been answered
        // this means we get a page with 0 to 1 objects
        // if no difficulty is given, we just get any random question
        if (difficulty != null && topic != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, topic, difficulty, pageable);
        } else if (difficulty != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, difficulty, pageable);
        } else if (topic != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, topic, pageable);
        } else {
            questionsPage = questionRepository.findRandomQuestionExcludingCompleted(completedQuestionIds, pageable);
        }

        if (questionsPage.isEmpty()) {
            logger.error("No available question found");
            return null; // todo temporary
            //throw new RuntimeException("No available question found.");
        }

        Question uncompletedQuestion = questionsPage.getContent().get(0);
        logger.debug("Successfully picked random uncompleted questio: ", uncompletedQuestion.getId());
        return uncompletedQuestion;
    }

    public Question getRandomQuestionExcludingCompletedWithMaxDifficultyLimit(Set<Long> completedQuestionIds, Integer difficulty) {
        return getRandomQuestionExcludingCompletedWithMaxDifficultyLimit(completedQuestionIds, difficulty, null);
    }

    public Question getRandomQuestionExcludingCompletedWithMaxDifficultyLimit(Set<Long> completedQuestionIds, Integer difficulty, String topic) {
        logger.info("Picking random uncompleted question with maximum difficulty limit of: ", difficulty, " for topic: ", topic);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> questionsPage;

        // get random question from repository that hasn't been answered and has a given maximum difficulty
        // this means we get a page with 0 to 1 objects
        // if no topic is given, we just seach for any topic
        if (topic != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompletedWithMaxDifficultyLimit(completedQuestionIds, topic, difficulty, pageable);

        } else {
            questionsPage = questionRepository.findRandomQuestionExcludingCompletedWithMaxDifficultyLimit(completedQuestionIds, difficulty, pageable);
        }

        if (questionsPage.isEmpty()) {
            logger.error("No available question found");
            throw new RuntimeException("No available question found.");
        }

        Question uncompletedQuestion = questionsPage.getContent().get(0);
        logger.debug("Successfully picked random uncompleted questio: ", uncompletedQuestion.getId());
        return uncompletedQuestion;
    }

    public Question getRandomQuestionExcludingCompletedWithMinDifficultyLimit(Set<Long> completedQuestionIds, Integer difficulty) {
        return getRandomQuestionExcludingCompletedWithMinDifficultyLimit(completedQuestionIds, difficulty, null);
    }

    public Question getRandomQuestionExcludingCompletedWithMinDifficultyLimit(Set<Long> completedQuestionIds, Integer difficulty, String topic) {
        logger.info("Picking random uncompleted question with maximum difficulty limit of: ", difficulty, " for topic: ", topic);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Question> questionsPage;

        // get random question from repository that hasn't been answered and has a given maximum difficulty
        // this means we get a page with 0 to 1 objects
        // if no topic is given, we just seach for any topic
        if (topic != null) {
            questionsPage = questionRepository.findRandomQuestionExcludingCompletedWithMinDifficultyLimit(completedQuestionIds, topic, difficulty, pageable);

        } else {
            questionsPage = questionRepository.findRandomQuestionExcludingCompletedWithMinDifficultyLimit(completedQuestionIds, difficulty, pageable);
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
    // TODO split this function to enable resuablilty of core functioanlity (find random question and shuffle answers)
    // todo this bleongs somewhere else, quizstate seems off here
    // todo need to implement function to convert to dto
    public QuestionGameEvent createQuestionGameEvent(Question question, QuizState quizState) {
        // TODO check where to add exception for findRandomQuestion (e.g. there are no questions left)
        // we use this datatype and not questionDto because we do not want to reveal real true answer to the client

        logger.info("Creating QuestionGameEvent for question: {}", question.getId());

        // Prepare a list to hold the final answers (including the real one)
        List<Answer> finalAnswers = new ArrayList<>();

        // Add the correct answer
        finalAnswers.add(question.getCorrectAnswer());

        // Create a copy of the mock answers list to shuffle
        List<MockAnswer> mockAnswersCopy = new ArrayList<>(question.getMockAnswers());

        Collections.shuffle(mockAnswersCopy);  // Shuffle the copied list

        // Add up to 3 mock answers from the shuffled copy
        for (int i = 0; i < Math.min(3, mockAnswersCopy.size()); i++) {
            finalAnswers.add(mockAnswersCopy.get(i));
        }

        // Shuffle the final list of answers (real + selected mock answers)
        Collections.shuffle(finalAnswers);

        logger.info("Created QuestionGameEvent for question: {} ", question.getId(), "with answers: {}", finalAnswers);

        // Return the question with the shuffled answers
        return new QuestionGameEvent(quizState, question.getId(), question.getQuestionText(), finalAnswers);
    }


}

package com.example.quiz.model;

import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class QuizState {
	// Class that is used to track the state of a quiz. Will be stored in tomcats session context.

    // List of all questions in the quiz
    private List<Question> allQuestions;
    
    // Index of the current question
    private int currentQuestionIndex;

    // Set to store IDs of questions that have already been answered
    private Set<Long> completedQuestionIds;

    // Current score of the quiz
    private int score;
    
    // TODO think about this. i want to be able to flexibly change out questions for a given degree of diffuclty via a joker.
    // So either load spare question set or retrieve the questions "live" and do not make premade quiz set.
    
    // Constructor to initialize a new quiz
    public QuizState() {
        this.allQuestions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
    }
    
    // Constructor to initialize a new quiz with a list of questions
    public QuizState(List<Question> questions) {
        this.allQuestions = questions;
        this.currentQuestionIndex = 0;
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
    }
    
    public void addQuestion(Question question) {
    	allQuestions.add(question);
    }
    
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < allQuestions.size()) {
            return allQuestions.get(currentQuestionIndex);
        }
        return null;  // No more questions
    }

    public void incrementQuestionIndex() {
        currentQuestionIndex++;
    }

    public boolean isCompleted(Long questionId) {
        return completedQuestionIds.contains(questionId);
    }

    public void markQuestionAsCompleted(Long questionId) {
        completedQuestionIds.add(questionId);
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public boolean hasMoreQuestions() {
        return currentQuestionIndex < allQuestions.size();
    }
}


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
    
    private int currentRound;
    
    // TODO think about this. i want to be able to flexibly change out questions for a given degree of diffuclty via a joker.
    // So either load spare question set or retrieve the questions "live" and do not make premade quiz set.
    
    // Constructor to initialize a new quiz
    public QuizState() {
        this.allQuestions = new ArrayList<>();
        this.currentQuestionIndex = -1; //TODO  as long as there is no question we keep this -1 because we increase everytime we generate a new question. so this always points to the current question.
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1; 
    }
    
    // Constructor to initialize a new quiz with a list of questions
    public QuizState(List<Question> questions) {
        this.allQuestions = questions;
        this.currentQuestionIndex = 0;
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1;
    }
    
    public List<Question> getAllQuestions() {
		return allQuestions;
	}

	public void setAllQuestions(List<Question> allQuestions) {
		this.allQuestions = allQuestions;
	}
    
    
    public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}

	public void setCurrentQuestionIndex(int currentQuestionIndex) {
		this.currentQuestionIndex = currentQuestionIndex;
	}
	    

    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score; 
    }
    
    public Set<Long> getCompletedQuestionIds() {
		return completedQuestionIds;
	}

	public void setCompletedQuestionIds(Set<Long> completedQuestionIds) {
		this.completedQuestionIds = completedQuestionIds;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}
}


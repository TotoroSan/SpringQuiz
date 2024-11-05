package com.example.quiz.model.entity;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;

import java.util.HashSet;

@Entity 
public class QuizState implements Serializable {
	// Class that is used to track the state of a quiz. Will be stored in tomcats session context.
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	

	// User Id the QuizState is associated with 
    @Column(nullable = false)
    private Long userId;
    
	// List of all questions in the quiz 
    //(ManyToMany because QuizState can have multiple questions and each question can appear in multiple QuizStates)
    // OrderColumn because we need to retreive the List in the same order we saved it.
    @ManyToMany(fetch = FetchType.LAZY)
    @OrderColumn(name = "question_order")
    private List<Question> allQuestions;
     
	// Index of the current question
    private int currentQuestionIndex;

	// Set to store IDs of questions that have already been answered
    @ElementCollection
    private Set<Long> completedQuestionIds;

	// Current score of the quiz
    private int score;
    
    private int currentRound;
    
    // TODO think about this. i want to be able to flexibly change out questions for a given degree of diffuclty via a joker.
    // So either load spare question set or retrieve the questions "live" and do not make premade quiz set.
    
    // Standard Constructor for JPA reflection
    public QuizState() {
    }
    
    
    // Constructor to initialize a new quiz
    public QuizState(Long userId) {
    	this.userId = userId;
        this.allQuestions = new ArrayList<>();
        this.currentQuestionIndex = -1; //TODO  as long as there is no question we keep this -1 because we increase everytime we generate a new question. so this always points to the current question.
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1; 
    }
    
    // Constructor to initialize a new quiz with a list of questions
    public QuizState(Long userId, List<Question> questions) {
    	this.userId = userId; 
        this.allQuestions = questions;
        this.currentQuestionIndex = 0;
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1;
    }
    
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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


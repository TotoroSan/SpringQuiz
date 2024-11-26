package com.example.quiz.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private double score;
    
    private int currentRound;

	// a segment is a series of questions (an effect choice constitutes the end of a segment)
	private int currentSegment;

	// number of (correctly) answered questions in the current segment
	private int answeredQuestionsInSegment;



	// flag that indicates if the GameState is active (meaning the game hasnt ended)
	private boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_modifier_id", referencedColumnName = "id")
    private QuizModifier quizModifier;

	// todo add timestamp here

    // TODO think about this. i want to be able to flexibly change out questions for a given degree of diffuclty via a joker.
    // So either load spare question set or retrieve the questions "live" and do not make premade quiz set.
    
    // Standard Constructor for JPA reflection
    public QuizState() {
    }


	// Todo consolidate constructors (builder or factory?)
    // Constructor to initialize a new quiz
    public QuizState(Long userId) {
    	this.userId = userId;
        this.allQuestions = new ArrayList<>();
        this.currentQuestionIndex = -1; //TODO  as long as there is no question we keep this -1 because we increase everytime we generate a new question. so this always points to the current question.
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1;
		this.currentSegment = 1;
		this.answeredQuestionsInSegment = 1;
		this.isActive = true;
        this.quizModifier = new QuizModifier(this); // initialize with standard quiz modifier
    }
    
    // Constructor to initialize a new quiz with a list of questions
    public QuizState(Long userId, List<Question> questions) {
    	this.userId = userId;
        this.allQuestions = questions;
        this.currentQuestionIndex = 0;
        this.completedQuestionIds = new HashSet<>();
        this.score = 0;  // Initialize score to 0
        this.currentRound = 1;
		this.currentSegment = 1;
		this.answeredQuestionsInSegment = 0;
		this.isActive = true;
        this.quizModifier = new QuizModifier(this); // initialize with standard quiz modifier
    }

	// Constructor to initialize a new quiz with a list of questions and existing a custom modifier
	public QuizState(Long userId, List<Question> questions, QuizModifier quizModifier) {
		this.userId = userId;
		this.allQuestions = questions;
		this.currentQuestionIndex = 0;
		this.completedQuestionIds = new HashSet<>();
		this.score = 0;  // Initialize score to 0
		this.currentRound = 1;
		this.currentSegment = 1;
		this.answeredQuestionsInSegment = 0;
		this.isActive = true;
		this.quizModifier = quizModifier; // initialize with standard quiz modifier
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
	    

    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
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




	public int getAnsweredQuestionsInSegment() {
		return answeredQuestionsInSegment;
	}

	public void setAnsweredQuestionsInSegment(int answeredQuestionsInSegment) {
		this.answeredQuestionsInSegment = answeredQuestionsInSegment;
	}


	public int getCurrentSegment() {
		return currentSegment;
	}

	public void setCurrentSegment(int currentSegment) {
		this.currentSegment = currentSegment;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public QuizModifier getQuizModifier() {
		return quizModifier;
	}
	public void setQuizModifier(QuizModifier quizModifier) {
		this.quizModifier = quizModifier;
	}
}


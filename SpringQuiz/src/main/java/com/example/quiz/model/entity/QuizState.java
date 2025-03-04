package com.example.quiz.model.entity;

import com.example.quiz.model.entity.Joker.Joker;
import com.example.quiz.model.enums.GameEventType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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

    //
    @Transient
    private Set<GameEventType> eventTypesOcurredInSegment = new HashSet<>();

    // flag that indicates if the GameState is active (meaning the game hasnt ended)
    private boolean isActive;

    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_modifier_id", referencedColumnName = "id")
    private QuizModifier quizModifier;

    // mappedBy quizState means that a GameEvent object is mapped to a QuizState object by the column "quizState"
    // in the gameEvents table
    @OneToMany(mappedBy = "quizState", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "event_order") // This ensures events are ordered by this column in the database
    private List<GameEvent> gameEvents;

    // map of bought and usable jokers TODO fix here if problems, maybe need to add ordering
    @OneToMany(mappedBy = "quizState", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "id") // Use the 'id' attribute of Joker as the map key
    private Map<UUID, Joker> ownedJokers = new HashMap<>();

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
        this.eventTypesOcurredInSegment = new HashSet<>();
        this.answeredQuestionsInSegment = 1;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.quizModifier = new QuizModifier(this); // initialize with standard quiz modifier
        this.gameEvents = new ArrayList<>();
        this.ownedJokers = new HashMap<UUID, Joker>();
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
        this.eventTypesOcurredInSegment = new HashSet<>();
        this.answeredQuestionsInSegment = 0;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.quizModifier = new QuizModifier(this); // initialize with standard quiz modifier
        this.gameEvents = new ArrayList<>();
        this.ownedJokers = new HashMap<UUID, Joker>();
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
        this.eventTypesOcurredInSegment = new HashSet<>();
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

    public Question getCurrentQuestion() {
        return allQuestions.get(currentQuestionIndex);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<GameEvent> getGameEvents() {
        return gameEvents;
    }

    public void setGameEvents(List<GameEvent> gameEvents) {
        this.gameEvents = gameEvents;
    }

    public void addGameEvent(GameEvent gameEvent) {
        this.gameEvents.add(gameEvent);
    }

    public GameEvent getLatestGameEvent() {
        return getGameEvents().get(this.gameEvents.size() - 1);
    }

    public void clearGameEvents() {
        this.gameEvents.clear();
    }

    public Map<UUID, Joker> getOwnedJokers() {
        return ownedJokers;
    }

    public void setOwnedJokers(Map<UUID, Joker> ownedJokers) {
        this.ownedJokers = ownedJokers;
    }




    public Set<GameEventType> getEventTypesOcurredInSegment() {
        return eventTypesOcurredInSegment;
    }

    public void setEventTypesOccurredInSegment(Set<GameEventType> eventsOccurredInSegment) {
        this.eventTypesOcurredInSegment = eventsOccurredInSegment;
    }

    public void addEventTypeToSegment(GameEventType eventType) {
        this.eventTypesOcurredInSegment.add(eventType);
    }

    public void clearSegmentEventTypes() {
        this.eventTypesOcurredInSegment.clear();
    }
    public boolean hasEventTypeOccurred(GameEventType eventType) {
        return eventTypesOcurredInSegment.contains(eventType);
    }
}


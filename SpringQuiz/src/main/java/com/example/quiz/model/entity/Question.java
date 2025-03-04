package com.example.quiz.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    // Quiz is currently not used 
//    @ManyToOne
//    @JoinColumn(name = "quiz_id")
//    @JsonBackReference // To avoid looping between Question and Quiz
//    private Quiz quiz;


    // Cascade the save operation to the real answer (i.e. if a question object is saved, the associated real answer object is also saved)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "correct_answer_id", referencedColumnName = "id")  // Separate column for correct answer
    @JsonManagedReference // To avoid looping between Question and Answer
    private CorrectAnswer correctAnswer;

    // question difficulty 1 (easiest) to 10
    private int difficulty;

    private String topic;

    // Reminder: we need this mock_answer_question_id because JPA otherwise throws correct answer and mock answer 
    // together since they are both associated with the same question id and are both the same data type.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //@JoinColumn(name = "mock_answer_question_id")  // Separate column for mock answers
    private List<MockAnswer> mockAnswers;

    // Constructors
    public Question() {
    }

    // Constructors
    public Question(String questionText) {
    }

    public Question(String questionText, Quiz quiz) {
        this.questionText = questionText;
        //this.quiz = quiz;
    }

    @Override
    public String toString() {
        return "Question Text: " + questionText;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

//    public Quiz getQuiz() {
//        return quiz;
//    }
//
//    public void setQuiz(Quiz quiz) {
//        this.quiz = quiz;
//    }

    public List<MockAnswer> getMockAnswers() {
        return mockAnswers;
    }

    public void setMockAnswers(List<MockAnswer> mockAnswers) {
        this.mockAnswers = mockAnswers;
    }

    public CorrectAnswer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(CorrectAnswer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


}

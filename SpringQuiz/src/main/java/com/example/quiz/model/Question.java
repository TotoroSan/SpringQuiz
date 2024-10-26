package com.example.quiz.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    // Cascade the save operation to the real answer (i.e. if a question object is saved, the associated real answer object is also saved)
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "real_answer_id")
    private Answer correctAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> mockAnswers;

    // Constructors
    public Question() {}

    public Question(String questionText, Quiz quiz) {
        this.questionText = questionText;
        this.quiz = quiz;
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

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Answer> getMockAnswers() {
        return mockAnswers;
    }

    public void setMockAnswers(List<Answer> answers) {
        this.mockAnswers = answers;
    }
    
    public Answer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Answer answer) {
        this.correctAnswer = answer;
    }
    
  
    
}

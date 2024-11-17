package com.example.quiz.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// this will be the class with "premade" quizzes. 
@Entity
public class Quiz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

//    @ManyToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference // to prevent infinite (question -> quiz -> questions -> quiz ) loop in answer json 
//    private List<Question> questions;
    

    
    // Constructors
    public Quiz() {}

    public Quiz(String title) {
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public List<Question> getQuestions() {
//        return questions;
//    }
//
//    public void setQuestions(List<Question> questions) {
//        this.questions = questions;
//    }
}

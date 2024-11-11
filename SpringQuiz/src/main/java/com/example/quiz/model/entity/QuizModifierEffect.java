package com.example.quiz.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

// this class represents a "Power-up" - some dynamic change to the QuizModifier
// it is not a "classic" jpa entity. specific rule subclasses just define a manipulation of the GameModifier.
// we only save the id of the active effects to the QuizModifier, not the full object 
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "effect_type")
public abstract class QuizModifierEffect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String idString; // TODO fix -> this is currently used to identy the effect subclass
    
    private String name;
    private int duration;  // The number of questions for which this modifier is active
    
    @ManyToOne
    private QuizModifier quizModifier;

    public QuizModifierEffect(String idString, String name, int duration) {
        this.idString = idString;
        this.name = name;
        this.duration = duration;
    }

    public abstract void apply(QuizModifier quizModifier);
	public abstract void reverse(QuizModifier quizModifier);
	
    public String getIdString() {
		return idString;
	}

	public void setIdString(String identifier) {
		this.idString = identifier;
	}

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }
    
    public QuizModifier getQuizModifier() {
        return quizModifier;
    }

    public void setQuizModifier(QuizModifier quizModifier) {
        this.quizModifier = quizModifier;
    }

    public void decrementDuration() {
        if (duration > 0) {
            duration--;
        }
    }




}

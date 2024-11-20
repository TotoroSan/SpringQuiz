package com.example.quiz.model.entity;

import jakarta.persistence.*;

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
    @JoinColumn(name = "quiz_modifier_id")
    private QuizModifier quizModifier;

    // Default constructor (required by JPA)
    protected QuizModifierEffect() {
    }

    // TODO this exists because we want to send a quizModifierDto that is  unrelated to a modifier
    public QuizModifierEffect(String idString, String name, int duration) {
        this.idString = idString;
        this.name = name;
        this.duration = duration;
    }

    public QuizModifierEffect(String idString, String name, int duration, QuizModifier quizModifier) {
        this.idString = idString;
        this.name = name;
        this.duration = duration;
        this.quizModifier = quizModifier;
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

    public void incrementDuration() {
        duration++;
    }




}

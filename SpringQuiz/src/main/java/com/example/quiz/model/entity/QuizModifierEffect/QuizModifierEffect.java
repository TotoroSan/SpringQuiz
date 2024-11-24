package com.example.quiz.model.entity.QuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
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
    private Integer duration;  // The number of questions for which this modifier is active  // duration of null means permanent effect



    private String description;
    private String type;
    private Boolean isPermanent;
    private Integer rarity;




    @ManyToOne
    @JoinColumn(name = "quiz_modifier_id")
    private QuizModifier quizModifier;


    // Default constructor (required by JPA)
    protected QuizModifierEffect() {
    }

    // TODO this exists because we want to send a quizModifierDto that is  unrelated to a modifier
    public QuizModifierEffect(String idString, String name, Integer duration) {
        this.idString = idString;
        this.name = name;
        this.duration = duration;
    }

    public QuizModifierEffect(String idString, String name, Integer duration, QuizModifier quizModifier, String description, String type, boolean isPermanent, Integer rarity) {
        this.idString = idString;
        this.name = name;
        this.duration = duration;
        this.quizModifier = quizModifier;
        this.isPermanent = isPermanent;
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

    public Integer getDuration() {
        return duration;
    }
    
    public QuizModifier getQuizModifier() {
        return quizModifier;
    }

    public void setQuizModifier(QuizModifier quizModifier) {
        this.quizModifier = quizModifier;
    }

    public Integer getRarity() {
        return rarity;
    }

    public void setRarity(Integer rarity) {
        this.rarity = rarity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void decrementDuration() {
        if (duration > 0) {
            duration--;
        }
    }
    public Boolean getPermanent() {
        return isPermanent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermanent(Boolean permanent) {
        isPermanent = permanent;
    }
    public void incrementDuration() {
        duration++;
    }




}

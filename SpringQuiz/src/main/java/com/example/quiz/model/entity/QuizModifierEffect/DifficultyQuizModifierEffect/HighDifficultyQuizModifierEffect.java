package com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect;

import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizModifierEffect.QuizModifierEffect;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HIGH_DIFFICULTY")
public class HighDifficultyQuizModifierEffect extends QuizModifierEffect {
    // Default constructor (required by JPA)
    protected HighDifficultyQuizModifierEffect() {
    }

    // TODO instantiate this and other classes with the meta data? this way duration can be set in the EffectFactory.
    // TODO its best to hardcode the inforamtion only in one place. instead of static attributes we chose the registry approach.
    public HighDifficultyQuizModifierEffect(QuizModifier quizModifier) {
        super("HIGH_DIFFICULTY", "High Difficulty", 3, quizModifier, "The next questions will have increased difficulty.", "topic", false, 1);
    }
    
    public HighDifficultyQuizModifierEffect(QuizModifier quizModifier, int duration) {
        super("HIGH_DIFFICULTY", "High Difficulty", duration, quizModifier, "The next questions will have increased difficulty.", "topic", false, 1);
    }

    @Override
    public void apply(QuizModifier quizModifier) {
        quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() + 1);
    }

	@Override
	public void reverse(QuizModifier quizModifier) {
		quizModifier.setDifficultyModifier(quizModifier.getDifficultyModifier() - 1);
	}
}
package com.example.quiz.model.entity.QuizModifierEffect;

// This is a factory class that serves the purpose of dynamically instantiating effect objects.
// It is needed, because the effects are instantiated after user request, so that only the chosen effect will be instantiated and subsequently saved.
// An alternative would be to have a "static" table with all effects in the database.

import com.example.quiz.model.entity.*;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.HighDifficultyQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.MaxDifficultyLimitQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.DifficultyQuizModifierEffect.MinDifficultyLimitQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect.DoubleScoreQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.ScoreQuizModifierEffect.QuadrupleScoreQuizModifierEffect;
import com.example.quiz.model.entity.QuizModifierEffect.TopicQuizModifierEffect.*;

import java.util.HashMap;
import java.util.Map;

// TODO every new effect needs to be added here
// this is not an entity -> no need to persist the factory. move later for clarity.

public class QuizModifierEffectFactory {
    // this registry is used to dynamically instantiate modifier classes based on user input
    private static final Map<String, Class<? extends QuizModifierEffect>> effectRegistry = new HashMap<>();

    // This registry holds static information about each subclass. It is used for presenting static effect information
    // to frontend without actually instantiating them (could also use static fields in modifer classes for this)
    private static final Map<String, QuizModifierEffectMetaData> quizModifierEffectMetadataRegistry = new HashMap<>();

    static {
        effectRegistry.put("DOUBLE_SCORE", DoubleScoreQuizModifierEffect.class);
        effectRegistry.put("QUADRUPLE_SCORE", QuadrupleScoreQuizModifierEffect.class);
        effectRegistry.put("HIGH_DIFFICULTY", HighDifficultyQuizModifierEffect.class);

        effectRegistry.put("MAX_DIFFICULTY_LIMIT", MaxDifficultyLimitQuizModifierEffect.class);
        effectRegistry.put("MIN_DIFFICULTY_LIMIT", MinDifficultyLimitQuizModifierEffect.class);

        // topic modifier effects
        effectRegistry.put("CHOOSE_TOPIC_ART", ArtTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_ASTRONOMY", AstronomyTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_BIOLOGY", BiologyTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_CHEMISTRY", ChemistryTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_ECONOMICS", EconomicsTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_GEOGRAPHY", GeographyTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_GEOLOGY", GeologyTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_HISTORY", HistoryTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_LINGUISTICS", LinguisticsTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_LITERATURE", LiteratureTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_MATHEMATICS", MathematicsTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_MEDICINE", MedicineTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_MUSIC", MusicTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_MYTHOLOGY", MythologyTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_PHYSICS", PhysicsTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_SCIENCE", ScienceTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_SPORTS", SportsTopicQuizModifierEffect.class);
        effectRegistry.put("CHOOSE_TOPIC_TECHNOLOGY", TechnologyTopicQuizModifierEffect.class);



        quizModifierEffectMetadataRegistry.put("DOUBLE_SCORE", new QuizModifierEffectMetaData("DOUBLE_SCORE", "Double Score", "The next questions give double score.", 3, "score"));
        quizModifierEffectMetadataRegistry.put("QUADRUPLE_SCORE", new QuizModifierEffectMetaData("QUADRUPLE_SCORE", "Quadruple Score", "The next questions give quadruple score.", 3, "score"));

        quizModifierEffectMetadataRegistry.put("HIGH_DIFFICULTY", new QuizModifierEffectMetaData("HIGH_DIFFICULTY", "High Difficulty", "The next questions will be of higher difficulty.", 3, "difficulty"));
        quizModifierEffectMetadataRegistry.put("MAX_DIFFICULTY_LIMIT", new QuizModifierEffectMetaData("MAX_DIFFICULTY_LIMIT", "Max Difficulty Limit", "The next questions will have a given max difficulty.", 3, "difficulty"));
        quizModifierEffectMetadataRegistry.put("MIN_DIFFICULTY_LIMIT", new QuizModifierEffectMetaData("MIN_DIFFICULTY_LIMIT", "Min Difficulty Limit", "The next questions will have a given min difficulty.", 3, "difficulty"));


        // topic modifier effects
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_ART", new QuizModifierEffectMetaData("CHOOSE_TOPIC_ART", "Choose Topic Art", "Allows you to choose Art as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_ASTRONOMY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_ASTRONOMY", "Choose Topic Astronomy", "Allows you to choose Astronomy as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_BIOLOGY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_BIOLOGY", "Choose Topic Biology", "Allows you to choose Biology as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_CHEMISTRY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_CHEMISTRY", "Choose Topic Chemistry", "Allows you to choose Chemistry as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_ECONOMICS", new QuizModifierEffectMetaData("CHOOSE_TOPIC_ECONOMICS", "Choose Topic Economics", "Allows you to choose Economics as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_GEOGRAPHY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_GEOGRAPHY", "Choose Topic Geography", "Allows you to choose Geography as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_GEOLOGY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_GEOLOGY", "Choose Topic Geology", "Allows you to choose Geology as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_HISTORY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_HISTORY", "Choose Topic History", "Allows you to choose History as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_LINGUISTICS", new QuizModifierEffectMetaData("CHOOSE_TOPIC_LINGUISTICS", "Choose Topic Linguistics", "Allows you to choose Linguistics as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_LITERATURE", new QuizModifierEffectMetaData("CHOOSE_TOPIC_LITERATURE", "Choose Topic Literature", "Allows you to choose Literature as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_MATHEMATICS", new QuizModifierEffectMetaData("CHOOSE_TOPIC_MATHEMATICS", "Choose Topic Mathematics", "Allows you to choose Mathematics as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_MEDICINE", new QuizModifierEffectMetaData("CHOOSE_TOPIC_MEDICINE", "Choose Topic Medicine", "Allows you to choose Medicine as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_MUSIC", new QuizModifierEffectMetaData("CHOOSE_TOPIC_MUSIC", "Choose Topic Music", "Allows you to choose Music as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_MYTHOLOGY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_MYTHOLOGY", "Choose Topic Mythology", "Allows you to choose Mythology as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_PHYSICS", new QuizModifierEffectMetaData("CHOOSE_TOPIC_PHYSICS", "Choose Topic Physics", "Allows you to choose Physics as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_SCIENCE", new QuizModifierEffectMetaData("CHOOSE_TOPIC_SCIENCE", "Choose Topic Science", "Allows you to choose Science as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_SPORTS", new QuizModifierEffectMetaData("CHOOSE_TOPIC_SPORTS", "Choose Topic Sports", "Allows you to choose Sports as the topic for the next questions.", 3, "topic"));
        quizModifierEffectMetadataRegistry.put("CHOOSE_TOPIC_TECHNOLOGY", new QuizModifierEffectMetaData("CHOOSE_TOPIC_TECHNOLOGY", "Choose Topic Technology", "Allows you to choose Technology as the topic for the next questions.", 3, "topic"));


    }

    public static Map<String, Class<? extends QuizModifierEffect>> getEffectRegistry() {
        return effectRegistry;
    }
    public static Map<String, QuizModifierEffectMetaData> getQuizModifierEffectMetadataRegistry() {
        return quizModifierEffectMetadataRegistry;
    }
    public static QuizModifierEffect createEffect(String effectId, int duration, QuizModifier quizModifier) {
        Class<? extends QuizModifierEffect> effectClass = effectRegistry.get(effectId);
        if (effectClass != null) {
            try {
                return effectClass.getConstructor(QuizModifier.class, int.class).newInstance(quizModifier, duration);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating effect: " + effectId, e);
            }
        }
        throw new IllegalArgumentException("Unknown effect ID: " + effectId);
    }


}

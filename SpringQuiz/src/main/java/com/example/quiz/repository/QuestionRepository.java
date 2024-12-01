package com.example.quiz.repository;

import com.example.quiz.model.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // You can add custom methods here for specific queries if needed

    // TODO get random Question+
    // Native query to get a random question
    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion();

    // Overloaded native query to get a random question within a given topic
    @Query(value = "SELECT * FROM question WHERE topic = :topic ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion(@Param("topic") String topic);

    // Overloaded native query to get a random question by difficulty
    @Query(value = "SELECT * FROM question WHERE difficulty = :difficulty ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomQuestion(@Param("difficulty") Integer difficulty);


    // Find questions by topic
    @Query("SELECT q FROM Question q WHERE q.topic = :topic")
    Page<Question> findQuestionsByTopic(@Param("topic") String topic, Pageable pageable);


    // cannot use Limit 1 here because it is a native query
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompleted(@Param("completedQuestionIds") Set<Long> completedQuestionIds, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.difficulty = :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompleted(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("difficulty") Integer difficulty, Pageable pageable);

    // Overloaded method to find a random question by excluding completed questions and matching topic
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.topic = :topic ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompleted(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("topic") String topic, Pageable pageable);

    // Overloaded method to find a random question by excluding completed questions, matching topic and difficulty
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.topic = :topic AND q.difficulty = :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompleted(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("topic") String topic, @Param("difficulty") Integer difficulty, Pageable pageable);


    // method to find a random question by excluding completed questions, matching topic and difficulty maximum. meaning it will draw a question with difficulty <= difficulty
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.difficulty <= :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompletedWithMaxDifficultyLimit(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("difficulty") Integer difficulty, Pageable pageable);

    // method to find a random question by excluding completed questions, matching topic and difficulty maximum. meaning it will draw a question with difficulty <= difficulty
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.topic = :topic AND q.difficulty <= :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompletedWithMaxDifficultyLimit(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("topic") String topic, @Param("difficulty") Integer difficulty, Pageable pageable);


    // method to find a random question by excluding completed questions, matching topic and difficulty minimum. meaning it will draw a question with difficulty >= difficulty
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.difficulty >= :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompletedWithMinDifficultyLimit(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("difficulty") Integer difficulty, Pageable pageable);

    // method to find a random question by excluding completed questions, matching topic and difficulty minimum. meaning it will draw a question with difficulty >= difficulty
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.topic = :topic AND q.difficulty >= :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompletedWithMinDifficultyLimit(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("topic") String topic, @Param("difficulty") Integer difficulty, Pageable pageable);

}








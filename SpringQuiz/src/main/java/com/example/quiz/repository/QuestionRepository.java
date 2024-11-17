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
    
    // cannot use Limit 1 here because it is a native query 
    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompleted(@Param("completedQuestionIds") Set<Long> completedQuestionIds, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.id NOT IN :completedQuestionIds AND q.difficulty = :difficulty ORDER BY FUNCTION('RAND')")
    Page<Question> findRandomQuestionExcludingCompletedAndDifficulty(@Param("completedQuestionIds") Set<Long> completedQuestionIds, @Param("difficulty") Integer difficulty, Pageable pageable);

}

package com.example.quiz.unitTest.service.user;
import com.example.quiz.exception.ResourceNotFoundException;
import com.example.quiz.model.entity.QuizSubmission;
import com.example.quiz.repository.QuizSubmissionRepository;
import com.example.quiz.service.admin.AdminQuizSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("test")
public class UserQuizSubmissionServiceTest {

    @Mock
    private QuizSubmissionRepository quizSubmissionRepository;

    @InjectMocks
    private AdminQuizSubmissionService adminQuizSubmissionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitQuiz() {
        // Arrange
        QuizSubmission submission = new QuizSubmission();
        submission.setScore(95);
        when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenReturn(submission);

        // Act
        QuizSubmission savedSubmission = adminQuizSubmissionService.submitQuiz(submission);

        // Assert
        assertEquals(95, savedSubmission.getScore());
        verify(quizSubmissionRepository, times(1)).save(submission);
    }

    @Test
    public void testGetSubmissionsByUserId() {
        // Arrange
        List<QuizSubmission> submissions = Arrays.asList(new QuizSubmission(), new QuizSubmission());
        when(quizSubmissionRepository.findByUserId(1L)).thenReturn(submissions);

        // Act
        List<QuizSubmission> result = adminQuizSubmissionService.getSubmissionsByUserId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(quizSubmissionRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetSubmissionById_NotFound() {
        // Arrange
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            adminQuizSubmissionService.getSubmissionById(1L);
        });
    }

    @Test
    public void testDeleteSubmission() {
        // Arrange
        QuizSubmission submission = new QuizSubmission();
        when(quizSubmissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        // Act
        adminQuizSubmissionService.deleteSubmission(1L);

        // Assert
        verify(quizSubmissionRepository, times(1)).delete(submission);
    }
}

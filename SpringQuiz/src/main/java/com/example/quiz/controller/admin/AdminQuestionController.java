package com.example.quiz.controller.admin;

import com.example.quiz.model.Question;
import com.example.quiz.model.QuestionDto;
import com.example.quiz.model.QuestionWithShuffledAnswersDto;
import com.example.quiz.service.admin.AdminQuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("admin/api/questions")
public class AdminQuestionController {

    @Autowired
    private AdminQuestionService adminQuestionService;

    // Get a question by ID TODO maybe change to optional return type because of possible null value or figure out how to handle this 
    @GetMapping("/{id}")
    public QuestionDto getQuestionById(@PathVariable Long id) {
        return adminQuestionService.getQuestionById(id);
    }
    
<<<<<<< HEAD
<<<<<<< HEAD
//    // Get a random question with shuffled answers including realAnswer and answers from the mock-answer pool
//    @GetMapping
//    public QuestionWithShuffledAnswersDto getRandomQuestionWithShuffledAnswers() {
//        return adminQuestionService.getQuestionWithShuffledAnswersDto();
//    }

=======
>>>>>>> parent of 1a2ebc3 (Update)
=======
>>>>>>> parent of 1a2ebc3 (Update)
    // Create a new question
    @PostMapping
    public QuestionDto createQuestion(@RequestBody QuestionDto questionDto) {
        return adminQuestionService.createQuestionFromDto(questionDto);
    }

    // Update a question
    @PutMapping("/{id}")
    public QuestionDto updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
        return adminQuestionService.updateQuestionFromDto(id, questionDto);
    }

    // Delete a question
    @DeleteMapping("/{id}")
    public void deleteQuestionById(@PathVariable Long id) {
        adminQuestionService.deleteQuestionById(id);
    }
    
}

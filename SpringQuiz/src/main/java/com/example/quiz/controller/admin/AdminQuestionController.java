package com.example.quiz.controller.admin;

import com.example.quiz.model.dto.QuestionDto;
import com.example.quiz.service.admin.AdminQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

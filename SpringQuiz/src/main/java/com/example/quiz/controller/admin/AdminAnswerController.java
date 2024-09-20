package com.example.quiz.controller.admin;


import com.example.quiz.model.Answer;
import com.example.quiz.service.admin.AdminAnswerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("admin/api/answers")
public class AdminAnswerController {

    @Autowired
    private AdminAnswerService adminAnswerService;

    // Get an answer by ID
    @GetMapping("/{id}")
    public Optional<Answer> getAnswerById(@PathVariable Long id) {
        return adminAnswerService.getAnswerById(id);
    }

    // Create a new answer
    @PostMapping
    public Answer createAnswer(@RequestBody Answer answer) {
        return adminAnswerService.createAnswer(answer);
    }

    // Update an answer
    @PutMapping("/{id}")
    public Answer updateAnswer(@PathVariable Long id, @RequestBody Answer answerDetails) {
        return adminAnswerService.updateAnswer(id, answerDetails);
    }

    // Delete an answer
    @DeleteMapping("/{id}")
    public void deleteAnswer(@PathVariable Long id) {
        adminAnswerService.deleteAnswer(id);
    }
}

package com.example.quiz.controller;


import com.example.quiz.model.Answer;
import com.example.quiz.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // Get an answer by ID
    @GetMapping("/{id}")
    public Optional<Answer> getAnswerById(@PathVariable Long id) {
        return answerService.getAnswerById(id);
    }

    // Create a new answer
    @PostMapping
    public Answer createAnswer(@RequestBody Answer answer) {
        return answerService.createAnswer(answer);
    }

    // Update an answer
    @PutMapping("/{id}")
    public Answer updateAnswer(@PathVariable Long id, @RequestBody Answer answerDetails) {
        return answerService.updateAnswer(id, answerDetails);
    }

    // Delete an answer
    @DeleteMapping("/{id}")
    public void deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
    }
}

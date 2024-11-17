package com.example.quiz.controller.user;

import com.example.quiz.service.user.UserQuizSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/api/submissions")
public class UserQuizSubmissionController {

    @Autowired
    private UserQuizSubmissionService userQuizSubmissionService;

    // TODO how does a user interact with quiz submissions?
    // This would be for creating user quizzes

}

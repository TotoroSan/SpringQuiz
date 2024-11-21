package com.example.quiz.controller.user;

import com.example.quiz.model.dto.LoginRequest;
import com.example.quiz.model.entity.JwtResponse;
import com.example.quiz.security.JwtTokenProvider;
import com.example.quiz.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/api/auth")
public class UserAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            logger.info("Authentication successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception ex) {
            logger.error("Authentication failed: {}", ex.getMessage());
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}

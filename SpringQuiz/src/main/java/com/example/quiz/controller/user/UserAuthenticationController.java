package com.example.quiz.controller.user;

import com.example.quiz.model.dto.LoginRequestDto;
import com.example.quiz.model.dto.JwtResponseDto;
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

    // generates a jwt token on successfull login and rerturns it
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto) {

        try {
            logger.info("Attempting to authenticate user: {}", loginRequestDto.getUsername());

            // this depends on the UserDetails that are specified in the security config
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(), // if we want to use email instead of username for authentication change here
                            loginRequestDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            logger.info("Authentication successful for user: {}", loginRequestDto.getUsername());
            return ResponseEntity.ok(new JwtResponseDto(jwt));
        } catch (Exception ex) {
            logger.error("Authentication failed: {}", ex.getMessage());
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }

    // todo logout

}

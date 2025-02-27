package com.example.quiz.service.user;

import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

// breaks with the naming convention (UserXyService) since we might switch this service to some other package
// TODO review if we should keep this service here
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        user.setRoles("ROLE_USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUserProfile(new UserProfile(user));

        return userRepository.save(user);
    }

}
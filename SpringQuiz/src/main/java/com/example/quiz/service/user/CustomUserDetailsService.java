package com.example.quiz.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//UserService will now be responsible for managing user information,
// while CustomUserDetailsService will handle the details needed specifically for authentication.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    // this function is convention to load user data via primary key (email at the moment)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities() // This should return the user's roles/authorities.
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}

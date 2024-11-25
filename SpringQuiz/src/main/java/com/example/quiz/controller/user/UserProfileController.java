package com.example.quiz.controller.user;
import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.service.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    // Get user profile by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        Optional<UserProfile> userProfile = userProfileService.findByUserId(userId);
        if (userProfile.isPresent()) {
            UserProfileDto userProfileDto = userProfileService.convertToDto(userProfile.get());
            return ResponseEntity.ok(userProfileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfileDto userProfileUpdates) {

        Optional<UserProfile> updatedProfile = userProfileService.updateUserProfile(userId, userProfileUpdates);
        if (updatedProfile.isPresent()) {
            UserProfileDto userProfileDto = userProfileService.convertToDto(updatedProfile.get());
            return ResponseEntity.ok(userProfileDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create user profile (if separate creation is allowed)
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDto> createUserProfile(@PathVariable Long userId, @RequestBody UserProfileDto userProfileDto) {
        Optional<UserProfile> createdProfile = userProfileService.createUserProfile(userId, userProfileDto);
        if (createdProfile.isPresent()) {
            return ResponseEntity.ok(userProfileService.convertToDto(createdProfile.get())); // the convertion back can be removed, its just better for debugging
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete user profile
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserProfile(@PathVariable Long userId) {
        boolean isDeleted = userProfileService.deleteUserProfile(userId);
        if (isDeleted) {
            return ResponseEntity.ok("User profile deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

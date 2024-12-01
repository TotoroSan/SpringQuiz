package com.example.quiz.controller.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.service.user.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("user/api/profile")
public class UserProfileController {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    // Get user profile by user ID
    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal User user) {
        logger.info("Received request to get userprofile for user Precracsh");
        logger.info("Received request to get userprofile for user: ", user.getId());

        Long userId = user.getId();
        Optional<UserProfile> userProfile = userProfileService.findByUserId(userId);
        if (userProfile.isPresent()) {
            UserProfileDto userProfileDto = userProfileService.convertToDto(userProfile.get());
            logger.info("Succsessfully processed get userprofile request for user: ", user.getUsername());
            return ResponseEntity.ok(userProfileDto);
        } else {
            logger.warn("No userprofile found for user: ", user.getId());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateUserProfile(@AuthenticationPrincipal User user, @RequestBody UserProfileDto userProfileUpdates) {
        logger.info("Received request to update userprofile for user: ", user.getUsername());

        Long userId = user.getId();
        Optional<UserProfile> updatedProfile = userProfileService.updateUserProfile(userId, userProfileUpdates);
        if (updatedProfile.isPresent()) {
            UserProfileDto userProfileDto = userProfileService.convertToDto(updatedProfile.get());
            logger.info("Succsessfully processed update userprofile request for user: ", user.getUsername());

            return ResponseEntity.ok(userProfileDto);
        } else {
            logger.warn("No userprofile found for user: ", user.getUsername());
            return ResponseEntity.notFound().build();
        }
    }

    // Create user profile (if separate creation is allowed)
    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(@AuthenticationPrincipal User user, @RequestBody UserProfileDto userProfileDto) {
        logger.info("Received request to create userprofile for user: ", user.getUsername());

        Long userId = user.getId();
        Optional<UserProfile> createdProfile = userProfileService.createUserProfile(userId, userProfileDto);
        if (createdProfile.isPresent()) {
            logger.info("Succsessfully created userprofile request for user: ", user.getUsername());
            return ResponseEntity.ok(userProfileService.convertToDto(createdProfile.get())); // the convertion back can be removed, its just better for debugging
        } else {
            logger.warn("No userprofile could be created for user: ", user.getUsername());
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete user profile
    @DeleteMapping
    public ResponseEntity<String> deleteUserProfile(@AuthenticationPrincipal User user) {
        logger.info("Received request to delete userprofile for user: ", user.getUsername());

        Long userId = user.getId();
        boolean isDeleted = userProfileService.deleteUserProfile(userId);
        if (isDeleted) {
            logger.info("Succsessfully deleted userprofile request for user: ", user.getUsername());
            return ResponseEntity.ok("User profile deleted successfully");
        } else {
            logger.warn("No userprofile could be deleted for user: ", user.getUsername());
            return ResponseEntity.notFound().build();
        }
    }
}

package com.example.quiz.controller.user;

import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.service.user.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    /**
     * Retrieves the user profile for the currently authenticated user.
     * If the user's profile exists, it is converted to UserProfileDto and returned.
     * Otherwise, a 404 response is issued.
     *
     * @param user The currently authenticated user
     * @return A ResponseEntity containing the user's profile (200) or 404 if not found
     */
    @Operation(
            summary = "Get user profile",
            description = """
        Fetches the UserProfile for the authenticated user from the database. 
        Returns a UserProfileDto on success, or 404 if the profile is not found.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "UserProfile found and returned successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No UserProfile found for the authenticated user",
            content = @Content
    )
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

    /**
     * Updates the user profile for the authenticated user based on the given UserProfileDto.
     * If the user's profile exists, it is updated and returned. If no profile is found,
     * a 404 (Not Found) response is returned.
     *
     * @param user               The currently authenticated user
     * @param userProfileUpdates A UserProfileDto containing updated profile fields
     * @return A ResponseEntity with the updated user profile (200 OK) or 404 if none is found
     */
    @Operation(
            summary = "Update user profile",
            description = """
        Updates the existing user profile for the authenticated user with the provided data. 
        If the profile is found and updated successfully, returns the updated UserProfileDto. 
        Otherwise, returns 404 if no profile is found.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "User profile updated successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No user profile found for the authenticated user",
            content = @Content
    )
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

    /**
     * Creates a new user profile for the authenticated user based on the given UserProfileDto.
     * If the profile is successfully created, the newly created UserProfileDto is returned.
     * Otherwise, a 400 (Bad Request) response is returned.
     *
     * @param user           The currently authenticated user
     * @param userProfileDto A DTO containing fields for the new user profile
     * @return A ResponseEntity with the created user profile (200 OK) or 400 if creation fails
     */
    @Operation(
            summary = "Create a new user profile",
            description = """
        Creates a user profile for the authenticated user using the provided data in UserProfileDto. 
        If creation is successful, returns the newly created UserProfileDto. 
        Otherwise, returns a 400 (Bad Request).
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "User profile created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "User profile could not be created",
            content = @Content
    )
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

    /**
     * Deletes the user profile of the currently authenticated user.
     * If the profile exists and is successfully deleted, returns a 200 (OK) with a success message.
     * Otherwise, returns 404 (Not Found) if no profile could be deleted.
     *
     * @param user The currently authenticated user
     * @return A ResponseEntity with a success message (200) or 404 if the profile is not found
     */
    @Operation(
            summary = "Delete user profile",
            description = """
        Removes the user profile associated with the authenticated user from the system. 
        Returns a success message if deletion succeeds, or 404 if the profile does not exist.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "User profile deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No user profile could be deleted (profile not found)",
            content = @Content
    )
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

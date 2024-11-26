package com.example.quiz.service.user;

import com.example.quiz.model.dto.QuizStateDto;
import com.example.quiz.model.dto.UserProfileDto;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.model.entity.UserProfile;
import com.example.quiz.repository.UserProfileRepository;
import com.example.quiz.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserProfile> findByUserId(Long userId) {
        return userRepository.findById(userId).map(User::getUserProfile);
    }

    public Optional<UserProfile> updateUserProfile(Long userId, UserProfileDto userProfileUpdates) {
        logger.info("Initiating update of user profile for userId: ", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfile userProfile = user.getUserProfile();
            if (userProfile != null) {
                logger.info("UserProfile found");
                userProfile.setFirstName(userProfileUpdates.getFirstName());
                userProfile.setLastName(userProfileUpdates.getLastName());
                userProfile.setDateOfBirth(userProfileUpdates.getDateOfBirth());
                userProfile.setAddress(userProfileUpdates.getAddress());
                userProfile.setPhoneNumber(userProfileUpdates.getPhoneNumber());
                userProfile.setProfilePictureUrl(userProfileUpdates.getProfilePictureUrl());
                userProfile.setBio(userProfileUpdates.getBio());
                userProfile.setSocialMediaLinks(userProfileUpdates.getSocialMediaLinks());
                userRepository.save(user);
                return Optional.of(userProfile);
            }
        }

        logger.info("No profile found");
        return Optional.empty();
    }

    public Optional<UserProfile> createUserProfile(Long userId, UserProfileDto userProfileDto) {
        logger.info("Creating profile for userId: ", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // create user profile from dto
            UserProfile userProfile = new UserProfile(user,userProfileDto.getFirstName(), userProfileDto.getLastName(),
                    userProfileDto.getDateOfBirth(), userProfileDto.getAddress(), userProfileDto.getPhoneNumber(), userProfileDto.getEmail(),
                    userProfileDto.getProfilePictureUrl(), userProfileDto.getBio(), userProfileDto.getSocialMediaLinks());

            userProfile.setUser(user);
            user.setUserProfile(userProfile);
            userRepository.save(user);
            logger.info("Successfully created and saved profile");
            return Optional.of(userProfile);
        }

        logger.info("User was not found");
        return Optional.empty();
    }

    public boolean deleteUserProfile(Long userId) {
        logger.info("Deleting profile of userId: ", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && userOptional.get().getUserProfile() != null) {
            userProfileRepository.delete(userOptional.get().getUserProfile());
            userRepository.save(userOptional.get());
            logger.info("Successfully deleted profile of userId: ", userId);
            return true;
        }
        logger.info("User with userId: ", userId, " not found");
        return false;
    }

    public UserProfileDto convertToDto(UserProfile userProfile) {
        logger.info("Converting userProfile to userProfileDto");
        // Convert to DTO to return to the user
        UserProfileDto userProfileDto = new UserProfileDto(userProfile.getId(), userProfile.getUser().getId(), userProfile.getFirstName(), userProfile.getLastName(),
                userProfile.getDateOfBirth(), userProfile.getAddress(), userProfile.getPhoneNumber(), userProfile.getUser().getEmail(),
                userProfile.getProfilePictureUrl(), userProfile.getBio(), userProfile.getSocialMediaLinks());

        logger.debug("Successfully converted userProfile to userProfileDto");
        return userProfileDto;
    }

}

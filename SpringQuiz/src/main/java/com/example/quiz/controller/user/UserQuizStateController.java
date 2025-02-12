package com.example.quiz.controller.user;


import com.example.quiz.model.dto.*;
import com.example.quiz.model.entity.GameEvent;
import com.example.quiz.model.entity.QuizModifier;
import com.example.quiz.model.entity.QuizState;
import com.example.quiz.model.entity.User;
import com.example.quiz.service.user.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// having the same URI for different actions (like create, update, and delete) but distinguishing them by the HTTP method (POST, PUT, DELETE, etc.) is indeed the best practice in RESTful API design.

@RestController
@RequestMapping("user/api/quiz")
@Tag(name = "QuizState", description = "Endpoints for everything related to the user's quiz state")
public class UserQuizStateController {
    private static final Logger logger = LoggerFactory.getLogger(UserQuizStateController.class);

    // this is the controller for quiz management and users accessing session data

    // we will route requests to different controllers to keep separation of concern.
    // we will update the session data from different controllers, so we can update in one go.
    // for later: (its possible to first confirm question correctness and then update with second requesst to have centralized place for session)

    @Autowired
    private UserQuizStateService userQuizStateService;

    @Autowired
    private UserQuizModifierService userQuizModifierService;

    @Autowired
    private UserQuestionService userQuestionService;

    @Autowired
    private UserGameEventService userGameEventService;

    @Autowired
    private UserJokerService userJokerService;

    // TODO how does a user interact with quizzes?

    // Start a new quiz
    @GetMapping("/start")
    public ResponseEntity<String> startQuiz(HttpSession session, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        QuizState quizState = userQuizStateService.startNewQuiz(userId);
        session.setAttribute("quizState", quizState); // we use session as a "hot storage" for QuizState
        return ResponseEntity.ok("Quiz started!");
    }

    // Load the last active Quiz if there is one
    @GetMapping("/load")
    public ResponseEntity<QuizSaveDto> loadLastActiveQuiz(HttpSession session, @AuthenticationPrincipal User user) throws JsonProcessingException {
        logger.info("Received request to load the last active quiz for user ID: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestActiveQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("No active quiz state found for user ID: {}", userId);
            return ResponseEntity.noContent().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);

        QuizSaveDto quizSaveDto = userQuizStateService.createQuizSaveDto(quizState);

        if (quizSaveDto == null) {
            logger.info("Loaded QuizState was null or invalid");
            return ResponseEntity.noContent().build();
        }

        logger.info("Loaded QuizSave with ID: {} for user ID: {}", quizState.getId(), userId);
        logger.info("QuizSaveDto content: {}", quizSaveDto); // here the .toString() method of quizSaveDto is implicetly called

        return ResponseEntity.ok(quizSaveDto);
    }


    // Get QuizState
    /*
     * @AuthenticationPrincipal annotation is used to directly inject the currently authenticated user into a method parameter.
     * Specifically, it extracts the user details from the authentication token, which means the authenticated user's information
     * is available for use without the need to manually parse the JWT or session.
     * */
    @GetMapping("/state")
    public ResponseEntity<QuizStateDto> getQuizState(HttpSession session, @AuthenticationPrincipal User user) {
        logger.info("Received request to get QuizState for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);


        QuizStateDto quizStateDto = userQuizStateService.convertToDto(quizState);

        logger.debug("Successfully retreived QuizState");
        return ResponseEntity.ok(quizStateDto);
    }

    // Endpoint to handle next game event (either a question or modifier effects)
    @GetMapping("/nextGameEvent")
    public ResponseEntity<GameEventDto> getNextGameEvent(HttpSession session, @AuthenticationPrincipal User user) {
        logger.info("Received request to get next game event for user ID: {}", user.getId());


        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        // todo should this raise an exception?
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        session.setAttribute("quizState", quizState);

        // prevent providing GameEvents if game has ended
        if (!quizState.isActive()) {
            logger.warn("Quiz state is not active! This Quiz has ended");
            return ResponseEntity.badRequest().build();
        }


        // quizState get next gameevent
        logger.info("Requesting nextGameEvent");
        GameEvent nextGameEvent = userQuizStateService.getNextGameEvent(quizState);


        // convert game event to dto here (the nextGameEvent is a subclass -> polymorphism causes converToDto to be applied for the subclass parameter (eg. QuestionGameEvent)
        // polymorphic serializatiuon is also supported by jackson, that is why we don't need to cast the subclass specifically.
        logger.info("Requesting conversion of nextGameEvent to nextGameEventDto");
        GameEventDto nextGameEventDto = userGameEventService.convertToDto(nextGameEvent);


        return ResponseEntity.ok(nextGameEventDto);

    }


    // Endpoint to get random QuizModifierEffects to present to the user
    // TODO currently not in use
    @GetMapping("/modifiers/getrandom")
    public ResponseEntity<List<QuizModifierEffectDto>> getRandomModifiers() {
        List<QuizModifierEffectDto> randomQuizModifierEffects = userQuizModifierService.pickRandomModifierEffectDtos();
        return ResponseEntity.ok(randomQuizModifierEffects);
    }

    // Endpoint to apply the chosen modifier effect to the modifier of the gamestate
    @PostMapping("/modifiers/apply")
    public ResponseEntity<String> applyModifier(HttpSession session, @RequestBody QuizModifierEffectDto quizModifierEffectDto, @AuthenticationPrincipal User user) {
        logger.info("Received request to apply chosen QuizModifierEffect for user ID: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().body("Quiz state not found");
        }

        QuizState quizState = optionalQuizState.get();
        QuizModifier quizModifier = quizState.getQuizModifier();

        // function would throw an exception if something is not valid

        boolean effectIsApplied = userQuizStateService.validateAndApplyModifierEffect(quizState, quizModifierEffectDto.getUuid());

        if (effectIsApplied) {
            userQuizStateService.moveToNextSegment(quizState);
            session.setAttribute("quizState", quizState);
            userQuizStateService.saveQuizState(quizState); // TODO move saving logic to service (?)
            logger.info("Successfully applied chosen QuizModifierEffect");
            return ResponseEntity.ok("Modifier applied successfully");
        } else {
            logger.error("Failed to apply chosen QuizModifierEffect");
            return ResponseEntity.badRequest().body("Failed to apply modifier");
        }
    }


    @GetMapping("/modifiers/getactive")
    public ResponseEntity<List<QuizModifierEffectDto>> getActiveQuizModifierDtos(@AuthenticationPrincipal User user) {
        logger.info("Received request to get ActiveQuizModifierDtos for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();
        List<QuizModifierEffectDto> activeQuizModifierEffects = userQuizModifierService.getActiveModifierEffectDtos(quizState.getQuizModifier());

        logger.info("Successfully retreived ActiveQuizModifierDtos for user: {}", user.getId());

        return ResponseEntity.ok(activeQuizModifierEffects);
    }

    // TODO this is an example of how we use swagger annotations for documentation of user facing endpoints
    /**
     * Purchases a Joker by receiving a JokerDto from the client.
     * Validates that the Joker is available in the current ShopGameEvent and
     * ensures the user has enough currency. On success, adds the Joker to the user's active jokers.
     */
    @Operation(
            summary = "Purchase a Joker",
            description = """
            Deducts the Joker's cost from the QuizState's coin balance and adds the Joker 
            to the user's active jokers if valid. The JokerDto is sent in the request body.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Joker purchased successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Purchase failed (insufficient funds, invalid Joker, or no shop event)",
            content = @Content
    )
    @PostMapping("/jokers/purchase")
    public ResponseEntity<String> purchaseJoker(
            @AuthenticationPrincipal User user,
            HttpSession session,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The Joker being purchased, containing at least its idString and possibly a tier."
            )
            @RequestBody JokerDto jokerDto
    ) {
        logger.info("User {} requested to purchase Joker with idString={}", user.getId(), jokerDto.getIdString());

        // 1) Retrieve the user's latest QuizState
        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);
        if (optionalQuizState.isEmpty()) {
            logger.warn("No active quiz state found for user ID: {}", userId);
            return ResponseEntity.badRequest().body("No active quiz state found.");
        }
        QuizState quizState = optionalQuizState.get();

        // 2) Invoke service to purchase
        boolean purchased = userJokerService.purchaseJoker(quizState, jokerDto.getIdString(), jokerDto.getTier());
        if (!purchased) {
            logger.warn("Failed to purchase Joker '{}' for user ID {}", jokerDto.getIdString(), userId);
            return ResponseEntity.badRequest().body("Could not purchase Joker (invalid ID, insufficient funds, or no shop event).");
        }

        // 3) Update session, return success
        session.setAttribute("quizState", quizState);
        logger.info("Joker '{}' purchased successfully by user {}", jokerDto.getIdString(), userId);
        return ResponseEntity.ok("Joker purchased successfully!");
    }

    /**
     * Uses (applies) a Joker from the user's active jokers by receiving a JokerDto in the request body.
     * We expect the JokerDto to contain at least the DB ID (e.g., jokerDto.getId()) to identify
     * which Joker to use. The service call decrements uses or removes the Joker if it has zero uses left.
     */
    @Operation(
            summary = "Use a Joker",
            description = """
            Applies the effect of a Joker (identified by its DB ID) that the user already owns. 
            If the Joker has multiple uses, it is decremented; if it only has one use left, 
            it is removed from active jokers.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Joker used successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Failed to use Joker (invalid ID or no uses left)",
            content = @Content
    )
    @PostMapping("/jokers/use")
    public ResponseEntity<String> useJoker(
            HttpSession session,
            @AuthenticationPrincipal User user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "JokerDto with at least the DB ID of the Joker to use"
            )
            @RequestBody JokerDto jokerDto
    ) {
        logger.info("User {} requested to use a Joker with DB ID {}", user.getId(), jokerDto.getUuid());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);

        if (optionalQuizState.isEmpty()) {
            logger.warn("No active quiz state found for user ID: {}", userId);
            return ResponseEntity.badRequest().body("No active quiz state found.");
        }

        QuizState quizState = optionalQuizState.get();
        // The JokerDto might have other fields, but we primarily need jokerDto.getId()
        UUID jokerObjectId = jokerDto.getUuid();
        if (jokerObjectId == null) {
            logger.warn("JokerDto provided no DB ID (id) for the Joker.");
            return ResponseEntity.badRequest().body("JokerDto must include a valid 'id' field.");
        }

        // Attempt to use the Joker
        boolean success = userJokerService.useJoker(quizState, jokerObjectId);
        if (!success) {
            logger.warn("Failed to use Joker with DB ID {} for user ID {}", jokerObjectId, userId);
            return ResponseEntity.badRequest().body("Failed to use Joker (invalid ID or no uses left).");
        }

        // Update session and return success
        session.setAttribute("quizState", quizState);
        logger.info("Joker with DB ID {} used successfully by user {}", jokerObjectId, userId);
        return ResponseEntity.ok("Joker used successfully!");
    }


    /**
     * Returns a list of all active jokers for the quizstate of the user.
    */

    @Operation(
            summary = "Get Active Jokers",
            description = "Retrieves all JokerDto objects for jokers currently owned by the user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active jokers"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Quiz state not found for the user",
            content = @Content
    )
    @GetMapping("/jokers/active")
    public ResponseEntity<List<JokerDto>> getActiveJokers(
            @AuthenticationPrincipal User user
    ) {
        logger.info("Received request to get active jokers for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();

        // Convert active jokers in the quizState to JokerDto
        List<JokerDto> activeJokers = userJokerService.getActiveJokerDtos(quizState);

        logger.info("Successfully retrieved {} active jokers for user ID: {}", activeJokers.size(), userId);
        return ResponseEntity.ok(activeJokers);
    }
}

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


    // TODO this is an example of how we use swagger for external documentation of public endpoints for users
    //  and javadoc for internal documentation for development
    /**
     * Starts a new quiz session for the authenticated user, storing the new QuizState in the session.
     * This endpoint returns a simple success message indicating that the quiz has started.
     *
     * @param session The HTTP session used for storing the newly created QuizState
     * @param user    The currently authenticated user
     * @return A ResponseEntity containing a success message
     */
    @Operation(
            summary = "Start a new quiz",
            description = """
        Initializes a new QuizState for the authenticated user,
        saves it in the HTTP session, and returns a confirmation message.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Quiz started successfully"
    )
    @GetMapping("/start")
    public ResponseEntity<String> startQuiz(HttpSession session, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        QuizState quizState = userQuizStateService.startNewQuiz(userId);
        session.setAttribute("quizState", quizState); // we use session as a "hot storage" for QuizState
        return ResponseEntity.ok("Quiz started!");
    }

    /**
     * Loads the last active quiz state for the authenticated user,
     * converts it to a QuizSaveDto, and returns it.
     * If no active quiz is found or the loaded QuizState is invalid,
     * a 204 (No Content) response is returned.
     *
     * @param session The HTTP session for storing the loaded QuizState
     * @param user    The authenticated user
     * @return A ResponseEntity containing a QuizSaveDto if found, otherwise 204 No Content
     * @throws JsonProcessingException if there's an issue serializing the quiz state
     */
    @Operation(
            summary = "Load the last active quiz",
            description = """
        Retrieves the most recent active QuizState for the user, if one exists.
        Converts it to a QuizSaveDto and returns it. If none is found or if the 
        QuizState is invalid, a 204 No Content response is issued.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully loaded the last active quiz"
    )
    @ApiResponse(
            responseCode = "204",
            description = "No active quiz found or invalid quiz state",
            content = @Content
    )
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


    /**
     * Retrieves the current QuizState for the authenticated user and converts it into a QuizStateDto.
     * If no QuizState is found, returns a 400 Bad Request.
     *
     * @param session The HTTP session for storing the retrieved QuizState
     * @param user    The currently authenticated user
     * @return A ResponseEntity containing the QuizStateDto, or 400 if no QuizState is found
     */
    @Operation(
            summary = "Get current QuizState",
            description = """
        Fetches the latest QuizState for the authenticated user and converts it into
        a QuizStateDto. If no QuizState is found, returns a 400 (Bad Request).
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the current QuizState"
    )
    @ApiResponse(
            responseCode = "400",
            description = "QuizState not found for the user",
            content = @Content
    )
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

    /**
     * Retrieves the next game event for the authenticated user's current quiz state.
     * This event can be a QuestionGameEvent, a ModifierEffectsGameEvent, or a ShopGameEvent,
     * depending on the quiz logic. If the quiz has ended or no quiz state is found, returns a 400 error.
     *
     * @param session The HTTP session used to store the updated QuizState
     * @param user    The currently authenticated user
     * @return A ResponseEntity containing the next GameEventDto
     */
    @Operation(
            summary = "Get next game event",
            description = """
        Retrieves and returns the next game event for the user's quiz session. 
        The event could be a question, modifier selection, or shop event, based 
        on the quiz progression logic. If the quiz is not active or no quiz state 
        is found, a 400 Bad Request is returned.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the next GameEvent",
            content = @Content
    )
    @ApiResponse(
            responseCode = "400",
            description = "No active quiz state found or the quiz has ended",
            content = @Content
    )
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


    /**
     * Applies a chosen modifier effect to the current QuizState based on the provided QuizModifierEffectDto.
     * Validates and instantiates the effect using its UUID, then moves the quiz forward if successful.
     * Returns 400 if the quiz state is not found or the effect cannot be applied.
     *
     * @param session                 The HTTP session where the QuizState is stored
     * @param quizModifierEffectDto   The DTO representing the chosen modifier effect
     * @param user                    The authenticated user
     * @return A ResponseEntity with a success message (200 OK) or an error (400 Bad Request)
     */
    @Operation(
            summary = "Apply a chosen modifier effect",
            description = """
        Takes a QuizModifierEffectDto containing an effect UUID, validates and applies it 
        to the user's current quiz state. If valid, the quiz state is advanced to the next segment.
        If invalid, a 400 Bad Request response is returned.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Modifier applied successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Failed to apply modifier (no quiz state found or invalid effect)",
            content = @Content
    )
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
            //userQuizStateService.moveToNextSegment(quizState); // TODO deprecreated we move to next segment after trader event, not here
            userQuizStateService.incrementAnsweredQuestionsInSegment(quizState); // todo temporary for debugging
            session.setAttribute("quizState", quizState);
            userQuizStateService.saveQuizState(quizState); // TODO move saving logic to service (?)
            logger.info("Successfully applied chosen QuizModifierEffect");
            return ResponseEntity.ok("Modifier applied successfully");
        } else {
            logger.error("Failed to apply chosen QuizModifierEffect");
            return ResponseEntity.badRequest().body("Failed to apply modifier");
        }
    }


    /**
     * Retrieves all currently active modifier effects for the authenticated user's quiz state
     * and returns them as a list of QuizModifierEffectDto. If no quiz state is found, returns 400.
     *
     * @param user The authenticated user
     * @return A ResponseEntity containing the list of active modifier effect DTOs,
     *         or 400 if no quiz state is found
     */
    @Operation(
            summary = "Get active modifier effects",
            description = """
        Fetches the latest quiz state for the user and returns all active modifier effects 
        (wrapped in QuizModifierEffectDto) that are currently applied to the quiz.
        Returns 400 if no quiz state is available.
        """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active modifier effects"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Quiz state not found for the user",
            content = @Content
    )
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
    @GetMapping("/jokers/owned")
    public ResponseEntity<List<JokerDto>> getOwnedJokers(
            @AuthenticationPrincipal User user
    ) {
        logger.info("Received request to get owned jokers for user: {}", user.getId());

        Long userId = user.getId();
        Optional<QuizState> optionalQuizState = userQuizStateService.getLatestQuizStateByUserId(userId);
        if (optionalQuizState.isEmpty()) {
            logger.warn("Quiz state not found for user ID: {}", userId);
            return ResponseEntity.badRequest().build();
        }

        QuizState quizState = optionalQuizState.get();

        // Convert active jokers in the quizState to JokerDto
        List<JokerDto> ownedJokers = userJokerService.getOwnedJokerDtos(quizState);

        logger.info("Successfully retrieved {} active jokers for user ID: {}", ownedJokers.size(), userId);
        return ResponseEntity.ok(ownedJokers);
    }
}

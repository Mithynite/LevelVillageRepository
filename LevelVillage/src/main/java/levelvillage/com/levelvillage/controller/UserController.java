package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.config.ConfigManager;
import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.dto.UserDTO;
import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.PostRepository;
import levelvillage.com.levelvillage.service.PostService;
import levelvillage.com.levelvillage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* This class manages API requests for Login and Sign Up, and other user related stuff
* @author Jakub Hofman
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // TODO změnit
public class UserController {

    private final UserService userService;
    private final int maxUsernameCharLength;
    private final int maxUserBioCharLength;
    private final PostRepository postRepository;

    @Autowired
    public UserController(UserService userService, PostRepository postRepository, PostService postService) {
        this.userService = userService;
        this.maxUsernameCharLength = ConfigManager.maxUsernameCharLength;
        this.maxUserBioCharLength = ConfigManager.maxUserBioCharLength;
        this.postRepository = postRepository;
    }

    /**
 * Validates the JWT token provided in the request path.
 *
 * @param token The JWT token to be validated.
 *
 * @return A ResponseEntity containing a status code and a message.
 *         - If the token is valid, the status code is HttpStatus.OK (200) and the message is "Token is valid".
 *         - If the token is invalid or expired, the status code is HttpStatus.UNAUTHORIZED (401) and the message is "Invalid or expired token".
 *
 * The function uses the userService to check the validity of the token.
 * If the token is valid, it returns a ResponseEntity with an OK status code and a success message.
 * If the token is invalid or expired, it returns a ResponseEntity with an UNAUTHORIZED status code and an error message.
 */
@GetMapping("/validate/{token}")
public ResponseEntity<String> validateUsersJWTToken(@PathVariable String token) {
    try {
        boolean valid = userService.isTokenValid(token);
        return valid
                ? ResponseEntity.ok("Token is valid")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
    }
}

    /**
 * Registers a new user with the provided User object.
 *
 * @param user The User object containing the username, email, and password for the new user.
 *
 * @return A ResponseEntity containing a status code and a message.
 *         - If the username, bio, or contact information is invalid, the status code is HttpStatus.BAD_REQUEST (400) and the message indicates the validation failure reason.
 *         - If the user registration is successful, the status code is HttpStatus.CREATED (201) and the message is "User registered successfully!".
 *         - If an error occurs during the user registration process, the status code is HttpStatus.INTERNAL_SERVER_ERROR (500) and the message is "User registration failed!".
 *
 * The function first validates the username, bio, and contact information using the validateUserInput method.
 * If any validation fails, the function returns a ResponseEntity with the appropriate status code and message.
 * If the validation passes, the function proceeds to register the new user using the userService's registerUser method.
 * The function then returns a ResponseEntity with the appropriate status code and message.
 */
@PostMapping("/signup")
public ResponseEntity<String> registerNewUser(@RequestBody User user) {
    ResponseEntity<String> validation = validateUserInput(user.getUsername(), user.getBio());
    if (validation != null) return validation;

    try {
        userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed!");
    }
}

    /**
 * Handles user login requests.
 *
 * @param userLoginRequest The User object containing the username and password for authentication.
 *
 * @return A ResponseEntity containing the JWT token, expiration time, username, and a success message.
 *         - If the authentication is successful, the status code is HttpStatus.OK (200) and the response body contains the JWT token, expiration time, username, and a success message.
 *         - If the authentication fails due to invalid credentials, the status code is HttpStatus.UNAUTHORIZED (401) and the response body contains the error message.
 *         - If an unexpected error occurs during the authentication process, the status code is HttpStatus.INTERNAL_SERVER_ERROR (500) and the response body contains the error message.
 */
@PostMapping("/login")
public ResponseEntity<?> loginUser(@RequestBody User userLoginRequest) {
    try {
        User user = userService.findUserByUsername(userLoginRequest.getUsername());
        String token = userService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
        long expirationTime = userService.getTokenExpiration(token);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "expiration", expirationTime,
                "username", user.getUsername(),
                "message", "Login successful"
        ));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}

    /**
 * Retrieves the user profile for the specified username.
 *
 * @param username The username of the user whose profile should be retrieved.
 * @param userDetails The UserDetails object representing the authenticated user.
 *
 * @return A ResponseEntity containing the UserDTO object representing the user's profile.
 *         - If the user is not authenticated, the status code is HttpStatus.UNAUTHORIZED (401) and the response body is empty.
 *         - If the user is not found, the status code is HttpStatus.NOT_FOUND (404) and the response body is empty.
 *         - If the user profile is successfully retrieved, the status code is HttpStatus.OK (200) and the response body contains the UserDTO object.
 */
@GetMapping("users/{username}/profile")
public ResponseEntity<UserDTO> getUserProfile(
        @PathVariable String username,
        @AuthenticationPrincipal UserDetails userDetails) {

    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User currentUser = userService.findUserByUsername(username);
    if (currentUser == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    UserDTO userDTO = new UserDTO(
            currentUser.getId(),
            currentUser.getUsername(),
            currentUser.getEmail(),
            currentUser.getBio(),
            currentUser.getDiscord(),
            currentUser.getInstagram(),
            currentUser.getLinkedin(),
            currentUser.getLikedPosts().stream().map(Post::getId).toList()
    );

    return ResponseEntity.ok(userDTO);
}

    /**
 * Updates the user's profile with the provided UserDTO object.
 *
 * @param userDetails The UserDetails object representing the authenticated user.
 * @param username The username of the user whose profile should be updated.
 * @param userDTO The UserDTO object containing the updated user information.
 *
 * @return A ResponseEntity with a status code and message.
 *         - If the user is not authenticated, the status code is HttpStatus.UNAUTHORIZED (401) and the message is "You must be logged in to update your profile."
 *         - If the authenticated user is not the owner of the profile, the status code is HttpStatus.FORBIDDEN (403) and the message is "You can only update your own profile."
 *         - If the username or bio length exceeds the maximum allowed length, the status code is HttpStatus.BAD_REQUEST (400) and the message indicates the validation failure reason.
 *         - If the contact information format is invalid, the status code is HttpStatus.BAD_REQUEST (400) and the message is "Invalid contact information format."
 *         - If the update is successful, the status code is HttpStatus.OK (200) and the message is "User's profile updated successfully."
 *         - If an error occurs during the update process, the status code is HttpStatus.INTERNAL_SERVER_ERROR (500) and the message is "An unexpected error occurred while updating the profile."
 */
@PutMapping("users/{username}/profile")
public ResponseEntity<String> updateUserProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable String username,
        @RequestBody UserDTO userDTO) {

    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to update your profile.");
    }

    if (!userDetails.getUsername().equals(username)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own profile.");
    }

    ResponseEntity<String> validation = validateUserInput(userDTO.getUsername(), userDTO.getBio());
    if (validation != null) return validation;

    if (!isContactInfoValid(userDTO)) {
        return ResponseEntity.badRequest().body("Invalid contact information format.");
    }

    try {
        userService.updateUserProfile(userDetails.getUsername(), userDTO);
        return ResponseEntity.ok("User's profile updated successfully");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while updating the profile.");
    }
}

        /**
     * Updates the list of posts liked by the user with the given username.
     *
     * @param username The username of the user whose liked posts should be updated.
     * @param postIds A list of post IDs representing the new liked posts.
     *
     * @return A ResponseEntity with a status code and message.
     *         - If the user is found, the status code is HttpStatus.OK (200) and the message is "User's liked posts updated successfully."
     *         - If the user is not found, the status code is HttpStatus.NOT_FOUND (404) and the response body is empty.
     *
     * The function uses the userService to find the user with the given username.
     * If the user is found, it updates the list of liked posts using the userService's updateLikedPosts method.
     * The function then returns a ResponseEntity with an OK status code and a success message.
     * If the user is not found, it returns a ResponseEntity with a NOT_FOUND status code and an empty response body.
     */
    @PutMapping("users/{username}/liked-posts")
    public ResponseEntity<String> updateUserLikedPosts(@PathVariable String username, @RequestBody List<Long> postIds) {
        User currentUser = userService.findUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.updateLikedPosts(currentUser.getId(), postIds);
        return ResponseEntity.ok("User's liked posts updated successfully.");
    }

    /**
 * Retrieves a list of posts liked by the user with the given username.
 *
 * @param username The username of the user whose liked posts should be retrieved.
 * @return A ResponseEntity containing a list of PostDTO objects representing the user's liked posts.
 *         The HTTP status code is set to HttpStatus.OK (200) if the operation is successful.
 *
 * The function uses the userService to find the user with the given username.
 * It then retrieves the list of posts liked by the user and maps each Post entity to a PostDTO object.
 * The list of PostDTO objects is returned in the response body.
 *
 * If the username does not correspond to any user, the function returns an empty list.
 * If an error occurs during the retrieval process, the function returns an HTTP status code of HttpStatus.INTERNAL_SERVER_ERROR (500).
 */
@GetMapping("users/{username}/liked-posts")
public ResponseEntity<List<PostDTO>> getUserLikedPosts(@PathVariable String username) {
    User user = userService.findUserByUsername(username);
    List<PostDTO> likedPosts = user.getLikedPosts().stream()
            .map(PostDTO::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(likedPosts);
}

    /**
 * Retrieves a list of posts created by the user with the given username.
 *
 * @param username The username of the user whose posts should be retrieved.
 * @return A ResponseEntity containing a list of PostDTO objects representing the user's posts.
 *         The HTTP status code is set to HttpStatus.OK (200) if the operation is successful.
 *
 * The function uses the postRepository to find all posts created by the user with the given username.
 * It then maps each Post entity to a PostDTO object and returns the list of PostDTO objects in the response body.
 *
 * If the username does not correspond to any user, the function returns an empty list.
 * If an error occurs during the retrieval process, the function returns an HTTP status code of HttpStatus.INTERNAL_SERVER_ERROR (500).
 */
@GetMapping("/users/{username}/posts")
public ResponseEntity<List<PostDTO>> getPostsByUsername(@PathVariable String username) {
    List<PostDTO> posts = postRepository.findByUserUsername(username);
    return ResponseEntity.ok(posts);
}

    /**
 * Validates the username and bio length for user registration and profile updates.
 *
 * @param username The username to be validated.
 * @param bio The bio to be validated.
 *
 * @return A ResponseEntity with a status code and message if the validation fails.
 *         Returns null if the validation passes.
 *
 * The validation rules are as follows:
 * - Username: Cannot exceed the maximum length defined in the ConfigManager.
 * - Bio: Cannot exceed the maximum length defined in the ConfigManager.
 *
 * If the validation fails, the ResponseEntity will contain a status code of HttpStatus.BAD_REQUEST
 * and a message indicating the validation failure reason.
 */
private ResponseEntity<String> validateUserInput(String username, String bio) {
    if (username != null && username.length() > maxUsernameCharLength) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Username cannot exceed " + maxUsernameCharLength + " characters.");
    }
    if (bio != null && bio.length() > maxUserBioCharLength) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Bio cannot exceed " + maxUserBioCharLength + " characters.");
    }
    return null;
}

        /**
     * Validates the contact information provided in the UserDTO object.
     *
     * @param userDTO The UserDTO object containing the contact information to be validated.
     * @return True if the contact information is valid, false otherwise.
     *
     * The validation rules are as follows:
     * - Discord: Must be null, empty, or match the pattern "^.{3,32}#\d{4}$".
     * - Instagram: Must be null, empty, or match the pattern "^https://(www\.)?instagram\.com/.*$".
     * - LinkedIn: Must be null, empty, or match the pattern "^https://(www\.)?linkedin\.com/.*$".
     */
    private boolean isContactInfoValid(UserDTO userDTO) {
        String discord = userDTO.getDiscord();
        String instagram = userDTO.getInstagram();
        String linkedIn = userDTO.getLinkedIn();

        return (discord == null || discord.isBlank() || discord.matches("^.{3,32}#\\d{4}$")) &&
                (instagram == null || instagram.isBlank() || instagram.matches("^https://(www\\.)?instagram\\.com/.*$")) &&
                (linkedIn == null || linkedIn.isBlank() || linkedIn.matches("^https://(www\\.)?linkedin\\.com/.*$"));
    }
}



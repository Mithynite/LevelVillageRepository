package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.dto.UserDTO;
import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.Skill;
import levelvillage.com.levelvillage.model.User;
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

/*
This class manages API requests for Login and Sign Up, and other user related stuff
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") //TODO změnit
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to register a new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) {
        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User userLoginRequest) {
        try {
            User user = userService.findUserByUsername(userLoginRequest.getUsername());
            String token = userService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
            long expirationTime = userService.getTokenExpiration(token); // Extract the expiration time from the token to later send it to Frontend
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "expiration", expirationTime, // Return expiration time as timestamp
                    "username", user.getUsername(), // Including the user's id so that he can use it to obtain certain info about his profile
                    "message", "Login successful"
            ));
        } catch (IllegalArgumentException e) {
            // Return 401 for authentication errors
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("users/{username}/profile")
    public ResponseEntity<UserDTO> getUserProfile(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // Unauthorized if no user is logged in
        }

        User currentUser = userService.findUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if user is not found
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

        return ResponseEntity.ok(userDTO);  // Return the requested user profile
    }
    
    @PutMapping("users/{username}/profile")
    public ResponseEntity<String> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String username,
            @RequestBody UserDTO userDTO) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to update your profile.");
        }

        // Ensure the user is only updating their own profile
        if (!userDetails.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own profile.");
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

    // Update (just) user's liked posts
    @PutMapping("users/{username}/liked-posts")
    public ResponseEntity<String> updateUserLikedPosts(@PathVariable String username, @RequestBody List<Long> postIds) {
        User currentUser = userService.findUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.updateLikedPosts(currentUser.getId(), postIds);
        return ResponseEntity.ok("User's liked posts updated successfully.");
    }

    @GetMapping("users/{username}/liked-posts")
    public ResponseEntity<List<PostDTO>> getUserLikedPosts(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        List<PostDTO> likedPosts = user.getLikedPosts().stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
        System.out.println(likedPosts);
        return ResponseEntity.ok(likedPosts);
    }
}


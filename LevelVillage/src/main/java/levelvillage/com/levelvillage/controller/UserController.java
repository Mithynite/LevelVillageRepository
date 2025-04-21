package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.config.ConfigManager;
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
@CrossOrigin(origins = "http://localhost:5173") // TODO změnit
public class UserController {

    private final UserService userService;
    private final int maxUsernameCharLength;
    private final int maxUserBioCharLength;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.maxUsernameCharLength = ConfigManager.maxUsernameCharLength;
        this.maxUserBioCharLength = ConfigManager.maxUserBioCharLength;
    }

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
        return ResponseEntity.ok(likedPosts);
    }

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

    private boolean isContactInfoValid(UserDTO userDTO) {
        String discord = userDTO.getDiscord();
        String instagram = userDTO.getInstagram();
        String linkedIn = userDTO.getLinkedIn();

        return (discord == null || discord.isBlank() || discord.matches("^.{3,32}#\\d{4}$")) &&
                (instagram == null || instagram.isBlank() || instagram.matches("^https://(www\\.)?instagram\\.com/.*$")) &&
                (linkedIn == null || linkedIn.isBlank() || linkedIn.matches("^https://(www\\.)?linkedin\\.com/.*$"));
    }
}



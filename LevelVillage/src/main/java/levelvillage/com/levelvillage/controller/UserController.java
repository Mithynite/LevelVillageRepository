package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.dto.UserDTO;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
This class manages API requests for Login and Sign Up, and other user realated stuff
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
            String token = userService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
            long expirationTime = userService.getTokenExpiration(token); // Extract the expiration time from the token to later send it to Frontend
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "expiration", expirationTime, // Return expiration time as timestamp
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

    @GetMapping("users/profile")
    public ResponseEntity<UserDTO> loginUser(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) userService.findUserByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(userDTO);
    }

}


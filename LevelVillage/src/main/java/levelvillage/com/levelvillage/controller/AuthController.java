package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.service.AuthService;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*
This class manages API requests for Login and Sign Up
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") //TODO změnit
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to register a new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) {
        try {
            authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
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
            String token = authService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
            long expirationTime = authService.getTokenExpiration(token); // Extract the expiration time from the token to later send it to Frontend
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
}


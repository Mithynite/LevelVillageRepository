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
    /*private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenUtil tokenUtil;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> registerNewUser(@RequestBody User user) {
        try{
            authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed!!!");
        }
    }

    @PostMapping("/login") // FrontEnd will send POST request to this backend
    public ResponseEntity<String> loginUser(@RequestBody User userLoginRequest) {
        var user = authService.authenticateTheUser(userLoginRequest.getUsername(), userLoginRequest.getPassword());
        if(user.isPresent()) { // checks if a valid user was found (exists?)
            // Generate JWT token
            String token = tokenUtil.generateToken(user.get().getUsername());
            Map<String, Object> response = Map.of(
                    "message", "User logged in successfully!",
                    "token", token
            );
            return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully!");
        } else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!!!");
        }
    }*/

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

    /**
     * Endpoint to log in a user and return a JWT token.
     */
    /*@PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User userLoginRequest) {
        try {
            String token = authService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User logged in successfully!");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Login failed!"));
        }
    }*/
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User userLoginRequest) {
        try {
            String token = authService.authenticateAndGenerateToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());
            return ResponseEntity.ok(Map.of("token", token, "message", "Login successful"));
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


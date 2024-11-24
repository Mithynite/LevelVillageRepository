package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.UserRepository;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    public AuthService(UserRepository userRepository, JWTTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(); // Initialize BCryptPasswordEncoder
    }

    // Register a new user with encrypted password
    /*public User registerUser(String username, String email, String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, email, encodedPassword);
        return userRepository.save(user);
    }*/

    public User registerUser(String username, String email, String password) {
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        // Check if email already exists
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailExists) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        // Save the new user with the encoded password
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, email, encodedPassword);
        return userRepository.save(user);
    }

    // Authenticate a user by comparing the raw password with the hashed password in the database
    /*public Optional<User> authenticateTheUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> bCryptPasswordEncoder.matches(password, user.getPassword()));
    }*/

    public String authenticateAndGenerateToken(String username, String password) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        // Check if the password matches
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }
        // Generate and return JWT token
        return jwtTokenUtil.generateToken(user.getUsername());
    }

}



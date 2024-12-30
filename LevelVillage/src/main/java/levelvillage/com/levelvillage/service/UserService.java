package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.UserRepository;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, JWTTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Spring Security's method for loading a user by username during authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user in the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Return Spring Security's User object with username, password, and authorities (roles)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // Use an empty list if no roles/authorities are implemented
        );
    }

    /**
     * Retrieve the custom User entity by username for application-level logic.
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Authenticate a user and generate a JWT token.
     */
    public String authenticateAndGenerateToken(String username, String password) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        // Check if the password matches
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        // Generate and return the JWT token
        return jwtTokenUtil.generateToken(user.getUsername());
    }

    /**
     * Register a new user with encrypted password.
     */
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

    /**
     * Retrieve the token expiration time in milliseconds.
     */
    public long getTokenExpiration(String token) {
        return jwtTokenUtil.extractExpiration(token);
    }
}

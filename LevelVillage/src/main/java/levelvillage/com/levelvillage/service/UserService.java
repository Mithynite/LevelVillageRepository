package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.dto.UserDTO;
import levelvillage.com.levelvillage.model.*;
import levelvillage.com.levelvillage.repository.*;
import levelvillage.com.levelvillage.util.JWTTokenUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import io.jsonwebtoken.*;

/**
 * Service class for managing user-related operations.
 * @author Jakub Hofman
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserLikedPostRepository userLikedPostRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, UserLikedPostRepository userLikedPostRepository, JWTTokenUtil jwtTokenUtil, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.userLikedPostRepository = userLikedPostRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.postRepository = postRepository;
    }

    /**
 * Validates the given JWT token.
 *
 * @param token The JWT token to validate.
 *
 * @return True if the token is valid, false otherwise.
 *
 * @throws JwtException If the token is invalid or cannot be parsed.
 * @throws IllegalArgumentException If the token is null or empty.
 */
public boolean isTokenValid(String token) {
    try {
        String username = jwtTokenUtil.extractUsername(token);
        return jwtTokenUtil.validateToken(token, username);
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}


    /**
 * Loads a user by their username for Spring Security authentication.
 *
 * @param username The username of the user to load.
 *
 * @return The UserDetails object representing the loaded user.
 *
 * @throws UsernameNotFoundException If no user is found with the given username.
 */
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.emptyList()
    );
}

    /**
 * Finds a user by their username.
 *
 * @param username The username of the user to find.
 *
 * @return The User object with the given username. If no user is found,
 *         a UsernameNotFoundException is thrown with an appropriate error message.
 *
 * @throws UsernameNotFoundException If no user is found with the given username.
 */
public User findUserByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
}

    /**
 * Authenticates the user with the provided username and password, and generates a JWT token.
 *
 * @param username The username of the user attempting to authenticate.
 * @param password The password provided by the user.
 *
 * @return A JWT token that can be used for subsequent authenticated requests.
 *
 * @throws IllegalArgumentException If the provided username or password is invalid.
 */
public String authenticateAndGenerateToken(String username, String password) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

    if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("Invalid username or password!");
    }

    return jwtTokenUtil.generateToken(user.getUsername());
}

        /**
     * Registers a new user with the provided username, email, and password.
     *
     * @param username The username of the new user. Must be unique.
     * @param email The email address of the new user. Must be unique.
     * @param password The password for the new user.
     *
     * @return The newly registered User object.
     *
     * @throws IllegalArgumentException If the username or email is already taken.
     */
    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailExists) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        User user = new User(username, email, encodedPassword);
        return userRepository.save(user);
    }

    public long getTokenExpiration(String token) {
        return jwtTokenUtil.extractExpiration(token);
    }

        /**
     * Updates the user profile with the provided information.
     *
     * @param username The username of the user whose profile needs to be updated.
     * @param userDTO An object containing the new user information.
     *
     * @return The updated User object.
     *
     * @throws IllegalArgumentException If the user with the given username is not found.
     *
     * @Transactional This method is transactional, meaning that it will be executed
     *                 within a database transaction. If any part of the method fails,
     *                 the transaction will be rolled back, ensuring data integrity.
     */
    @Transactional
    public User updateUserProfile(String username, UserDTO userDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        if (userDTO.getDiscord() != null) {
            user.setDiscord(userDTO.getDiscord());
        }
        if (userDTO.getInstagram() != null) {
            user.setInstagram(userDTO.getInstagram());
        }
        if (userDTO.getLinkedIn() != null) {
            user.setLinkedin(userDTO.getLinkedIn());
        }

        return userRepository.save(user); // Save the updated user profile
    }

        /**
     * Updates the list of liked posts for a given user.
     *
     * @param userId The ID of the user whose liked posts need to be updated.
     * @param newLikedPostIds A list of IDs representing the new liked posts.
     *
     * @throws IllegalArgumentException If the user with the given ID is not found,
     *                                  or if a post with a given ID is not found.
     *
     * @Transactional This method is transactional, meaning that it will be executed
     *                 within a database transaction. If any part of the method fails,
     *                 the transaction will be rolled back, ensuring data integrity.
     */
    @Transactional
    public void updateLikedPosts(Long userId, List<Long> newLikedPostIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        // Get current liked posts
        List<UserLikedPost> currentLikedPosts = userLikedPostRepository.findByUserId(user.getId());

        // Convert to a list of post IDs
        List<Long> currentLikedPostIds = currentLikedPosts.stream()
                .map(lp -> lp.getPost().getId())
                .collect(Collectors.toList());

        // Find posts to remove (unliked posts)
        List<UserLikedPost> postsToRemove = currentLikedPosts.stream()
                .filter(lp -> !newLikedPostIds.contains(lp.getPost().getId()))
                .collect(Collectors.toList());

        // Find posts to add (newly liked posts)
        List<Long> postsToAdd = newLikedPostIds.stream()
                .filter(id -> !currentLikedPostIds.contains(id))
                .collect(Collectors.toList());

        // Remove unliked posts
        userLikedPostRepository.deleteAll(postsToRemove);

        // Add new liked posts
        List<UserLikedPost> newUserLikedPosts = postsToAdd.stream()
                .map(postId -> {
                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new IllegalArgumentException("Post with ID: " + postId + " not found!"));
                    return new UserLikedPost(user, post);
                }).collect(Collectors.toList());

        userLikedPostRepository.saveAll(newUserLikedPosts);
    }

}


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

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SkillRepository skillRepository;
    private final UserLikedPostRepository userLikedPostRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, SkillRepository skillRepository, UserLikedPostRepository userLikedPostRepository, JWTTokenUtil jwtTokenUtil, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userLikedPostRepository = userLikedPostRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.postRepository = postRepository;
    }

    public boolean isTokenValid(String token) {
        try {
            String username = jwtTokenUtil.extractUsername(token);
            return jwtTokenUtil.validateToken(token, username);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


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

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public String authenticateAndGenerateToken(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        return jwtTokenUtil.generateToken(user.getUsername());
    }

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


package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.config.ConfigManager;
import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.model.*;
import levelvillage.com.levelvillage.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for handling CRUD operations related to posts.
 *
 * @author Jakub Hofman
 */
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://138.3.255.133:80", allowedHeaders = "*", allowCredentials = "true") //TODO změnit
public class PostController {
    private final PostService postService;
    private final int maxPostTitleCharLength;
    private final int maxPostDescriptionCharLength;

    /**
     * Constructor for PostController.
     *
     * @param postService The service for handling post-related operations.
     */
    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
        this.maxPostTitleCharLength = ConfigManager.maxPostTitleCharLength;
        this.maxPostDescriptionCharLength = ConfigManager.maxPostDescriptionCharLength;
    }

    /**
     * Validates the input for creating or updating a post.
     *
     * @param title       The title of the post.
     * @param description The description of the post.
     * @return A ResponseEntity with a status and message if the input is invalid. Otherwise, returns null.
     */
    private ResponseEntity<String> validatePostInput(String title, String description) {
        if (title == null || description == null || title.trim().isEmpty() || description.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and description cannot be empty!");
        }

        if (title.trim().length() > maxPostTitleCharLength) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title cannot exceed 100 characters.");
        }

        if (description.trim().length() > maxPostDescriptionCharLength) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description cannot exceed 1000 characters.");
        }

        return null;
    }

    /**
    * Retrieves a list of all posts.
    *
    * @return A list of PostDTO objects representing all posts.
    */
    @GetMapping
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

        /**
     * Creates a new post.
     *
     * @param post The DTO object representing the new post to be created.
     *             The DTO should contain the title, description, and skills of the post.
     *
     * @return A ResponseEntity with a status code and a message.
     *         - If the post is successfully created, the status code is 200 (OK) and the message is "Post created successfully!".
     *         - If the post data is invalid (e.g., title or description is empty or exceeds the maximum length),
     *           the status code is 400 (Bad Request) and the message contains the specific validation error.
     *         - If an error occurs while creating the post, the status code is 500 (Internal Server Error)
     *           and the message is "An error occurred while creating the post!".
     */
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostDTO post) {
        try {
            ResponseEntity<String> validationResponse = validatePostInput(post.getTitle(), post.getDescription());
            if (validationResponse != null) return validationResponse;

            Post savedPost = postService.createPost(post);
            return ResponseEntity.ok("Post created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the post!");
        }
    }

        /**
     * Retrieves a post by its unique identifier.
     *
     * @param id The unique identifier of the post to be retrieved.
     * @return A ResponseEntity containing the requested PostDTO if the post exists.
     *         If the post does not exist, the status code is 400 (Bad Request) and the response body is empty.
     *         If the post exists, the status code is 200 (OK) and the response body contains the PostDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {

        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();  // Unauthorized if no user is logged in
        }

        PostDTO postDTO = new PostDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getTitle(),
                post.getDescription(),
                post.getCreated_at(),
                post.getSkills().stream().map(Skill::getId).toList()
        );

        return ResponseEntity.ok(postDTO); // Return DTO instead of full entity
    }

    /**
 * Checks if the logged-in user is the owner of the post with the given ID.
 *
 * @param id The unique identifier of the post to be checked.
 * @param principal The principal object representing the logged-in user.
 *
 * @return A ResponseEntity containing a string indicating the ownership status.
 *         - If the logged-in user is the owner of the post, the response body contains "owner".
 *         - If the logged-in user is not the owner of the post, the response body contains "not_owner".
 *         - If the post with the given ID does not exist, the status code is 400 (Bad Request) and the response body is empty.
 */
@GetMapping("/{id}/check-ownership")
public ResponseEntity<String> checkPostOwnership(@PathVariable Long id, Principal principal) {
    Post post = postService.getPostById(id);

    // Compare the logged-in user with the post owner
    if (post.getUser().getUsername().equals(principal.getName())) {
        return ResponseEntity.ok("owner");
    } else {
        return ResponseEntity.ok("not_owner");
    }
}

    /**
 * Updates an existing post with the provided data.
 *
 * @param id The unique identifier of the post to be updated.
 * @param updatedPost The DTO object containing the updated data for the post.
 *                    The DTO should contain the title, description, and skills of the post.
 *
 * @return A ResponseEntity with a status code and a message.
 *         - If the post data is valid and the post is successfully updated,
 *           the status code is 200 (OK) and the message is "Post updated successfully".
 *         - If the post data is invalid (e.g., title or description is empty or exceeds the maximum length),
 *           the status code is 400 (Bad Request) and the message contains the specific validation error.
 *         - If the post with the given ID does not exist, the status code is 404 (Not Found)
 *           and the message is "Post not found!".
 *         - If an error occurs while updating the post, the status code is 500 (Internal Server Error)
 *           and the message is "An error occurred while updating the post!".
 */
@PutMapping("/{id}")
public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody PostDTO updatedPost) {
    if (updatedPost == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post data cannot be null!");
    }

    ResponseEntity<String> validationResponse = validatePostInput(updatedPost.getTitle(), updatedPost.getDescription());
    if (validationResponse != null) return validationResponse;

    try {
        updatedPost.setId(id);
        postService.updatePost(updatedPost);
        return ResponseEntity.ok("Post updated successfully");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the post!");
    }
}

    /**
 * Deletes a post by its unique identifier.
 *
 * @param id The unique identifier of the post to be deleted.
 *
 * @throws IllegalArgumentException If the post with the given ID does not exist.
 */
@DeleteMapping("/{id}")
public void deletePostById(@PathVariable Long id) {
    postService.deletePostById(id);
}

}

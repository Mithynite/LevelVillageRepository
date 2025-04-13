package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.model.*;
import levelvillage.com.levelvillage.repository.UserRepository;
import levelvillage.com.levelvillage.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true") //TODO změnit
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }
    // Get all posts
    @GetMapping
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostDTO post) {
        try {
            if(post.getDescription().trim().isEmpty() || post.getTitle().trim().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and description cannot be empty!");
            }

            Post savedPost = postService.createPost(post);
            return ResponseEntity.ok("Post created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the post!");
        }
    }

    // Get a post by ID
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

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody PostDTO updatedPost) {
        if(updatedPost != null){
            try {
                if(updatedPost.getDescription().trim().isEmpty() || updatedPost.getTitle().trim().isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and description cannot be empty!");
                }

                updatedPost.setId(id);

                postService.updatePost(updatedPost);
                return ResponseEntity.ok("Post updated successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the post!");
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Post data cannot be null!");
        }
    }

    // Delete a post by ID
    @DeleteMapping("/{id}")
    public void deletePostById(@PathVariable Long id) {
        postService.deletePostById(id);
    }

}

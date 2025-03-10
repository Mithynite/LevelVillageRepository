package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.User;
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
    private final UserRepository userRepository;

    @Autowired
    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }
    // Get all posts
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        try {
            if(post.getDescription().trim().isEmpty() || post.getTitle().trim().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and description cannot be empty!");
            }
            String username = post.getUser().getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            post.setUser(user);
            Post savedPost = postService.createPost(post);
            return ResponseEntity.ok("Post created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the post!");
        }
    }

    // Get a post by ID
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
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
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        if(updatedPost != null){
            try {
                if(updatedPost.getDescription().trim().isEmpty() || updatedPost.getTitle().trim().isEmpty()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and description cannot be empty!");
                }
                Post postToEdit = postService.getPostById(id);
                postToEdit.setTitle(updatedPost.getTitle());
                postToEdit.setDescription(updatedPost.getDescription());

                postService.updatePost(postToEdit);
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

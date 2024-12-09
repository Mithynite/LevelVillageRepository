package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.model.Post;
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
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // Create a new post
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    // Get a post by ID
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/{id}/check-ownership")
    public ResponseEntity<String> checkPostOwnership(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);

        System.out.println(post.getUser().getUsername() + " : " + principal.getName());
        // Compare the logged-in user with the post owner
        if (post.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.ok("owner"); // Logged-in user is the owner
        } else {
            return ResponseEntity.ok("not_owner"); // Logged-in user is not the owner
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        if(updatedPost != null){
            try {
                Post postToEdit = postService.getPostById(id);
                postToEdit.setTitle(updatedPost.getTitle());
                postToEdit.setDescription(updatedPost.getDescription());

                postService.updatePost(postToEdit);
                return ResponseEntity.ok("Post updated successfully");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found!");
            } catch (Exception e) {
                e.printStackTrace(); // Log the error for debugging
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

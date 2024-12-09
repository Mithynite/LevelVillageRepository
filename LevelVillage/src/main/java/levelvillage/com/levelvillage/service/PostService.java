package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void updatePost(Post post) {
        postRepository.save(post);
    }

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get a specific post by ID
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Delete a post by ID
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

}

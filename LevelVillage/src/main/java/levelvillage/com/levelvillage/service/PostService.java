package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.PostRepository;
import levelvillage.com.levelvillage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Post upadtePost(Post post) {
        return postRepository.save(post);
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

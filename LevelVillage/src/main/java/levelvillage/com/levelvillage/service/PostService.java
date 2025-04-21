package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.PostSkill;
import levelvillage.com.levelvillage.model.Skill;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.PostRepository;
import levelvillage.com.levelvillage.repository.PostSkillRepository;
import levelvillage.com.levelvillage.repository.SkillRepository;
import levelvillage.com.levelvillage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostSkillRepository postSkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;


    @Autowired
    public PostService(PostRepository postRepository, PostSkillRepository postSkillRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postSkillRepository = postSkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }
    // Create a new post
    @Transactional
    public Post createPost(PostDTO postDTO) {

        String username = postDTO.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());

        Post savedPost = postRepository.save(post);

        List<Long> skillIds = postDTO.getSkills();
        List<PostSkill> postSkills = mapSkillsToPost(skillIds, post);

        postSkillRepository.saveAll(postSkills);

        return savedPost;
    }

    @Transactional
    public void updatePost(PostDTO updatedPost) {

        Long id = updatedPost.getId();
        Post postToEdit = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found!"));;
        postToEdit.setTitle(updatedPost.getTitle());
        postToEdit.setDescription(updatedPost.getDescription());

        List<Long> skillIds = updatedPost.getSkills();
        List<PostSkill> postSkills = mapSkillsToPost(skillIds, postToEdit);

        postRepository.save(postToEdit);

        postSkillRepository.deleteByPostId(id);
        postSkillRepository.saveAll(postSkills);
    }

    // Get all posts
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll(); // Fetch full entity (not just DTOs)
        return posts.stream().map(post -> new PostDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getTitle(),
                post.getDescription(),
                post.getCreated_at(),
                post.getSkills().stream().map(Skill::getId).toList()
        )).toList();
    }

    // Get a specific post by ID
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Delete a post by ID
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    public List<Skill> getPostSkills(Long postId) {
        return postSkillRepository.findByPostId(postId).stream()
                .map(PostSkill::getSkill)
                .collect(Collectors.toList());
    }

    public List<PostSkill> mapSkillsToPost(List<Long> skillIds, Post post) {

        return skillIds.stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId + "!"));
                    return new PostSkill(post, skill);
                }).collect(Collectors.toList());
    }
}

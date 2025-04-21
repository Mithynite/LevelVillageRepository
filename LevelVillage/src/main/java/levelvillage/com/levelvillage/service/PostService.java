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

/**
 * Service class for managing operations with post repository and its dependencies.
 * @author Jakub Hofman
 */
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
    /**
 * Creates a new post in the database.
 *
 * @param postDTO A PostDTO object containing the details of the new post.
 *                The postDTO must contain a valid username, title, description, and a list of skill IDs.
 *
 * @return The newly created Post entity.
 *
 * @throws RuntimeException If the provided username in the postDTO does not correspond to an existing user.
 */
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

    /**
 * Updates an existing post in the database.
 *
 * @param updatedPost A PostDTO object containing the updated details of the post.
 *                    The post's ID must be provided in the DTO.
 *
 * @throws IllegalArgumentException If a post with the given ID does not exist in the database.
 * @throws RuntimeException If the provided username in the DTO does not correspond to an existing user.
 */
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

    /**
 * Retrieves a list of all posts, including their associated user information and skills.
 *
 * @return A list of PostDTO objects, each representing a post with its details.
 * @throws RuntimeException If there are no posts in the database.
 */
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

    /**
 * Retrieves a list of skills associated with a specific post.
 *
 * @param postId The unique identifier of the post for which to retrieve associated skills.
 * @return A list of Skill entities representing the skills associated with the given post.
 * @throws RuntimeException If no post is found with the given postId.
 */
public List<Skill> getPostSkills(Long postId) {
    return postSkillRepository.findByPostId(postId).stream()
            .map(PostSkill::getSkill)
            .collect(Collectors.toList());
}

    /**
 * Maps a list of skill IDs to a given post, creating a list of PostSkill entities.
 *
 * @param skillIds A list of skill IDs to be associated with the post.
 * @param post The post to which the skills will be associated.
 * @return A list of PostSkill entities, where each entity represents a skill associated with the given post.
 * @throws IllegalArgumentException If a skill with a given ID does not exist in the database.
 */
public List<PostSkill> mapSkillsToPost(List<Long> skillIds, Post post) {

    return skillIds.stream()
            .map(skillId -> {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("Skill not found with ID: " + skillId + "!"));
                return new PostSkill(post, skill);
            }).collect(Collectors.toList());
}
}

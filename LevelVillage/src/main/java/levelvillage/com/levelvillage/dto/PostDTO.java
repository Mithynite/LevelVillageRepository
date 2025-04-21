package levelvillage.com.levelvillage.dto;

import levelvillage.com.levelvillage.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object (DTO) for Post entity. This class is used to transfer data between the service layer and the client.
 * It includes fields for post details and user information.
 * @author Jakub Hofman
 */
@Setter
@Getter
public class PostDTO {
    private Long id;
    private String title;
    private String description;
    private Date createdAt;
    private String username;
    private List<Long> skills = new ArrayList<>();

    /**
     * Constructor for PostDTO with all fields.
     *
     * @param id          The unique identifier of the post.
     * @param username    The username of the user who created the post.
     * @param title       The title of the post.
     * @param description The description of the post.
     * @param createdAt    The date and time when the post was created.
     * @param skills      The list of skill IDs associated with the post.
     */
    public PostDTO(Long id, String username, String title, String description, Date createdAt, List<Long> skills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.username = username;
        this.skills = skills;
    }

    /**
     * Constructor for PostDTO without skills.
     *
     * @param id          The unique identifier of the post.
     * @param username    The username of the user who created the post.
     * @param title       The title of the post.
     * @param description The description of the post.
     * @param createdAt    The date and time when the post was created.
     */
    public PostDTO(Long id, String username, String title, String description, Date createdAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.username = username;
    }

    /**
     * Constructor for PostDTO from a Post entity.
     *
     * @param post The Post entity to create the DTO from.
     */
    public PostDTO(Post post) {
        this.id = post.getId();
        this.username = post.getUser().getUsername(); // Extract username from User
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.createdAt = post.getCreated_at();
    }

    /**
     * Default constructor for PostDTO.
     */
    public PostDTO() {
    }

    /**
     * Returns a string representation of the PostDTO object.
     *
     * @return A string representation of the PostDTO object.
     */
    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", username='" + username + '\'' +
                ", skills=" + skills +
                '}';
    }
}


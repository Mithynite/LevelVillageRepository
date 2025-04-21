package levelvillage.com.levelvillage.dto;

import levelvillage.com.levelvillage.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class PostDTO {
    private Long id;
    private String title;
    private String description;
    private Date createdAt;
    private String username;
    private List<Long> skills = new ArrayList<>();

    public PostDTO(Long id, String username, String title, String description, Date createdAt, List<Long> skills) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.username = username;
        this.skills = skills;
    }

    public PostDTO(Long id, String username, String title, String description, Date createdAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.username = username;
    }

    public PostDTO(Post post) {
        this.id = post.getId();
        this.username = post.getUser().getUsername(); // Extract username from User
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.createdAt = post.getCreated_at();
    }

    public PostDTO() {
    }

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


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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


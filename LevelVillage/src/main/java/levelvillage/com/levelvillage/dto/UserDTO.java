package levelvillage.com.levelvillage.dto;

import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private List<Post> likedPosts = new ArrayList<>();
    private List<Post> savedPosts = new ArrayList<>();
    private List<Long> skills = new ArrayList<>();

    public UserDTO(Long id, String username, String email, String bio, List<Post> likedPosts, List<Post> savedPosts, List<Long> skills) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.bio = bio;
        this.likedPosts = likedPosts;
        this.savedPosts = savedPosts;
        this.skills = skills;
    }
    public UserDTO(Long id, String username, String email, String bio, List<Long> skills) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.bio = bio;
        this.skills = skills;
    }
    public UserDTO(){

    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", likedPosts=" + likedPosts +
                ", savedPosts=" + savedPosts +
                ", skills=" + skills +
                '}';
    }
}

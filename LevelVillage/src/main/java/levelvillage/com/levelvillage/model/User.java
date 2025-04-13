package levelvillage.com.levelvillage.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;
    private String email;
    private String password;
    private String bio;
    private String discord;
    private String instagram;
    private String linkedin;

    @JsonManagedReference // Prevents infinite recursion
    @ManyToMany
    @JoinTable(
            name = "user_liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> likedPosts = new ArrayList<>();

    // Constructors, getters, and setters
    public User() {}

    public User(String username, String email, String password, String bio, String discord, String instagram, String linkedIn) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.discord = discord;
        this.instagram = instagram;
        this.linkedin = linkedIn;
    }
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}


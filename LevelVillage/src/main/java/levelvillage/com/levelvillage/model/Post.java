package levelvillage.com.levelvillage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date created_at;

    @JsonBackReference // Prevents infinite recursion
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false) // FK column is non-updatable
    private User user;

    @ManyToMany
    @JoinTable(
            name = "post_skill",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    public Post() {}

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}

package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

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

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false) // FK column is non-updatable
    private User user;

    public Post() {}

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}

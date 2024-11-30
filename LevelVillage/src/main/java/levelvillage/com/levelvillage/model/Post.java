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

    @Setter
    private String title;
    private String description;
    private Date created_at; // TODO Možná nefunguje

    @ManyToOne
    @JoinColumn(name="user_id") // FK of the post table is 'user_id'
    private User user;

    public Post(Long id, String title, String description, User user) { // + Date of creation
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user;
    }
    public Post() {}
}

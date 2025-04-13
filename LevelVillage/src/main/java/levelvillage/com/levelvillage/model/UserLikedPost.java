package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_liked_posts")
@Setter
@Getter
public class UserLikedPost {
    @EmbeddedId // Composite PK from skill_id and user_id
    private UserPostId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Constructors
    public UserLikedPost() {}

    public UserLikedPost(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new UserPostId(user.getId(), post.getId());
    }
}

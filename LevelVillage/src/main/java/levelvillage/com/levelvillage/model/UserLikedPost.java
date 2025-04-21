package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a relationship between a user and a post in the LevelVillage application.
 * This entity is used to track user likes on posts.
 *
 * @author Tabnine team
 */
@Entity
@Table(name = "user_liked_posts")
@Setter
@Getter
public class UserLikedPost {

    /**
     * The composite primary key consisting of user_id and post_id.
     */
    @EmbeddedId
    private UserPostId id;

    /**
     * The user who liked the post.
     * This field is mapped to the user_id column in the database.
     *
     * @see User
     */
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The post that the user liked.
     * This field is mapped to the post_id column in the database.
     *
     * @see Post
     */
    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Default constructor for JPA.
     */
    public UserLikedPost() {}

    /**
     * Constructor to create a new UserLikedPost instance.
     *
     * @param user The user who liked the post.
     * @param post The post that the user liked.
     */
    public UserLikedPost(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new UserPostId(user.getId(), post.getId());
    }
}

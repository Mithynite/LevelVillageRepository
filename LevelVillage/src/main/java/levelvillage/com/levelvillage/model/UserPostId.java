package levelvillage.com.levelvillage.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents the composite primary key for the UserPost entity.
 * It consists of two fields: userId and postId.
 *
 * @author Tabnine team
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserPostId {

    /**
     * The unique identifier of the user.
     */
    private Long userId;

    /**
     * The unique identifier of the post.
     */
    private Long postId;
}

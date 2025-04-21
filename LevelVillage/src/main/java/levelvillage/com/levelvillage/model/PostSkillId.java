package levelvillage.com.levelvillage.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This class represents the composite primary key for the PostSkill entity.
 * It consists of two fields: postId and skillId, which together uniquely identify a relationship between a post and a skill.
 *
 * @author Jakub Hofman
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostSkillId implements Serializable {

    /**
     * The unique identifier of the post.
     */
    private Long postId;

    /**
     * The unique identifier of the skill.
     */
    private Long skillId;
}

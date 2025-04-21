package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a relationship between a {@link Post} and a {@link Skill} in the LevelVillage application.
 * This entity is mapped to the "post_skill" table in the database.
 *
 * @author Jakub Hofman
 */
@Entity
@Table(name = "post_skill")
@Setter
@Getter
public class PostSkill {
    /**
     * The composite primary key consisting of {@link Post#id} and {@link Skill#id}.
     */
    @EmbeddedId
    private PostSkillId id;

    /**
     * The post associated with this relationship.
     * This field is mapped to the "post_id" column in the database.
     * It is a foreign key referencing the {@link Post#id} column.
     */
    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * The skill associated with this relationship.
     * This field is mapped to the "skill_id" column in the database.
     * It is a foreign key referencing the {@link Skill#id} column.
     */
    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    /**
     * Default constructor for JPA.
     */
    public PostSkill() {}

    /**
     * Constructor to create a new PostSkill instance.
     *
     * @param post The post associated with this relationship.
     * @param skill The skill associated with this relationship.
     */
    public PostSkill(Post post, Skill skill) {
        this.post = post;
        this.skill = skill;
        this.id = new PostSkillId(post.getId(), skill.getId());
    }
}

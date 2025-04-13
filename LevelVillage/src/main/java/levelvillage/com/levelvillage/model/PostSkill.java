package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_skill")
@Setter
@Getter
public class PostSkill {
    @EmbeddedId // Composite PK from skill_id and user_id
    private PostSkillId id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Constructors
    public PostSkill() {}

    public PostSkill(Post post, Skill skill) {
        this.post = post;
        this.skill = skill;
        this.id = new PostSkillId(post.getId(), skill.getId());
    }
}

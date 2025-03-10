package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_skill")
@Setter
@Getter
public class UserSkill {

    @EmbeddedId // Composite PK from skill_id and user_id
    private UserSkillId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Constructors
    public UserSkill() {}

    public UserSkill(User user, Skill skill) {
        this.user = user;
        this.skill = skill;
        this.id = new UserSkillId(user.getId(), skill.getId());
    }

}

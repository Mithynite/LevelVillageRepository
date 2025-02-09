package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_skill") // The name of the table in the database
@Setter
@Getter
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Constructors
    public UserSkill() {
    }

    public UserSkill(User user, Skill skill) {
        this.user = user;
        this.skill = skill;
    }

    @Override
    public String toString() {
        return "UserSkill{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", skill=" + skill.getSkillName() +
                '}';
    }
}

package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;

    public Skill(String skillName) {
        this.skillName = skillName;
    }

    public Skill() {

    }
}

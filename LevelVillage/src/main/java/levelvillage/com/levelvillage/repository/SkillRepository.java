package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface SkillRepository is used to interact with the MySQL database (as template)
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {
}

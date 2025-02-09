package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    void deleteByUserId(Long userId);
    // Method to find UserSkill entities by User object
    List<UserSkill> findByUser(User user);
}


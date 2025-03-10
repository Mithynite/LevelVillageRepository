package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    void deleteByUserId(Long userId);
    List<UserSkill> findByUserId(Long userId);
}


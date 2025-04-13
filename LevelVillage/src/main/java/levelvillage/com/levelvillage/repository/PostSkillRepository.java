package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSkillRepository extends JpaRepository<PostSkill, Long> {
    void deleteByPostId(Long postId);
    List<PostSkill> findByPostId(Long postId);
}

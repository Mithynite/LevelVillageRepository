package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This interface represents a repository for managing {@link PostSkill} entities.
 * It extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations.
 *
 * @author Jakub Hofman
 */
public interface PostSkillRepository extends JpaRepository<PostSkill, Long> {

    /**
     * Deletes all {@link PostSkill} entities associated with the given postId.
     *
     * @param postId the id of the post to delete associated skills for
     */
    void deleteByPostId(Long postId);

    /**
     * Retrieves a list of {@link PostSkill} entities associated with the given postId.
     *
     * @param postId the id of the post to retrieve associated skills for
     * @return a list of {@link PostSkill} entities associated with the given postId
     */
    List<PostSkill> findByPostId(Long postId);
}

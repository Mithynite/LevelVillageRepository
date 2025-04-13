package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Interface PostRepository is used to interact with the MySQL database (as template)
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT new levelvillage.com.levelvillage.dto.PostDTO(p.id, u.username, p.title, p.description, p.created_at) " +
            "FROM Post p JOIN p.user u")
    List<PostDTO> findAllPostsAsDTO();

    @Query("SELECT new levelvillage.com.levelvillage.dto.PostDTO(p.id, u.username, p.title, p.description, p.created_at) " +
            "FROM Post p JOIN p.user u WHERE p.id = :postId")
    PostDTO findPostByIdAsDTO(@Param("postId") Long postId);
}

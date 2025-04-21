package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.dto.PostDTO;
import levelvillage.com.levelvillage.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * This interface represents a repository for managing {@link Post} entities.
 * It extends Spring Data JPA's {@link JpaRepository} interface, providing basic CRUD operations.
 * Additionally, it includes custom queries using Spring Data JPA's {@link Query} annotation.
 * @author Jakub Hofman
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Retrieves a list of all posts as {@link PostDTO} objects.
     * The returned list contains the post ID, user's username, title, description, and creation timestamp.
     *
     * @return a list of {@link PostDTO} objects representing all posts
     */
    @Query("SELECT new levelvillage.com.levelvillage.dto.PostDTO(p.id, u.username, p.title, p.description, p.created_at) " +
            "FROM Post p JOIN p.user u")
    List<PostDTO> findAllPostsAsDTO();

    /**
     * Retrieves a list of posts created by a specific user as {@link PostDTO} objects.
     * The returned list contains the post ID, user's username, title, description, and creation timestamp.
     *
     * @param username the username of the user whose posts should be retrieved
     * @return a list of {@link PostDTO} objects representing the posts created by the specified user
     */
    @Query("SELECT new levelvillage.com.levelvillage.dto.PostDTO(p.id, u.username, p.title, p.description, p.created_at) " +
            "FROM Post p JOIN p.user u WHERE u.username = :username")
    List<PostDTO> findByUserUsername(@Param("username") String username);
}

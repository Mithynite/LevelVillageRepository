package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface PostRepository is used to interact with the MySQL database (as template)
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}

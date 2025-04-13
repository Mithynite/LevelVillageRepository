package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.UserLikedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLikedPostRepository extends JpaRepository<UserLikedPost, Long> {
    List<UserLikedPost> findByUserId(Long userId);
}

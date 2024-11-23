package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface UserRepository is used to interact with the MySQL database (as template)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
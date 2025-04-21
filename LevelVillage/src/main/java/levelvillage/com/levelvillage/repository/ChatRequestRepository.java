package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import levelvillage.com.levelvillage.model.ChatRequest;
import levelvillage.com.levelvillage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This interface extends JpaRepository for managing ChatRequest entities.
 * It provides methods for querying and manipulating ChatRequest data in the database.
 * @author Jakub Hofman
 */
public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    /**
     * Finds all ChatRequests where the receiver's username matches the given username and the status is the given status.
     *
     * @param receiverUsername The username of the receiver.
     * @param status The status of the ChatRequest.
     * @return A list of ChatRequests that match the given criteria.
     */
    List<ChatRequest> findByReceiverUsernameAndStatus(String receiverUsername, ChatRequestStatus status);

    /**
     * Checks if a ChatRequest exists where the sender is the given user, the receiver is the given user, and the status is the given status.
     *
     * @param sender The sender of the ChatRequest.
     * @param receiver The receiver of the ChatRequest.
     * @param status The status of the ChatRequest.
     * @return True if a ChatRequest with the given criteria exists, false otherwise.
     */
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, ChatRequestStatus status);
}


package levelvillage.com.levelvillage.repository;

import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import levelvillage.com.levelvillage.model.ChatRequest;
import levelvillage.com.levelvillage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {
    List<ChatRequest> findByReceiverUsernameAndStatus(String receiverUsername, ChatRequestStatus status);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, ChatRequestStatus status);
}


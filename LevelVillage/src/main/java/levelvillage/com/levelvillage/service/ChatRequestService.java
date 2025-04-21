package levelvillage.com.levelvillage.service;

import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import levelvillage.com.levelvillage.model.ChatRequest;
import levelvillage.com.levelvillage.model.Post;
import levelvillage.com.levelvillage.model.User;
import levelvillage.com.levelvillage.repository.ChatRequestRepository;
import levelvillage.com.levelvillage.repository.PostRepository;
import levelvillage.com.levelvillage.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for managing chat requests from User to User.
 * @author Jakub Hofman
 */
@Service
public class ChatRequestService {

    private final ChatRequestRepository chatRequestRepository;
    private final PostRepository postRepository;
    private UserRepository userRepository;

    public ChatRequestService(ChatRequestRepository chatRequestRepository, UserRepository userRepository, PostRepository postRepository) {
        this.chatRequestRepository = chatRequestRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
 * Sends a chat request from one user to another.
 *
 * @param senderUsername The username of the sender.
 * @param receiverUsername The username of the receiver.
 * @param postId The unique identifier of the post that the receiver is interested in.
 *
 * @throws IllegalArgumentException If the sender's username is the same as the receiver's username.
 * @throws UsernameNotFoundException If either the sender or receiver username is not found.
 * @throws IllegalStateException If a chat request is already pending between the sender and receiver.
 * @throws RuntimeException If the post with the given {@code postId} is not found.
 */
public void sendChatRequest(String senderUsername, String receiverUsername, Long postId) {
    if (senderUsername.equals(receiverUsername)) {
        throw new IllegalArgumentException("Cannot send chat request to yourself.");
    }

    User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
    User receiver = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Receiver not found"));
    Post receiverPost = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

    boolean alreadySent = chatRequestRepository.existsBySenderAndReceiverAndStatus(
            sender, receiver, ChatRequestStatus.PENDING);

    if (alreadySent) {
        throw new IllegalStateException("Chat request already sent.");
    }

    ChatRequest request = new ChatRequest();
    request.setSender(sender);
    request.setReceiver(receiver);
    request.setStatus(ChatRequestStatus.PENDING);
    request.setReceiverPost(receiverPost);

    chatRequestRepository.save(request);
}

    /**
 * Checks if a chat request is already pending between two users.
 *
 * @param senderUsername The username of the sender.
 * @param receiverUsername The username of the receiver.
 *
 * @return {@code true} if a chat request is already pending between the sender and receiver,
 *         {@code false} otherwise.
 *
 * @throws UsernameNotFoundException If either the sender or receiver username is not found.
 */
public boolean chatRequestIsAlreadyPending(String senderUsername, String receiverUsername) {
    User sender = userRepository.findByUsername(senderUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
    User receiver = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Receiver not found"));

    return chatRequestRepository.existsBySenderAndReceiverAndStatus(
            sender, receiver, ChatRequestStatus.PENDING);
}

    public List<ChatRequest> getUsersPendingChatRequests(String receiverUsername) {
        return chatRequestRepository.findByReceiverUsernameAndStatus(receiverUsername, ChatRequestStatus.PENDING);
    }

    /**
 * Responds to a chat request by updating its status.
 *
 * @param requestId The unique identifier of the chat request.
 * @param status The new status to set for the chat request.
 *
 * @throws NoSuchElementException If the chat request with the given {@code requestId} is not found.
 */
public void respondToRequest(Long requestId, ChatRequestStatus status) {
    ChatRequest request = chatRequestRepository.findById(requestId)
            .orElseThrow(() -> new NoSuchElementException("Request not found"));
    request.setStatus(status);
    chatRequestRepository.save(request);
}
}

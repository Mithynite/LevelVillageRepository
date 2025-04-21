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

    public void respondToRequest(Long requestId, ChatRequestStatus status) {
        ChatRequest request = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));
        request.setStatus(status);
        chatRequestRepository.save(request);
    }
}

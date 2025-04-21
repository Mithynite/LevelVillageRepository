package levelvillage.com.levelvillage.controller;

import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import levelvillage.com.levelvillage.model.ChatRequest;
import levelvillage.com.levelvillage.service.ChatRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-requests")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true") //TODO změnit
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    @Autowired
    public ChatRequestController(ChatRequestService chatRequestService) {
        this.chatRequestService = chatRequestService;
    }

    @PostMapping("/{receiverUsername}/posts/{postId}")
    public ResponseEntity<?> sendRequest(@AuthenticationPrincipal UserDetails user,
                                         @PathVariable String receiverUsername,
                                         @PathVariable Long postId) {
        try {
            chatRequestService.sendChatRequest(user.getUsername(), receiverUsername, postId);
            return ResponseEntity.ok("Chat request sent.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ChatRequest>> getUsersIncomingChatRequests(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(chatRequestService.getUsersPendingChatRequests(user.getUsername()));
    }

    @PostMapping("/{requestId}/respond")
    public ResponseEntity<?> respondToRequest(@PathVariable Long requestId,
                                              @RequestParam String response) {
        try {
            chatRequestService.respondToRequest(requestId, ChatRequestStatus.valueOf(response.toUpperCase()));
            return ResponseEntity.ok("Response recorded.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{receiverUsername}")
    public ResponseEntity<Boolean> checkIfTheChatRequestWasAlreadySent(@PathVariable String receiverUsername,
                                                                       @AuthenticationPrincipal UserDetails user) {
        boolean isAlreadyPending = chatRequestService.chatRequestIsAlreadyPending(user.getUsername(), receiverUsername);
        return ResponseEntity.ok(isAlreadyPending);
    }

}

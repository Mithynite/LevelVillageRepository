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

/**
* This class manages API requests for Chat requests.
* @author Jakub Hofman
 *
 * */
@RestController
@RequestMapping("/api/chat-requests")
@CrossOrigin(origins = "http://138.3.255.133:80", allowedHeaders = "*", allowCredentials = "true") //TODO změnit
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    @Autowired
    public ChatRequestController(ChatRequestService chatRequestService) {
        this.chatRequestService = chatRequestService;
    }

        /**
     * This function sends a chat request from the authenticated user to the specified receiver.
     *
     * @param user             The UserDetails object representing the authenticated user making the request.
     *                         This object is obtained from the @AuthenticationPrincipal annotation, which automatically
     *                         injects the authenticated user's details.
     * @param receiverUsername The username of the user who is the intended recipient of the chat request.
     * @param postId           The unique identifier of the post for which the chat request is being sent.
     *
     * @return A ResponseEntity indicating the outcome of the chat request.
     *         - HttpStatus.OK: The chat request was successfully sent.
     *         - HttpStatus.BAD_REQUEST: An error occurred while sending the chat request. The error message
     *           will be included in the response body.
     *
     * @throws Exception If an error occurs while sending the chat request.
     */
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

    /**
 * This function retrieves a list of incoming chat requests for the authenticated user.
 *
 * @param user The UserDetails object representing the authenticated user making the request.
 *             This object is obtained from the @AuthenticationPrincipal annotation, which automatically
 *             injects the authenticated user's details.
 *
 * @return A ResponseEntity containing a list of ChatRequest objects representing the incoming chat requests.
 *         - HttpStatus.OK: The request was successful and the list of chat requests is returned.
 *         - HttpStatus.INTERNAL_SERVER_ERROR: An error occurred while retrieving the chat requests.
 *
 * @throws Exception If an error occurs while retrieving the chat requests.
 */
@GetMapping
public ResponseEntity<List<ChatRequest>> getUsersIncomingChatRequests(@AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(chatRequestService.getUsersPendingChatRequests(user.getUsername()));
}

    /**
 * This function handles the response to a chat request.
 *
 * @param requestId The unique identifier of the chat request to respond to.
 * @param response  The response to the chat request, which can be either "ACCEPTED" or "REJECTED".
 *
 * @return A ResponseEntity indicating the outcome of the response.
 *         - HttpStatus.OK: The response was successfully recorded.
 *         - HttpStatus.BAD_REQUEST: An error occurred while processing the response.
 *
 * @throws Exception If an error occurs while processing the response.
 */
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

    /**
 * This function checks if a chat request has already been sent from the current user to the specified receiver.
 *
 * @param receiverUsername The username of the user who is the intended recipient of the chat request.
 * @param user             The UserDetails object representing the current user making the request.
 *                         This object is obtained from the @AuthenticationPrincipal annotation, which automatically
 *                         injects the authenticated user's details.
 *
 * @return A ResponseEntity containing a boolean value indicating whether a chat request has already been sent.
 *         - True: A chat request has already been sent from the current user to the specified receiver.
 *         - False: No chat request has been sent from the current user to the specified receiver.
 */
@GetMapping("/{receiverUsername}")
public ResponseEntity<Boolean> checkIfTheChatRequestWasAlreadySent(@PathVariable String receiverUsername,
                                                                   @AuthenticationPrincipal UserDetails user) {
    boolean isAlreadyPending = chatRequestService.chatRequestIsAlreadyPending(user.getUsername(), receiverUsername);
    return ResponseEntity.ok(isAlreadyPending);
}

}

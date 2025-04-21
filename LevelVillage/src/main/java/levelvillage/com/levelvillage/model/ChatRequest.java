package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Represents a chat request entity in the LevelVillage application.
 * This class is used to manage and store information about chat requests between users.
 * @author Jakub Hofman
 */
@Getter
@Setter
@Entity
@Data
public class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who initiated the chat request.
     * This field is mapped to the 'sender_id' column in the database.
     * It is a required field and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * The user who received the chat request.
     * This field is mapped to the 'receiver_id' column in the database.
     * It is a required field and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * The post of the user who received the chat request.
     * This field is mapped to the 'receiver_post_id' column in the database.
     * It is a required field and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_post_id", nullable = false)
    private Post receiverPost;

    /**
     * The date and time when the chat request was created.
     * This field is automatically set to the current date and time when a new ChatRequest object is persisted.
     * It is stored in the 'created_at' column in the database.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date created_at;

    /**
     * The status of the chat request.
     * This field is stored in the 'status' column in the database.
     * It is an enum of type ChatRequestStatus and defaults to ChatRequestStatus.PENDING.
     */
    @Enumerated(EnumType.STRING)
    private ChatRequestStatus status = ChatRequestStatus.PENDING;

    /**
     * A method that is automatically called before a new ChatRequest object is persisted to the database.
     * It sets the 'created_at' field to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}

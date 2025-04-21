package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import levelvillage.com.levelvillage.enums.ChatRequestStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Data
public class ChatRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "receiver_post_id", nullable = false)
    private Post receiverPost;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date created_at;

    @Enumerated(EnumType.STRING)
    private ChatRequestStatus status = ChatRequestStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}

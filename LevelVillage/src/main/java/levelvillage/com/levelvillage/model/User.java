package levelvillage.com.levelvillage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Data
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Setter
        private String username;
        private String email;
        private String password;

        // Constructors, getters, and setters
        public User() {}

        public User(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
}


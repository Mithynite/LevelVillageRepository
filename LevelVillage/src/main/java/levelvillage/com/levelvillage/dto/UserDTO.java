package levelvillage.com.levelvillage.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    public UserDTO(Long id, String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
    }
}

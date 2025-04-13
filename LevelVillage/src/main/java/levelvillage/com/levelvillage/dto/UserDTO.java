package levelvillage.com.levelvillage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String discord;
    private String instagram;
    private String linkedIn;
    private List<Long> likedPosts = new ArrayList<>();

    public UserDTO(Long id, String username, String email, String bio, String discord, String instagram, String linkedIn, List<Long> likedPosts) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.bio = bio;
        this.discord = discord;
        this.instagram = instagram;
        this.linkedIn = linkedIn;
        this.likedPosts = likedPosts;
    }

    public UserDTO(){

    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", discord='" + discord + '\'' +
                ", instagram='" + instagram + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", likedPosts=" + likedPosts +
                '}';
    }
}

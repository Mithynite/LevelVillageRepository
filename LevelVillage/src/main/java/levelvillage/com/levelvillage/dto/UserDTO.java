package levelvillage.com.levelvillage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a User's data transfer object (DTO). It is used to transfer user information between different layers of the application.
 *
 * @author Jakub Hofman
 */
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

    /**
     * Constructs a new UserDTO object with the provided parameters.
     *
     * @param id The unique identifier of the user.
     * @param username The username of the user.
     * @param email The email address of the user.
     * @param bio The biography of the user.
     * @param discord The Discord username of the user.
     * @param instagram The Instagram username of the user.
     * @param linkedIn The LinkedIn profile URL of the user.
     * @param likedPosts The list of post IDs that the user has liked.
     */
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

package asp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserDTO implements Serializable {
    private String username;
    private String email;
    private String institutionalEmail;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;

    public UserDTO(String username, String email, String institutionalEmail, String firstName, String lastName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.institutionalEmail = institutionalEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public UserDTO() {
    }

    public UserDTO(String username, String email, String institutionalEmail, String firstName, String lastName, String phoneNumber, String role) {
        this.username = username;
        this.email = email;
        this.institutionalEmail = institutionalEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}

package asp.service;

import asp.dtos.UserDto;
import asp.model.Role;
import asp.model.User;
import asp.repository.TokenRepository;
import asp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<String> getAvailableRoles() {
        List<String> roles = new ArrayList<>();
        for (Role role : Role.values()) {
            roles.add(role.name());
        }
        return roles;
    }

    public void deleteUserByUsername(String username) {
        // Find the user by username
        // Delete the user from the repository
        User user = userRepository.findByUsername(username).get();
        // Delete all tokens associated with the user
        tokenRepository.deleteAll(tokenRepository.findAllValidTokenByUser(user.getId()));
        // Delete the user
        userRepository.delete(user);
    }

    public void updateUser(UserDto user) {
        // Check if the user exists by username
        userRepository.findByUsername(user.getUsername()).ifPresent(existingUser -> {
            // Update the existing user's fields
            existingUser.setEmail(user.getEmail());
            existingUser.setInstitutionalEmail(user.getInstitutionalEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setRole(Role.valueOf(user.getRole()));

            // Save the updated user
            userRepository.save(existingUser);
        });
    }
}

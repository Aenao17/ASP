package asp.controller;

import asp.dtos.UserDto;
import asp.model.User;
import asp.service.AuthenticationService;
import asp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilizatori")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authService;

    @Autowired
    public UserController(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Afișează toți utilizatorii (necesită autentificare)")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/roles")
    @Operation(summary = "Returneaza rolurile dispoibile in sistem")
    public List<String> getAvailableRoles() {
        return userService.getAvailableRoles();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Șterge un utilizator după nume de utilizator (necesită autentificare)")
    public void deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }

    @PutMapping("/{username}")
    @Operation(summary = "Actualizează un utilizator după nume de utilizator (necesită autentificare)")
    public void updateUser(@PathVariable String username, @RequestBody UserDto user) {
        userService.updateUser(user);
    }

}

import asp.dtos.UserDto;
import asp.model.Role;
import asp.model.User;
import asp.repository.TokenRepository;
import asp.repository.UserRepository;
import asp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.token.Token;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllUsers_shouldReturnAllUsers() {
        User user1 = new User();
        user1.setUsername("alice");

        User user2 = new User();
        user2.setUsername("bob");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.findAllUsers();

        assertEquals(2, users.size());
        assertEquals("alice", users.get(0).getUsername());
        assertEquals("bob", users.get(1).getUsername());
    }

    @Test
    void getAvailableRoles_shouldReturnAllEnumNames() {
        List<String> roles = userService.getAvailableRoles();

        assertEquals(List.of("ADMINISTRATOR", "ADMIN", "CD", "PM", "VOLUNTEER", "USER"), roles);
    }
    @Test
    void updateUser_shouldUpdateFieldsAndSave() {
        User existing = new User();
        existing.setUsername("john");

        UserDto dto = new UserDto(
                "john",
                "john@example.com",
                "john@umfcd.ro",
                "John",
                "Doe",
                "0712345678",
                "PM"
        );

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existing));

        userService.updateUser(dto);

        assertEquals("john@example.com", existing.getEmail());
        assertEquals("john@umfcd.ro", existing.getInstitutionalEmail());
        assertEquals("John", existing.getFirstName());
        assertEquals("Doe", existing.getLastName());
        assertEquals("0712345678", existing.getPhoneNumber());
        assertEquals(Role.PM, existing.getRole());

        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_shouldDoNothingIfUserNotFound() {
        UserDto dto = new UserDto(
                "ghost",
                "ghost@example.com",
                "ghost@umfcd.ro",
                "Ghost",
                "User",
                "0700000000",
                "VOLUNTEER"
        );

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        userService.updateUser(dto);

        verify(userRepository, never()).save(any());
    }
}
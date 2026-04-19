// user-service/src/main/java/asp/config/AdminInitializer.java
package asp.config;

import asp.auth.RegisterRequest;
import asp.model.Role;
import asp.model.User;
import asp.repository.UserRepository;
import asp.service.AuthenticationService;
import asp.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AdminInitializer(UserRepository userRepository, UserService userService, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername("admin");
            registerRequest.setPassword("admin");
            registerRequest.setEmail("admin@gmail.com");
            registerRequest.setInstitutionalEmail("admin@ubb.ro");
            registerRequest.setFirstName("Admin");
            registerRequest.setLastName("Admin");
            registerRequest.setPhoneNumber("0754312134");
            registerRequest.setRole(Role.ADMINISTRATOR);
            authenticationService.register(registerRequest);
            System.out.println("Admin user created successfully.");
        }
    }
}
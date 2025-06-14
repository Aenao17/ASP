package asp.controller;

import asp.dtos.FullVolunteer;
import asp.dtos.UserDTO;
import asp.dtos.VolunteerDTO;
import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
public class VolunteerProxyController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.volunteer}")
    private String volunteerServiceUrl;
    @Value("${services.user}")
    private String userServiceUrl;

    @GetMapping
    public ResponseEntity<?> getAllVolunteers(@RequestHeader("Authorization") String authHeader) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForGet(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only CD, ADMIN, ADMINISTRATOR, or PM can access volunteers.");
        }

        // Get volunteers from volunteer service
        ResponseEntity<VolunteerDTO[]> volunteerResponse = restTemplate.exchange(
                volunteerServiceUrl + "/api/volunteers",  // assuming the volunteer backend path
                HttpMethod.GET,
                null,
                VolunteerDTO[].class
        );
        VolunteerDTO[] volunteers = volunteerResponse.getBody();

        // Get users from user service
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> userRequestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserDTO[]> userResponse = restTemplate.exchange(
                userServiceUrl + "/api/users",
                HttpMethod.GET,
                userRequestEntity,
                UserDTO[].class
        );
        UserDTO[] users = userResponse.getBody();

        // Merge volunteers with user details
        List<FullVolunteer> fullVolunteers = Arrays.stream(volunteers)
                .map(vol -> {
                    Optional<UserDTO> userOpt = Arrays.stream(users)
                            .filter(u -> u.getUsername().equals(vol.getUsernameLinked()))
                            .findFirst();

                    if (userOpt.isPresent()) {
                        UserDTO user = userOpt.get();
                        return new FullVolunteer(
                                user.getUsername(),
                                user.getEmail(),
                                user.getInstitutionalEmail(),
                                user.getPhoneNumber(),
                                user.getFirstName(),
                                user.getLastName(),
                                vol.getBirthday(),
                                vol.getDepartament(),
                                vol.getPoints().toString()
                        );
                    } else {
                        return new FullVolunteer(
                                vol.getUsernameLinked(),
                                "unknown",
                                "unknown",
                                "unknown",
                                "unknown",
                                "unknown",
                                vol.getBirthday(),
                                vol.getDepartament(),
                                vol.getPoints().toString()
                        );
                    }
                })
                .toList();
        return ResponseEntity.ok(fullVolunteers);
    }


    @PostMapping
    public ResponseEntity<String> addVolunteer(@RequestBody String volunteerJson, @RequestHeader("Authorization") String authHeader) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForPost(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only CD, ADMIN, or ADMINISTRATOR can add volunteers.");
        }

        String username = volunteerJson.split("\"usernameLinked\":\"")[1].split("\"")[0];
        // Check if the user exists in the user service
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> userRequestEntity = new HttpEntity<>(headers);
        ResponseEntity<UserDTO[]> userResponse = restTemplate.exchange(
                userServiceUrl + "/api/users",
                HttpMethod.GET,
                userRequestEntity,
                UserDTO[].class
        );

        UserDTO[] users = userResponse.getBody();
        boolean userExists = Arrays.stream(users)
                .anyMatch(user -> user.getUsername().equals(username));

        if (!userExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User with username '" + username + "' does not exist.");
        }

        //edit user Role to VOLUNTEER
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.set("Authorization", authHeader);
        UserDTO userToUpdate = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setRole("VOLUNTEER");
            HttpEntity<UserDTO> userRequest = new HttpEntity<>(userToUpdate, userHeaders);
            ResponseEntity<String> userResponseUpdate = restTemplate.exchange(
                    userServiceUrl + "/api/users/" + username,
                    HttpMethod.PUT,
                    userRequest,
                    String.class
            );
            if (!userResponseUpdate.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(userResponseUpdate.getStatusCode())
                        .body("Failed to update user role: " + userResponseUpdate.getBody());
            }
        }

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(volunteerJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                volunteerServiceUrl + "/api/volunteers",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteVolunteerByUsername(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);

        if (!"ADMINISTRATOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only ADMINISTRATOR can delete volunteers.");
        }

        //chanfe user role to USER
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.set("Authorization", authHeader);
        ResponseEntity<UserDTO[]> userResponse = restTemplate.exchange(
                userServiceUrl + "/api/users",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                UserDTO[].class
        );
        UserDTO[] users = userResponse.getBody();
        Optional<UserDTO> userOpt = Arrays.stream(users)
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
        if (userOpt.isPresent()) {
            UserDTO userToUpdate = userOpt.get();
            userToUpdate.setRole("USER");
            HttpEntity<UserDTO> userRequest = new HttpEntity<>(userToUpdate, userHeaders);
            ResponseEntity<String> userResponseUpdate = restTemplate.exchange(
                    userServiceUrl + "/api/users/" + username,
                    HttpMethod.PUT,
                    userRequest,
                    String.class
            );
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                volunteerServiceUrl + "/api/volunteers/" + username,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PutMapping("/{username}")
    public ResponseEntity<String> updateVolunteer(
            @PathVariable String username,
            @RequestBody String volunteerJson,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = extractToken(authHeader);
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);

        if (!"ADMINISTRATOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only ADMINISTRATOR can update volunteers.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        HttpEntity<String> requestEntity = new HttpEntity<>(volunteerJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                volunteerServiceUrl + "/api/volunteers",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }


    private String extractUserRole(String authHeader) {
        String token = extractToken(authHeader);
        Claims claims = jwtService.extractAllClaims(token);
        return claims.get("role", String.class);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid JWT token");
    }

    private boolean isAllowedForGet(String role) {
        return "ADMINISTRATOR".equals(role) || "ADMIN".equals(role) || "CD".equals(role) || "PM".equals(role);
    }

    private boolean isAllowedForPost(String role) {
        return "ADMINISTRATOR".equals(role) || "ADMIN".equals(role) || "CD".equals(role);
    }
}

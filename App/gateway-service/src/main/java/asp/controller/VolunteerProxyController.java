package asp.controller;

import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
public class VolunteerProxyController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.volunteer}")
    private String volunteerServiceUrl;

    @GetMapping
    public ResponseEntity<String> getAllVolunteers(@RequestHeader("Authorization") String authHeader) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForGet(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only CD, ADMIN, ADMINISTRATOR, or PM can access volunteers.");
        }

        ResponseEntity<String> response = restTemplate.exchange(
                volunteerServiceUrl + "/api/volunteers",  // assumes backend path
                HttpMethod.GET,
                null,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping
    public ResponseEntity<String> addVolunteer(@RequestBody String volunteerJson, @RequestHeader("Authorization") String authHeader) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForPost(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: only CD, ADMIN, or ADMINISTRATOR can add volunteers.");
        }

        HttpHeaders headers = new HttpHeaders();
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

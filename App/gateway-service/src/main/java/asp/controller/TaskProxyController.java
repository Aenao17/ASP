package asp.controller;

import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskProxyController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.task}")
    private String taskServiceUrl;

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestHeader("Authorization") String authHeader) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForGet(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only CD, ADMIN, ADMINISTRATOR, or PM can view tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                taskServiceUrl + "/api/tasks",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PostMapping
    public ResponseEntity<String> addTask(
            @RequestBody String taskJson,
            @RequestHeader("Authorization") String authHeader
    ) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForPost(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only CD, ADMIN, or ADMINISTRATOR can create tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        HttpEntity<String> requestEntity = new HttpEntity<>(taskJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                taskServiceUrl + "/api/tasks",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(
            @PathVariable int id,
            @RequestBody String taskJson,
            @RequestHeader("Authorization") String authHeader
    ) {
        String role = extractUserRole(authHeader);
        if (!"ADMINISTRATOR".equals(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only ADMINISTRATOR can update tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        HttpEntity<String> requestEntity = new HttpEntity<>(taskJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                taskServiceUrl + "/api/tasks/" + id,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(
            @PathVariable int id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String role = extractUserRole(authHeader);
        if (!"ADMINISTRATOR".equals(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only ADMINISTRATOR can delete tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                taskServiceUrl + "/api/tasks/" + id,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
    }

    // --- helper methods ---

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
        return "ADMINISTRATOR".equals(role)
                || "ADMIN".equals(role)
                || "CD".equals(role)
                || "PM".equals(role);
    }

    private boolean isAllowedForPost(String role) {
        return "ADMINISTRATOR".equals(role)
                || "ADMIN".equals(role)
                || "CD".equals(role);
    }
}

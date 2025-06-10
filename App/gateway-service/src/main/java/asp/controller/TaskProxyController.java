package asp.controller;

import asp.dtos.Task;
import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only CD, ADMIN, ADMINISTRATOR, or PM can access tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Task[]> taskResponse = restTemplate.exchange(
                taskServiceUrl + "/api/tasks",
                HttpMethod.GET,
                entity,
                Task[].class
        );

        List<Task> tasks = Arrays.asList(taskResponse.getBody());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<String> createTask(
            @RequestBody String taskJson,
            @RequestHeader("Authorization") String authHeader
    ) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForPost(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
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

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PutMapping
    public ResponseEntity<String> updateTask(
            @RequestBody String taskJson,
            @RequestHeader("Authorization") String authHeader
    ) {
        String role = extractUserRole(authHeader);
        if (!isAllowedForPut(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: only CD, ADMIN, or ADMINISTRATOR can update tasks.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        HttpEntity<String> requestEntity = new HttpEntity<>(taskJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                taskServiceUrl + "/api/tasks",
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

    private boolean isAllowedForPut(String role) {
        return "ADMINISTRATOR".equals(role)
                || "ADMIN".equals(role)
                || "CD".equals(role)
                || "PM".equals(role)
        || "VOLUNTEER".equals(role);
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
                || "PM".equals(role)
                || "VOLUNTEER".equals(role);
    }

    private boolean isAllowedForPost(String role) {
        return "ADMINISTRATOR".equals(role)
                || "ADMIN".equals(role)
                || "CD".equals(role);
    }
}
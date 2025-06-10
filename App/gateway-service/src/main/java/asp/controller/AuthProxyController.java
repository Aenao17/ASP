package asp.controller;

import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Autentificare (Proxy)", description = "Redirecționează cererile de login/register către user-service")
@RestController
@RequestMapping("/api/auth")
public class AuthProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtService jwtService;

    @Value("${services.user}")
    private String userServiceUrl;

    public AuthProxyController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Operation(summary = "Înregistrare utilizator nou")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody String requestBody) {

        return forwardToUserService("/api/auth/register", requestBody);
    }

    @Operation(summary = "Autentificare utilizator")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String requestBody) {
        try {
            return forwardToUserService("/api/auth/login", requestBody);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la autentificare: " + e.getMessage());
        }
    }

    @Operation(summary="Get rol utilizator")
    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Rolul utilizatorului nu a putut fi determinat.");
        }
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Logout utilizator")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userServiceUrl + "/api/auth/logout",
                HttpMethod.POST,
                entity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    private ResponseEntity<String> forwardToUserService(String path, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                userServiceUrl + path,
                entity,
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
}

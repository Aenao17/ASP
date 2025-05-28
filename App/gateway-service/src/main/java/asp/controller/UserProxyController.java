package asp.controller;

import asp.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProxyController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.user}")
    private String userServiceUrl;

    @GetMapping
    public ResponseEntity<String> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);

        if (!"ADMINISTRATOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acces interzis: doar ADMINISTRATOR poate accesa această resursă.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userServiceUrl + "/api/users",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }


    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Token JWT lipsă sau invalid");
    }
}

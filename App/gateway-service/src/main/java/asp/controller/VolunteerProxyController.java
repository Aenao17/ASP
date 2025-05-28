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

    @Value("${services.volunteer}")
    private String volunteerServiceBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<String> getAllVolunteers(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtService.extractAllClaims(token);
        String role = claims.get("role", String.class);

        if (!role.equals("PM") && !role.equals("CD")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        ResponseEntity<String> response = restTemplate.exchange(
                volunteerServiceBaseUrl + "/volunteers",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(authHeader)),
                String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    private HttpHeaders createHeaders(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
}

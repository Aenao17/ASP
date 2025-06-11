package asp.controller;

import asp.dtos.InventoryItemDto;
import asp.dtos.StorageUnitDto;
import asp.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/office")
@RequiredArgsConstructor
public class OfficeProxyController {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.office}")
    private String officeServiceUrl;

    // DTO-uri pentru proxy
    public record CreateUnitDto(String name, Integer parentId) {}
    public record ItemDto(String name, Integer quantity) {}

    private HttpHeaders headers(String auth) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Authorization", auth);
        return h;
    }

    @PostMapping("/storage-unit")
    public ResponseEntity<StorageUnitDto> createUnit(
            @RequestBody CreateUnitDto dto,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<CreateUnitDto> req = new HttpEntity<>(dto, headers(auth));
        ResponseEntity<StorageUnitDto> resp = restTemplate.exchange(
                officeServiceUrl + "/api/office/storage-unit",
                HttpMethod.POST, req, StorageUnitDto.class
        );
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    @PostMapping("/storage-unit/{id}/item")
    public ResponseEntity<InventoryItemDto> addItem(
            @PathVariable Integer id,
            @RequestBody ItemDto dto,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<ItemDto> req = new HttpEntity<>(dto, headers(auth));
        ResponseEntity<InventoryItemDto> resp = restTemplate.exchange(
                officeServiceUrl + "/api/office/storage-unit/" + id + "/item",
                HttpMethod.POST, req, InventoryItemDto.class
        );
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    @DeleteMapping("/storage-unit/{id}")
    public ResponseEntity<Void> deleteUnit(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<Void> req = new HttpEntity<>(headers(auth));
        restTemplate.exchange(
                officeServiceUrl + "/api/office/storage-unit/" + id,
                HttpMethod.DELETE, req, Void.class
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/item/{id}/quantity")
    public ResponseEntity<InventoryItemDto> updateItemQuantity(
            @PathVariable Integer id,
            @RequestParam int newQty,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<Void> req = new HttpEntity<>(headers(auth));
        ResponseEntity<InventoryItemDto> resp = restTemplate.exchange(
                officeServiceUrl + "/api/office/item/" + id + "/quantity?newQty=" + newQty,
                HttpMethod.PUT, req, InventoryItemDto.class
        );
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<List<InventoryItemDto>> getItemsInUnit(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<Void> req = new HttpEntity<>(headers(auth));
        ResponseEntity<List<InventoryItemDto>> resp = restTemplate.exchange(
                officeServiceUrl + "/api/office/items/" + id,
                HttpMethod.GET, req,
                new ParameterizedTypeReference<List<InventoryItemDto>>() {}
        );
        return ResponseEntity.ok(resp.getBody());
    }

    @GetMapping("/items/search/{name}")
    public ResponseEntity<List<InventoryItemDto>> searchItems(
            @PathVariable String name,
            @RequestHeader("Authorization") String auth
    ) {
        HttpEntity<Void> req = new HttpEntity<>(headers(auth));
        ResponseEntity<List<InventoryItemDto>> resp = restTemplate.exchange(
                officeServiceUrl + "/api/office/items/search/" + name,
                HttpMethod.GET, req,
                new ParameterizedTypeReference<List<InventoryItemDto>>() {}
        );
        return ResponseEntity.ok(resp.getBody());
    }
}

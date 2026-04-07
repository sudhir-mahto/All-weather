package com.allweather.controller;

import com.allweather.model.ServiceItem;
import com.allweather.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for AC repair services.
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceItemService serviceItemService;

    /**
     * GET /api/services
     * Get all active services for the website.
     * Optional ?all=true returns all services including inactive (admin use).
     */
    @GetMapping
    public ResponseEntity<List<ServiceItem>> getServices(
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        if (all) {
            return ResponseEntity.ok(serviceItemService.getAllServices());
        }
        return ResponseEntity.ok(serviceItemService.getAllActiveServices());
    }

    /**
     * GET /api/services/{id}
     * Get a single service by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        return serviceItemService.getServiceById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Service not found with id: " + id
            )));
    }

    /**
     * POST /api/services
     * Create a new service (admin).
     */
    @PostMapping
    public ResponseEntity<ServiceItem> createService(@RequestBody ServiceItem service) {
        ServiceItem saved = serviceItemService.createService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/services/{id}
     * Update an existing service (admin).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServiceItem service) {
        return serviceItemService.updateService(id, service)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Service not found with id: " + id
            )));
    }
}

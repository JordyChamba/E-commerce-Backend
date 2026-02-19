package com.docker.backend.controller;

import com.docker.backend.dto.request.OrderRequest;
import com.docker.backend.dto.response.OrderResponse;
import com.docker.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Crear una orden (solo CLIENT)")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(request, userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las órdenes (solo ADMIN)")
    public ResponseEntity<Page<OrderResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.findAll(page, size));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Ver mis órdenes (solo CLIENT)")
    public ResponseEntity<Page<OrderResponse>> findMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findByUser(userDetails.getUsername(), page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver orden por ID")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.findById(id, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado de una orden (solo ADMIN)")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                       @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}

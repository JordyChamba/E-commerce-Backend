package com.docker.backend.service;

import com.docker.backend.dto.request.OrderItemRequest;
import com.docker.backend.dto.request.OrderRequest;
import com.docker.backend.dto.response.OrderItemResponse;
import com.docker.backend.dto.response.OrderResponse;
import com.docker.backend.exception.BadRequestException;
import com.docker.backend.exception.ResourceNotFoundException;
import com.docker.backend.model.*;
import com.docker.backend.model.enums.OrderStatus;
import com.docker.backend.repository.OrderRepository;
import com.docker.backend.repository.ProductRepository;
import com.docker.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.docker.backend.model.enums.Role;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderResponse create(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BadRequestException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        Order order = Order.builder()
                .user(user)
                .total(total)
                .status(OrderStatus.PENDING)
                .build();

        // Asociar items a la orden
        items.forEach(item -> item.setOrder(order));
        order.getItems().addAll(items);

        return toResponse(orderRepository.save(order));
    }

    // ADMIN: ve todas las órdenes
    public Page<OrderResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    // CLIENT: ve solo sus órdenes
    public Page<OrderResponse> findByUser(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }

    public OrderResponse findById(Long id, String userEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Un CLIENT solo puede ver sus propias órdenes
        if (user.getRole() == Role.CLIENT && !order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("No tienes permiso para ver esta orden");
        }

        return toResponse(order);
    }

    public OrderResponse updateStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
        try {
            order.setStatus(OrderStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado inválido: " + newStatus +
                    ". Estados válidos: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED");
        }
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUser().getEmail())
                .items(itemResponses)
                .status(order.getStatus().name())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

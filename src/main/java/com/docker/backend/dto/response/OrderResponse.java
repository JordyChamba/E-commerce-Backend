package com.docker.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String userEmail;
    private List<OrderItemResponse> items;
    private String status;
    private BigDecimal total;
    private LocalDateTime createdAt;
}

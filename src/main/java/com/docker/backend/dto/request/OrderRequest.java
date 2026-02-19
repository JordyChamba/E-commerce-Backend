package com.docker.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotEmpty(message = "La orden debe tener al menos un producto")
    private List<OrderItemRequest> items;
}

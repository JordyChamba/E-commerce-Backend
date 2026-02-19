package com.docker.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0)
    private Integer stock;

    private String imageUrl;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;
}

package com.docker.backend.service;

import com.docker.backend.dto.request.ProductRequest;
import com.docker.backend.dto.response.ProductResponse;
import com.docker.backend.exception.ResourceNotFoundException;
import com.docker.backend.model.Category;
import com.docker.backend.model.Product;
import com.docker.backend.repository.CategoryRepository;
import com.docker.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public Page<ProductResponse> findAll(int page, int size, Long categoryId, String name) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products;

        if (categoryId != null && name != null) {
            products = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId, pageable);
        } else if (name != null) {
            products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::toResponse);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .category(categoryService.toResponse(product.getCategory()))
                .createdAt(product.getCreatedAt())
                .build();
    }
}

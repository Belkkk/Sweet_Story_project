package com.shop.SweetStory.catalog.service;

import com.shop.SweetStory.catalog.dto.CreateProductRequest;
import com.shop.SweetStory.catalog.dto.ProductSummaryResponse;
import com.shop.SweetStory.catalog.dto.UpdateProductRequest;
import com.shop.SweetStory.catalog.entity.Product;
import com.shop.SweetStory.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductSummaryResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductSummaryResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return toResponse(product);
    }

    @Transactional
    public ProductSummaryResponse createProduct(CreateProductRequest request) {
        Product product = new Product(
                null,
                request.name().trim(),
                request.description(),
                request.imageUrl(),
                request.price()
        );

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional
    public ProductSummaryResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    private ProductSummaryResponse toResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                product.getDescription()
        );
    }
}
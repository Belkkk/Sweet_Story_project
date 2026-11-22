package com.shop.SweetStory.catalog.service;

import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.catalog.dto.CreateProductRequest;
import com.shop.SweetStory.catalog.dto.CreateProductResponse;
import com.shop.SweetStory.catalog.dto.ProductDetailResponse;
import com.shop.SweetStory.catalog.dto.ProductListResponse;
import com.shop.SweetStory.catalog.dto.ProductSummaryResponse;
import com.shop.SweetStory.catalog.dto.UpdateProductRequest;
import com.shop.SweetStory.catalog.entity.Product;
import com.shop.SweetStory.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductListResponse getAllProducts() {
        List<ProductSummaryResponse> items = productRepository.findAll()
                .stream()
                .map(this::toSummaryResponse)
                .toList();

        return new ProductListResponse(items);
    }

    public ProductDetailResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return toDetailResponse(product);
    }

    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setImages(safeCopy(request.images()));
        product.setFillings(safeCopy(request.fillings()));
        product.setDecorations(safeCopy(request.decorations()));
        product.setMinWeight(request.minWeight());
        product.setMaxWeight(request.maxWeight());

        Product saved = productRepository.save(product);
        return new CreateProductResponse("success", toDetailResponse(saved));
    }

    @Transactional
    public ActionMessageResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setImages(safeCopy(request.images()));
        product.setFillings(safeCopy(request.fillings()));
        product.setDecorations(safeCopy(request.decorations()));
        product.setMinWeight(request.minWeight());
        product.setMaxWeight(request.maxWeight());

        productRepository.save(product);
        return new ActionMessageResponse("success", "Товар успешно обновлен");
    }

    @Transactional
    public ActionMessageResponse deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
        return new ActionMessageResponse("success", "Товар удален");
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    private ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getPreviewImageUrl(),
                product.getDescription()
        );
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                safeCopy(product.getImages()),
                product.getDescription(),
                safeCopy(product.getFillings()),
                safeCopy(product.getDecorations()),
                product.getMinWeight(),
                product.getMaxWeight()
        );
    }

    private List<String> safeCopy(List<String> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}
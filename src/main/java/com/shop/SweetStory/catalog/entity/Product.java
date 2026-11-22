package com.shop.SweetStory.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cake_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal cakePrice;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_fillings", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "filling")
    private List<String> fillings = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_decorations", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "decoration")
    private List<String> decorations = new ArrayList<>();

    @Column(name = "min_weight")
    private Double minWeight;

    @Column(name = "max_weight")
    private Double maxWeight;

    public String getPreviewImageUrl() {
        return (images == null || images.isEmpty()) ? null : images.get(0);
    }


}

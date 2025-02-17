package org.orderhub.pr.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.category.domain.Category;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;
    private String price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;

    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Product(String name, String price, String imageUrl, SaleStatus saleStatus, ConditionStatus conditionStatus, Category category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.saleStatus = saleStatus;
        this.conditionStatus = conditionStatus;
        this.category = category;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Category getMajorCategory() {
        if (category == null) return null;
        return category.getParent() != null ? category.getParent().getParent() : category.getParent();
    }

    public Category getMiddleCategory() {
        if (category == null) return null;
        return category.getParent() == null ? category : category.getParent();
    }

}

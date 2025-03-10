package org.orderhub.pr.product.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.product.dto.request.ProductUpdateRequest;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

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

    @Embedded
    private ProductImage image;

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;

    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus;

    @Type(value = JsonType.class)  // Hibernate-Types를 이용
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    private Instant createdAt;
    private Instant updatedAt;

    @Builder
    public Product(String name, String price, ProductImage image, SaleStatus saleStatus, ConditionStatus conditionStatus, Category category) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.saleStatus = saleStatus;
        this.conditionStatus = conditionStatus;
        this.category = category;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Category getMajorCategory() {
        if (category == null) return null;
        Category parent = category.getParent();
        return (parent != null && parent.getParent() != null) ? parent.getParent() : null;
    }

    public Category getMiddleCategory() {
        if (category == null) return null;
        return category.getParent();
    }

    public void updateProductImage(ProductImage image) {
        this.image = image;
    }

    public void updateProduct(ProductUpdateRequest request, Category updatedCategory) {
        this.name = request.getName();
        this.price = request.getPrice();
        this.category = updatedCategory;
        this.saleStatus = request.getSaleStatus();
        this.conditionStatus = request.getConditionStatus();
    }

    public void deleteProduct() {
        this.saleStatus = SaleStatus.DELETED;
    }

}

package org.orderhub.pr.discount.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BundleDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer discountValue;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @OneToMany(mappedBy = "bundleDiscount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundleDiscountProduct> bundleProducts = new ArrayList<>();

    private Instant startDate;
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    private DiscountStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = DiscountStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Builder
    public BundleDiscount(Integer discountValue, DiscountType discountType, List<BundleDiscountProduct> bundleProducts, Instant startDate, Instant endDate, DiscountStatus status) {
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.bundleProducts = (bundleProducts != null) ? new ArrayList<>(bundleProducts) : new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public boolean appliesTo(Set<Long> productIds) {
        Set<Long> bundleProductIds = bundleProducts.stream()
                .map(bundle -> bundle.getProduct().getId())
                .collect(Collectors.toSet());
        return productIds.equals(bundleProductIds);
    }

    public void clearProducts() {
        this.bundleProducts.clear();
    }

    public void addProducts(List<Product> products) {
        this.bundleProducts.addAll(
                products.stream()
                        .map(product -> new BundleDiscountProduct(this, product))
                        .toList()
        );
    }

}

package org.orderhub.pr.discount.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean appliesTo(Set<Long> productIds) {
        Set<Long> bundleProductIds = bundleProducts.stream()
                .map(bundle -> bundle.getProduct().getId())
                .collect(Collectors.toSet());
        return productIds.equals(bundleProductIds);
    }

}

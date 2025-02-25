package org.orderhub.pr.discount.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue;

    private Integer thresholdQuantity; // N개 이상 구매 시 적용될 개수(THRESHOLD_PRICE 전용)
    private Integer discountUnitPrice; // N개 이상 구매 시 적용될 개당 가격(THRESHOLD_PRICE 전용)

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

    public boolean isThresholdPriceDiscount() {
        return discountType == DiscountType.THRESHOLD_PRICE;
    }

}

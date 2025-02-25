package org.orderhub.pr.discount.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BundleDiscountProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_discount_id")
    private BundleDiscount bundleDiscount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true) // 상품당 하나의 번들 할인만 적용 가능
    private Product product;

    private Instant createdAt;
    private Instant updatedAt;

    @Builder
    public BundleDiscountProduct(BundleDiscount bundleDiscount, Product product) {
        this.bundleDiscount = bundleDiscount;
        this.product = product;
    }


}

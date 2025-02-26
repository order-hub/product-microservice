package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orderhub.pr.product.ProductTestFactory;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BundleDiscountTest {

    private BundleDiscount bundleDiscount;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = mock(Product.class);
        product2 = mock(Product.class);

        when(product1.getId()).thenReturn(1L);
        when(product2.getId()).thenReturn(2L);
        when(product1.getName()).thenReturn("맥주");
        when(product2.getName()).thenReturn("소주");

        // 묶음 할인 생성
        bundleDiscount = BundleDiscount.builder()
                .bundleProducts(List.of(
                        BundleDiscountProduct.builder()
                                .product(product1).bundleDiscount(bundleDiscount).build(),
                        BundleDiscountProduct.builder()
                                .product(product2).bundleDiscount(bundleDiscount).build()
                        ))
                .discountType(DiscountType.FIXED)
                .discountValue(2000)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .build();
    }

    @Test
    void testBundleDiscountCreation() {
        assertThat(bundleDiscount).isNotNull();
        assertThat(bundleDiscount.getDiscountType()).isEqualTo(DiscountType.FIXED);
        assertThat(bundleDiscount.getDiscountValue()).isEqualTo(2000);
        assertThat(bundleDiscount.getBundleProducts()).hasSize(2);
    }

    @Test
    void testAppliesToExactProducts() {
        Set<Long> productIds = Set.of(product1.getId(), product2.getId());
        assertThat(bundleDiscount.appliesTo(productIds)).isTrue();
    }

    @Test
    void testAppliesToDifferentProducts() {
        Set<Long> differentProductIds = Set.of(product1.getId(), 999L); // 존재하지 않는 상품 포함
        assertThat(bundleDiscount.appliesTo(differentProductIds)).isFalse();
    }

    @Test
    void testCreatedAtAndUpdatedAtAreSet() {
        bundleDiscount.onCreate();
        assertThat(bundleDiscount.getCreatedAt()).isNotNull();
        assertThat(bundleDiscount.getUpdatedAt()).isNotNull();
    }

    @Test
    void testUpdatedAtIsUpdatedOnUpdate() throws InterruptedException {
        bundleDiscount.onCreate();
        Instant firstUpdatedAt = bundleDiscount.getUpdatedAt();

        // 1초 후 업데이트
        Thread.sleep(1000);
        bundleDiscount.onUpdate();

        assertThat(bundleDiscount.getUpdatedAt()).isAfter(firstUpdatedAt);
    }

}
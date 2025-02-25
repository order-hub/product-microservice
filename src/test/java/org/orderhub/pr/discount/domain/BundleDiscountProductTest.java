package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BundleDiscountProductTest {

    private BundleDiscount bundleDiscount;
    private Product product;
    private BundleDiscountProduct bundleDiscountProduct;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        bundleDiscount = mock(BundleDiscount.class);
        product = mock(Product.class);

        when(bundleDiscount.getId()).thenReturn(1L);
        when(product.getId()).thenReturn(1L);

        // `BundleDiscountProduct` 객체 생성
        bundleDiscountProduct = BundleDiscountProduct.builder()
                .bundleDiscount(bundleDiscount)
                .product(product)
                .build();
    }

    @Test
    void testBundleDiscountProductCreation() {
        assertThat(bundleDiscountProduct).isNotNull();
        assertThat(bundleDiscountProduct.getBundleDiscount()).isEqualTo(bundleDiscount);
        assertThat(bundleDiscountProduct.getProduct()).isEqualTo(product);
    }

    @Test
    void testCreatedAtAndUpdatedAtAreSetOnCreate() {
        bundleDiscountProduct.onCreate();

        assertThat(bundleDiscountProduct.getCreatedAt()).isNotNull();
        assertThat(bundleDiscountProduct.getUpdatedAt()).isNotNull();
    }

    @Test
    void testUpdatedAtIsUpdatedOnUpdate() throws InterruptedException {
        bundleDiscountProduct.onCreate();
        Instant firstUpdatedAt = bundleDiscountProduct.getUpdatedAt();

        // 1초 대기 후 업데이트 호출
        Thread.sleep(1000);
        bundleDiscountProduct.onUpdate();

        assertThat(bundleDiscountProduct.getUpdatedAt()).isAfter(firstUpdatedAt);
    }
}

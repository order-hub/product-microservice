package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.product.domain.Product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductDiscountTest {

    private ProductDiscount productDiscount;
    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        when(product.getId()).thenReturn(1L);

        productDiscount = ProductDiscountTestFactory.createFixedDiscount(product, 2000);
    }

    @Test
    void testProductDiscountCreation() {
        assertThat(productDiscount).isNotNull();
        assertThat(productDiscount.getProduct()).isEqualTo(product);
        assertThat(productDiscount.getDiscountType()).isEqualTo(DiscountType.FIXED);
        assertThat(productDiscount.getDiscountValue()).isEqualTo(2000);
    }

    @Test
    void testIsThresholdPriceDiscount_ReturnsTrue() {
        productDiscount = ProductDiscountTestFactory.createThresholdPriceDiscount(product, 4, 4000);
        assertThat(productDiscount.isThresholdPriceDiscount()).isTrue();
    }

    @Test
    void testIsThresholdPriceDiscount_ReturnsFalse() {
        assertThat(productDiscount.isThresholdPriceDiscount()).isFalse();
    }
}

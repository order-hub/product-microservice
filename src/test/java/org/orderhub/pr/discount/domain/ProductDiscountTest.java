package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.discount.dto.request.ProductDiscountUpdateRequest;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

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

    @Test
    void testUpdateProductDiscount() {
        // given
        Product newProduct = mock(Product.class);
        when(newProduct.getId()).thenReturn(2L);

        ProductDiscountUpdateRequest updateRequest = ProductDiscountUpdateRequest.builder()
                .discountValue(3000)
                .thresholdQuantity(5)
                .discountUnitPrice(3500)
                .startDate(Instant.now().plusSeconds(3600)) // 1시간 후
                .endDate(Instant.now().plusSeconds(7200)) // 2시간 후
                .build();

        // when
        productDiscount.update(newProduct, DiscountType.PERCENTAGE, updateRequest);

        // then
        assertThat(productDiscount.getProduct()).isEqualTo(newProduct);
        assertThat(productDiscount.getDiscountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(productDiscount.getDiscountValue()).isEqualTo(3000);
        assertThat(productDiscount.getThresholdQuantity()).isEqualTo(5);
        assertThat(productDiscount.getDiscountUnitPrice()).isEqualTo(3500);
        assertThat(productDiscount.getStartDate()).isEqualTo(updateRequest.getStartDate());
        assertThat(productDiscount.getEndDate()).isEqualTo(updateRequest.getEndDate());
    }

    @Test
    void testDeleteProductDiscount() {
        // when
        productDiscount.delete();

        // then
        assertThat(productDiscount.getStatus()).isEqualTo(DiscountStatus.DELETED);
        assertThat(productDiscount.isDeleted()).isTrue();
    }

    @Test
    void testRestoreProductDiscount() {
        // given
        productDiscount.delete();
        assertThat(productDiscount.isDeleted()).isTrue();

        // when
        productDiscount.restore();

        // then
        assertThat(productDiscount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(productDiscount.isDeleted()).isFalse();
    }
}

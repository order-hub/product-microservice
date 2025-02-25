package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.order.domain.OrderItem;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiscountTypeTest {

    private ProductDiscount discount;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        discount = mock(ProductDiscount.class);
        orderItem = mock(OrderItem.class);

        when(discount.getStartDate()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS));
        when(discount.getEndDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    void testFixedDiscount() {
        when(discount.getDiscountValue()).thenReturn(2000);
        when(orderItem.getPrice()).thenReturn(5000);

        Long discountAmount = DiscountType.FIXED.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(2000L);
    }

    @Test
    void testFixedDiscount_CannotExceedPrice() {
        when(discount.getDiscountValue()).thenReturn(7000);
        when(orderItem.getPrice()).thenReturn(5000);

        Long discountAmount = DiscountType.FIXED.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(5000L); // 최대 상품 가격만큼만 할인 가능
    }

    @Test
    void testPercentageDiscount() {
        when(discount.getDiscountValue()).thenReturn(10); // 10% 할인
        when(orderItem.getPrice()).thenReturn(5000);

        Long discountAmount = DiscountType.PERCENTAGE.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(500L); // 5000 * 10% = 500
    }

    @Test
    void testThresholdPriceDiscount_Applicable() {
        when(discount.getThresholdQuantity()).thenReturn(4);
        when(discount.getDiscountUnitPrice()).thenReturn(4000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(orderItem.getQuantity()).thenReturn(5);

        Long discountAmount = DiscountType.THRESHOLD_PRICE.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(5000L);
        // 원래 5000 * 5 = 25000
        // 할인 적용 후 4000 * 5 = 20000
        // 차액 = 5000L
    }

    @Test
    void testThresholdPriceDiscount_NotApplicable() {
        when(discount.getThresholdQuantity()).thenReturn(4);
        when(discount.getDiscountUnitPrice()).thenReturn(4000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(orderItem.getQuantity()).thenReturn(3); // 조건 미충족

        Long discountAmount = DiscountType.THRESHOLD_PRICE.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(0L);
    }

    @Test
    void testFixedDiscount_WhenActive() {
        when(discount.getDiscountValue()).thenReturn(2000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(discount.getStartDate()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS));
        when(discount.getEndDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS));

        Long discountAmount = DiscountType.FIXED.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(2000L);
    }

    @Test
    void testFixedDiscount_WhenNotActive() {
        when(discount.getDiscountValue()).thenReturn(2000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(discount.getStartDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS)); // 할인 시작 전
        when(discount.getEndDate()).thenReturn(Instant.now().plus(2, ChronoUnit.DAYS));

        Long discountAmount = DiscountType.FIXED.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(0L);
    }

    @Test
    void testThresholdPriceDiscount_WhenActive() {
        when(discount.getThresholdQuantity()).thenReturn(4);
        when(discount.getDiscountUnitPrice()).thenReturn(4000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(orderItem.getQuantity()).thenReturn(5);
        when(discount.getStartDate()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS));
        when(discount.getEndDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS));

        Long discountAmount = DiscountType.THRESHOLD_PRICE.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(5000L);
        // 원래 5000 * 5 = 25000
        // 할인 적용 후 4000 * 5 = 20000
        // 차액 = 5000L
    }

    @Test
    void testThresholdPriceDiscount_WhenNotActive() {
        when(discount.getThresholdQuantity()).thenReturn(4);
        when(discount.getDiscountUnitPrice()).thenReturn(4000);
        when(orderItem.getPrice()).thenReturn(5000);
        when(orderItem.getQuantity()).thenReturn(5);
        when(discount.getStartDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS)); // 할인 시작 전
        when(discount.getEndDate()).thenReturn(Instant.now().plus(2, ChronoUnit.DAYS));

        Long discountAmount = DiscountType.THRESHOLD_PRICE.applyDiscount(discount, orderItem);

        assertThat(discountAmount).isEqualTo(0L);
    }
}

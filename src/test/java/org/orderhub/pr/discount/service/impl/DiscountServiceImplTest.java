package org.orderhub.pr.discount.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.order.domain.OrderItem;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiscountServiceImplTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private Product product;
    private OrderItem orderItem;
    private ProductDiscount productDiscount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 가짜 상품 및 주문 항목 생성
        product = mock(Product.class);
        orderItem = mock(OrderItem.class);

        when(orderItem.getProduct()).thenReturn(product);
        when(orderItem.getPrice()).thenReturn(5000); // ✅ Integer 값을 올바르게 반환하도록 설정

        // 가짜 할인 정보 생성
        productDiscount = mock(ProductDiscount.class);
        when(productDiscount.getDiscountType()).thenReturn(DiscountType.FIXED);
        when(productDiscount.getDiscountValue()).thenReturn(2000);

        // ✅ startDate와 endDate를 명확하게 설정하여 NullPointerException 방지
        when(productDiscount.getStartDate()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS)); // 1일 전 시작
        when(productDiscount.getEndDate()).thenReturn(Instant.now().plus(1, ChronoUnit.DAYS)); // 1일 후 종료
    }

    @Test
    void testApplyProductDiscount_WhenDiscountExists() {
        // given
        when(productDiscountRepository.findByProduct(product)).thenReturn(Optional.of(productDiscount));
        when(productDiscount.getDiscountType().applyDiscount(productDiscount, orderItem)).thenReturn(2000);

        // when
        Integer discountAmount = discountService.applyProductDiscount(orderItem);

        // then
        assertThat(discountAmount).isEqualTo(2000);

        verify(productDiscountRepository, times(1)).findByProduct(product);
    }

    @Test
    void testApplyProductDiscount_WhenNoDiscountExists() {
        // given
        when(productDiscountRepository.findByProduct(product)).thenReturn(Optional.empty());

        // when
        Integer discountAmount = discountService.applyProductDiscount(orderItem);

        // then
        assertThat(discountAmount).isEqualTo(0);

        // verify
        verify(productDiscountRepository, times(1)).findByProduct(product);
        verifyNoMoreInteractions(productDiscount);
    }
}

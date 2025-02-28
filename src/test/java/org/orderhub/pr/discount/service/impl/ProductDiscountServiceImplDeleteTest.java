package org.orderhub.pr.discount.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductDiscountServiceImplDeleteTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @InjectMocks
    private ProductDiscountServiceImpl productDiscountService;

    private ProductDiscount productDiscount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 기존 할인 정보 (활성 상태)
        productDiscount = ProductDiscount.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(2000)
                .thresholdQuantity(3)
                .discountUnitPrice(1500)
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();
    }

    @Test
    void testDeleteDiscount() {
        // given
        when(productDiscountRepository.findById(1L)).thenReturn(Optional.of(productDiscount));

        // when
        productDiscountService.deleteDiscount(1L);

        // then
        assertThat(productDiscount.getStatus()).isEqualTo(DiscountStatus.DELETED);
        assertThat(productDiscount.isDeleted()).isTrue();

        // verify
        verify(productDiscountRepository, times(1)).findById(1L);
    }

    @Test
    void testRestoreDiscount() {
        // given
        productDiscount.delete(); // 먼저 삭제
        when(productDiscountRepository.findById(1L)).thenReturn(Optional.of(productDiscount));

        // when
        productDiscountService.restoreDiscount(1L);

        // then
        assertThat(productDiscount.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(productDiscount.isDeleted()).isFalse();

        // verify
        verify(productDiscountRepository, times(1)).findById(1L);
    }

}
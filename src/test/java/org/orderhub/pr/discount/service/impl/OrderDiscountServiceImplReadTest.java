package org.orderhub.pr.discount.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.OrderDiscount;
import org.orderhub.pr.discount.dto.response.OrderDiscountResponse;
import org.orderhub.pr.discount.repository.OrderDiscountRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDiscountServiceImplReadTest {

    @Mock
    private OrderDiscountRepository orderDiscountRepository;

    @InjectMocks
    private OrderDiscountServiceImpl orderDiscountService;

    @Test
    @DisplayName("findById() - 존재하는 OrderDiscount 조회 성공")
    void testFindById_Success() {
        // given
        Long discountId = 1L;
        OrderDiscount orderDiscount = OrderDiscount.builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .provider("Provider A")
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();

        when(orderDiscountRepository.findById(discountId)).thenReturn(Optional.of(orderDiscount));

        // when
        OrderDiscountResponse response = orderDiscountService.findById(discountId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountType()).isEqualTo("PERCENTAGE");
        assertThat(response.getDiscountAmount()).isEqualTo(10);
        assertThat(response.getProvider()).isEqualTo("Provider A");

        verify(orderDiscountRepository, times(1)).findById(discountId);
    }

    @Test
    @DisplayName("findById() - 존재하지 않는 OrderDiscount 조회 시 예외 발생")
    void testFindById_NotFound() {
        // given
        Long discountId = 100L;

        when(orderDiscountRepository.findById(discountId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderDiscountService.findById(discountId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(orderDiscountRepository, times(1)).findById(discountId);
    }

    @Test
    @DisplayName("findAll() - 모든 할인 목록 조회 성공")
    void testFindAll() {
        // given
        List<OrderDiscount> discounts = Arrays.asList(
                OrderDiscount.builder()
                        .discountType(DiscountType.PERCENTAGE)
                        .discountValue(10)
                        .provider("Provider A")
                        .startDate(Instant.now().minusSeconds(7200))
                        .endDate(Instant.now().plusSeconds(7200))
                        .build(),
                OrderDiscount.builder()
                        .discountType(DiscountType.FIXED)
                        .discountValue(5000)
                        .provider("Provider B")
                        .startDate(Instant.now().minusSeconds(3600))
                        .endDate(Instant.now().plusSeconds(3600))
                        .build()
        );

        when(orderDiscountRepository.findAll()).thenReturn(discounts);

        // when
        List<OrderDiscountResponse> responses = orderDiscountService.findAll();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getDiscountType()).isEqualTo("PERCENTAGE");
        assertThat(responses.get(1).getDiscountType()).isEqualTo("FIXED");

        verify(orderDiscountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findActiveAll() - 활성화된 할인 목록만 조회")
    void testFindActiveAll() {
        // given
        OrderDiscount activeDiscount = OrderDiscount.builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(15)
                .provider("Provider X")
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();

        OrderDiscount expiredDiscount = OrderDiscount.builder()
                .discountType(DiscountType.FIXED)
                .discountValue(2000)
                .provider("Provider Y")
                .startDate(Instant.now().minusSeconds(7200)) // 2시간 전 시작
                .endDate(Instant.now().minusSeconds(3600)) // 1시간 전 종료 (비활성)
                .build();

        List<OrderDiscount> discounts = Arrays.asList(activeDiscount, expiredDiscount);

        when(orderDiscountRepository.findAll()).thenReturn(discounts);

        // when
        List<OrderDiscountResponse> responses = orderDiscountService.findActiveAll();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getProvider()).isEqualTo("Provider X");
        assertThat(responses.get(0).getDiscountType()).isEqualTo("PERCENTAGE");

        verify(orderDiscountRepository, times(1)).findAll();
    }
}

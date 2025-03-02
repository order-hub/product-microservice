package org.orderhub.pr.discount.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.OrderDiscount;
import org.orderhub.pr.discount.dto.request.OrderDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.OrderDiscountResponse;
import org.orderhub.pr.discount.repository.OrderDiscountRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDiscountServiceImplTest {

    @Mock
    private OrderDiscountRepository orderDiscountRepository;

    @InjectMocks
    private OrderDiscountServiceImpl orderDiscountService;

    private OrderDiscountCreateRequest request;
    private OrderDiscount savedOrderDiscount;

    @BeforeEach
    void setUp() {
        request = OrderDiscountCreateRequest.builder()
                .discountType("PERCENTAGE")
                .discountAmount(10)
                .provider("Provider A")
                .startDate(Instant.parse("2025-03-02T00:00:00Z"))
                .endDate(Instant.parse("2025-04-02T00:00:00Z"))
                .build();

        savedOrderDiscount = OrderDiscount.builder()
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .provider("Provider A")
                .startDate(Instant.parse("2025-03-02T00:00:00Z"))
                .endDate(Instant.parse("2025-04-02T00:00:00Z"))
                .build();
    }

    @Test
    void create_ShouldSaveAndReturnOrderDiscountResponse() {
        // given
        when(orderDiscountRepository.save(any(OrderDiscount.class))).thenReturn(savedOrderDiscount);

        // when
        OrderDiscountResponse response = orderDiscountService.create(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountType()).isEqualTo(savedOrderDiscount.getDiscountType().name());
        assertThat(response.getDiscountAmount()).isEqualTo(savedOrderDiscount.getDiscountValue());
        assertThat(response.getProvider()).isEqualTo(savedOrderDiscount.getProvider());
        assertThat(response.getStartDate()).isEqualTo(savedOrderDiscount.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(savedOrderDiscount.getEndDate());

        // verify
        verify(orderDiscountRepository, times(1)).save(any(OrderDiscount.class));
    }
}

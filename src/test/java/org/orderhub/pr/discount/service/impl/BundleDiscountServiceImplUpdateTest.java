package org.orderhub.pr.discount.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderhub.pr.discount.domain.BundleDiscount;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.dto.request.BundleDiscountUpdateRequest;
import org.orderhub.pr.discount.dto.response.BundleDiscountResponse;
import org.orderhub.pr.discount.repository.BundleDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleDiscountServiceImplUpdateTest {

    @Mock
    private BundleDiscountRepository bundleDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BundleDiscountServiceImpl bundleDiscountService;

    private BundleDiscount bundleDiscount;
    private BundleDiscountUpdateRequest updateRequest;
    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = mock(Product.class);
        product2 = mock(Product.class);
        product3 = mock(Product.class);

        lenient().when(product1.getId()).thenReturn(1L);
        lenient().when(product2.getId()).thenReturn(2L);
        lenient().when(product3.getId()).thenReturn(3L);

        bundleDiscount = BundleDiscount.builder()
                .discountValue(1000)
                .discountType(DiscountType.FIXED)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .status(DiscountStatus.ACTIVE)
                .build();

        updateRequest = BundleDiscountUpdateRequest.builder()
                .discountValue(2000)
                .discountType(DiscountType.PERCENTAGE)
                .productIds(List.of(1L, 3L)) // 변경된 상품 목록
                .startDate(Instant.now().plusSeconds(86400))
                .endDate(Instant.now().plusSeconds(172800))
                .status(DiscountStatus.INACTIVE)
                .build();
    }

    @Test
    void testUpdateBundleDiscount_ShouldReturnUpdatedBundleDiscountResponse() {
        // Given
        when(bundleDiscountRepository.findById(1L)).thenReturn(Optional.of(bundleDiscount));
        when(productService.findAllById(updateRequest.getProductIds())).thenReturn(List.of(product1, product3));

        // When
        BundleDiscountResponse response = bundleDiscountService.updateBundleDiscount(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountValue()).isEqualTo(updateRequest.getDiscountValue());
        assertThat(response.getDiscountType()).isEqualTo(updateRequest.getDiscountType());
        assertThat(response.getStartDate()).isEqualTo(updateRequest.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(updateRequest.getEndDate());
        assertThat(response.getStatus()).isEqualTo(updateRequest.getStatus());
        assertThat(response.getProductIds()).containsExactlyInAnyOrder(1L, 3L);

        verify(bundleDiscountRepository, times(1)).findById(1L);
        verify(productService, times(1)).findAllById(updateRequest.getProductIds());
    }

    @Test
    void testUpdateBundleDiscount_ShouldThrowException_WhenBundleDiscountNotFound() {
        // Given
        when(bundleDiscountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bundleDiscountService.updateBundleDiscount(999L, updateRequest));

        verify(bundleDiscountRepository, times(1)).findById(999L);
        verify(productService, never()).findAllById(any()); // Product 조회가 호출되지 않아야 함
        verify(bundleDiscountRepository, never()).save(any()); // Save도 호출되지 않아야 함
    }
}

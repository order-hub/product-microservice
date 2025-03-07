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
import org.orderhub.pr.discount.repository.BundleDiscountRepository;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleDiscountServiceImplDeleteTest {

    @Mock
    private BundleDiscountRepository bundleDiscountRepository;

    @InjectMocks
    private BundleDiscountServiceImpl bundleDiscountService;

    private BundleDiscount bundleDiscount;

    @BeforeEach
    void setUp() {
        bundleDiscount = BundleDiscount.builder()
                .discountValue(1000)
                .discountType(DiscountType.FIXED)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .status(DiscountStatus.ACTIVE)
                .build();
    }

    @Test
    void testDeleteBundleDiscount_ShouldDeleteBundleDiscount() {
        // Given
        when(bundleDiscountRepository.findById(1L)).thenReturn(Optional.of(bundleDiscount));

        // When
        bundleDiscountService.deleteBundleDiscount(1L);

        // Then
        verify(bundleDiscountRepository, times(1)).findById(1L);
        verify(bundleDiscountRepository, times(1)).delete(bundleDiscount);
    }

    @Test
    void testDeleteBundleDiscount_ShouldThrowException_WhenBundleDiscountNotFound() {
        // Given
        when(bundleDiscountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class, () ->
                bundleDiscountService.deleteBundleDiscount(999L));

        verify(bundleDiscountRepository, times(1)).findById(999L);
        verify(bundleDiscountRepository, never()).delete(any()); // 삭제가 호출되지 않아야 함
    }
}

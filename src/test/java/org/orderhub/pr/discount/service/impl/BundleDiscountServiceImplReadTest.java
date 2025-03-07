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
import org.orderhub.pr.discount.dto.response.BundleDiscountResponse;
import org.orderhub.pr.discount.repository.BundleDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleDiscountServiceImplReadTest {

    @Mock
    private BundleDiscountRepository bundleDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BundleDiscountServiceImpl bundleDiscountService;

    private BundleDiscount bundleDiscount;
    private Product product;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        lenient().when(product.getId()).thenReturn(1L); // lenient 사용
        lenient().when(product.getName()).thenReturn("맥주"); // lenient 사용

        bundleDiscount = BundleDiscount.builder()
                .discountValue(1000)
                .discountType(DiscountType.FIXED)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .status(DiscountStatus.ACTIVE)
                .build();
    }

    @Test
    void testGetBundleDiscount_ShouldReturnBundleDiscountResponse() {
        // Given
        when(bundleDiscountRepository.findById(1L)).thenReturn(Optional.of(bundleDiscount));

        // When
        BundleDiscountResponse response = bundleDiscountService.getBundleDiscount(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountValue()).isEqualTo(bundleDiscount.getDiscountValue());
        assertThat(response.getDiscountType()).isEqualTo(bundleDiscount.getDiscountType());
        assertThat(response.getStatus()).isEqualTo(bundleDiscount.getStatus());

        verify(bundleDiscountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBundleDiscount_ShouldThrowException_WhenNotFound() {
        // Given
        when(bundleDiscountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bundleDiscountService.getBundleDiscount(999L));

        verify(bundleDiscountRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllBundleDiscounts_ShouldReturnListOfBundleDiscountResponse() {
        // Given
        when(bundleDiscountRepository.findAll()).thenReturn(List.of(bundleDiscount));

        // When
        List<BundleDiscountResponse> responses = bundleDiscountService.getAllBundleDiscounts();

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses.size()).isEqualTo(1);
        assertThat(responses.get(0).getDiscountValue()).isEqualTo(bundleDiscount.getDiscountValue());

        verify(bundleDiscountRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBundleDiscounts_ShouldReturnEmptyList_WhenNoDiscountsExist() {
        // Given
        when(bundleDiscountRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<BundleDiscountResponse> responses = bundleDiscountService.getAllBundleDiscounts();

        // Then
        assertThat(responses).isEmpty();

        verify(bundleDiscountRepository, times(1)).findAll();
    }
}
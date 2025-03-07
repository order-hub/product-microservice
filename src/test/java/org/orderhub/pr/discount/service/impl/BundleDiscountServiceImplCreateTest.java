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
import org.orderhub.pr.discount.dto.request.BundleDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.BundleDiscountResponse;
import org.orderhub.pr.discount.repository.BundleDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleDiscountServiceImplCreateTest {

    @Mock
    private BundleDiscountRepository bundleDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BundleDiscountServiceImpl bundleDiscountService;

    private BundleDiscountCreateRequest createRequest;
    private Product product1;
    private Product product2;
    private BundleDiscount bundleDiscount;

    @BeforeEach
    void setUp() {
        product1 = mock(Product.class);
        product2 = mock(Product.class);

        lenient().when(product1.getId()).thenReturn(1L);
        lenient().when(product2.getId()).thenReturn(2L);

        createRequest = BundleDiscountCreateRequest.builder()
                .discountValue(1500)
                .discountType(DiscountType.PERCENTAGE)
                .productIds(List.of(1L, 2L))
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400))
                .build();

        bundleDiscount = BundleDiscount.builder()
                .discountValue(createRequest.getDiscountValue())
                .discountType(createRequest.getDiscountType())
                .startDate(createRequest.getStartDate())
                .endDate(createRequest.getEndDate())
                .status(DiscountStatus.ACTIVE)
                .build();
    }

    @Test
    void testCreateBundleDiscount_ShouldReturnBundleDiscountResponse() {
        // Given
        when(productService.findAllById(createRequest.getProductIds())).thenReturn(List.of(product1, product2));
        when(bundleDiscountRepository.save(any(BundleDiscount.class))).thenReturn(bundleDiscount);

        // When
        BundleDiscountResponse response = bundleDiscountService.createBundleDiscount(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountValue()).isEqualTo(createRequest.getDiscountValue());
        assertThat(response.getDiscountType()).isEqualTo(createRequest.getDiscountType());
        assertThat(response.getStatus()).isEqualTo(DiscountStatus.ACTIVE);
        assertThat(response.getProductIds()).containsExactlyInAnyOrder(1L, 2L);

        verify(productService, times(1)).findAllById(createRequest.getProductIds());
        verify(bundleDiscountRepository, times(1)).save(any(BundleDiscount.class));
    }

    @Test
    void testCreateBundleDiscount_ShouldThrowException_WhenProductsNotFound() {
        // Given
        when(productService.findAllById(createRequest.getProductIds())).thenReturn(List.of()); // No products found

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> bundleDiscountService.createBundleDiscount(createRequest));

        verify(productService, times(1)).findAllById(createRequest.getProductIds());
        verify(bundleDiscountRepository, never()).save(any(BundleDiscount.class)); // Ensure that save() is never called
    }

}
package org.orderhub.pr.discount.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.dto.response.ProductDiscountResponse;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.service.ProductService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductDiscountServiceImplReadTest {
    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductDiscountServiceImpl productDiscountService;

    private Product product;
    private ProductDiscount activeDiscount;
    private ProductDiscount deletedDiscount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 상품 생성
        product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        ProductImage productImage = mock(ProductImage.class);
        when(productImage.getImageUrl()).thenReturn("imageUrl");
        when(product.getImage()).thenReturn(productImage);

        // Mock 활성화된 할인
        activeDiscount = ProductDiscount.builder()
                .product(product)
                .discountType(DiscountType.FIXED)
                .discountValue(2000)
                .thresholdQuantity(3)
                .discountUnitPrice(1500)
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();

        // Mock 삭제된 할인
        deletedDiscount = ProductDiscount.builder()
                .product(product)
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(10)
                .startDate(Instant.now().minusSeconds(7200)) // 2시간 전 시작
                .endDate(Instant.now().plusSeconds(7200)) // 2시간 후 종료
                .build();

        deletedDiscount.delete();
    }

    @Test
    void testGetActiveDiscountByProduct_WhenDiscountExists() {
        // given
        when(productDiscountRepository.findByProductAndStatus(product, DiscountStatus.ACTIVE))
                .thenReturn(Optional.of(activeDiscount));

        // when
        Optional<ProductDiscountResponse> response = productDiscountService.getActiveDiscountByProduct(product);

        // then
        assertThat(response).isPresent();
        assertThat(response.get().getDiscountValue()).isEqualTo(2000);
        assertThat(response.get().getProductDiscountType()).isEqualTo(DiscountType.FIXED.name());

        // verify
        verify(productDiscountRepository, times(1)).findByProductAndStatus(product, DiscountStatus.ACTIVE);
    }

    @Test
    void testGetActiveDiscountByProduct_WhenNoDiscountExists() {
        // given
        when(productDiscountRepository.findByProductAndStatus(product, DiscountStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // when
        Optional<ProductDiscountResponse> response = productDiscountService.getActiveDiscountByProduct(product);

        // then
        assertThat(response).isEmpty();

        // verify
        verify(productDiscountRepository, times(1)).findByProductAndStatus(product, DiscountStatus.ACTIVE);
    }

    @Test
    void testGetAllActiveDiscounts() {
        // given
        when(productDiscountRepository.findAllByStatus(DiscountStatus.ACTIVE))
                .thenReturn(List.of(activeDiscount));

        // when
        List<ProductDiscountResponse> responses = productDiscountService.getAllActiveDiscounts();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getDiscountValue()).isEqualTo(2000);
        assertThat(responses.get(0).getProductDiscountType()).isEqualTo(DiscountType.FIXED.name());

        // verify
        verify(productDiscountRepository, times(1)).findAllByStatus(DiscountStatus.ACTIVE);
    }

    @Test
    void testGetProductDiscountById_WhenExists() {
        // given
        when(productDiscountRepository.findById(1L)).thenReturn(Optional.of(activeDiscount));

        // when
        ProductDiscount discount = productDiscountService.getProductDiscountById(1L);

        // then
        assertThat(discount).isNotNull();
        assertThat(discount.getDiscountValue()).isEqualTo(2000);
        assertThat(discount.getDiscountType()).isEqualTo(DiscountType.FIXED);

        // verify
        verify(productDiscountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductDiscountById_WhenNotExists() {
        // given
        when(productDiscountRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> productDiscountService.getProductDiscountById(1L));

        // verify
        verify(productDiscountRepository, times(1)).findById(1L);
    }
}
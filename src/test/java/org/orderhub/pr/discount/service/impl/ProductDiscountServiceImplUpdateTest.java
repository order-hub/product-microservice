package org.orderhub.pr.discount.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.dto.request.ProductDiscountUpdateRequest;
import org.orderhub.pr.discount.dto.response.ProductDiscountResponse;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.service.ProductService;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductDiscountServiceImplUpdateTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductDiscountServiceImpl productDiscountService;

    private Product product;
    private ProductDiscount productDiscount;
    private ProductDiscountUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 상품 생성
        product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        ProductImage productImage = mock(ProductImage.class);
        when(productImage.getImageUrl()).thenReturn("imageUrl");
        when(product.getImage()).thenReturn(productImage);

        // 기존 할인 정보
        productDiscount = ProductDiscount.builder()
                .product(product)
                .discountType(DiscountType.FIXED)
                .discountValue(2000)
                .thresholdQuantity(3)
                .discountUnitPrice(1500)
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();

        // 수정 요청 데이터
        updateRequest = ProductDiscountUpdateRequest.builder()
                .productId(1L)
                .discountType("PERCENTAGE")
                .discountValue(10)
                .thresholdQuantity(5)
                .discountUnitPrice(1200)
                .startDate(Instant.now().plusSeconds(1800)) // 30분 후 시작
                .endDate(Instant.now().plusSeconds(7200)) // 2시간 후 종료
                .build();
    }

    @Test
    void testUpdateDiscount() {
        // given
        when(productService.getProductById(updateRequest.getProductId())).thenReturn(product);
        when(productDiscountRepository.findById(updateRequest.getProductId())).thenReturn(java.util.Optional.of(productDiscount));

        // when
        ProductDiscountResponse response = productDiscountService.updateDiscount(updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountValue()).isEqualTo(updateRequest.getDiscountValue());
        assertThat(response.getProductDiscountType()).isEqualTo(DiscountType.PERCENTAGE.name());
        assertThat(response.getThresholdQuantity()).isEqualTo(updateRequest.getThresholdQuantity());
        assertThat(response.getDiscountUnitPrice()).isEqualTo(updateRequest.getDiscountUnitPrice());
        assertThat(response.getStartDate()).isEqualTo(updateRequest.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(updateRequest.getEndDate());

        // verify
        verify(productService, times(1)).getProductById(updateRequest.getProductId());
        verify(productDiscountRepository, times(1)).findById(updateRequest.getProductId());
    }

}
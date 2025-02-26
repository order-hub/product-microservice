package org.orderhub.pr.discount.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.dto.request.ProductDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.ProductDiscountResponse;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.service.ProductService;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductDiscountServiceImplCreateTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductDiscountServiceImpl productDiscountService;

    private Product product;
    private ProductDiscount productDiscount;
    private ProductDiscountCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 상품 생성
        product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        ProductImage productImage = mock(ProductImage.class);
        when(productImage.getImageUrl()).thenReturn("imageUrl");
        when(product.getImage()).thenReturn(productImage);

        // Mock 생성 요청 데이터
        createRequest = ProductDiscountCreateRequest.builder()
                .productId(1L)
                .discountType("FIXED")
                .discountValue(2000)
                .thresholdQuantity(3)
                .discountUnitPrice(1500)
                .startDate(Instant.now().minusSeconds(3600)) // 1시간 전 시작
                .endDate(Instant.now().plusSeconds(3600)) // 1시간 후 종료
                .build();

        // Mock 저장될 할인 객체
        productDiscount = ProductDiscount.builder()
                .product(product)
                .discountType(DiscountType.FIXED)
                .discountValue(createRequest.getDiscountValue())
                .thresholdQuantity(createRequest.getThresholdQuantity())
                .discountUnitPrice(createRequest.getDiscountUnitPrice())
                .startDate(createRequest.getStartDate())
                .endDate(createRequest.getEndDate())
                .build();
    }


    @Test
    void testCreateDiscount() {
        // given
        when(productService.getProductById(createRequest.getProductId())).thenReturn(product);
        when(productDiscountRepository.save(any(ProductDiscount.class))).thenReturn(productDiscount);

        // when
        ProductDiscountResponse response = productDiscountService.createDiscount(createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getDiscountValue()).isEqualTo(createRequest.getDiscountValue());
        assertThat(response.getProductDiscountType()).isEqualTo(DiscountType.FIXED.name());
        assertThat(response.getProductId()).isEqualTo(createRequest.getProductId());

        // verify
        verify(productService, times(1)).getProductById(createRequest.getProductId());
        verify(productDiscountRepository, times(1)).save(any(ProductDiscount.class));
    }
}
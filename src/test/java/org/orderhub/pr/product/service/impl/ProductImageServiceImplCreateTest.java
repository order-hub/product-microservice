package org.orderhub.pr.product.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.orderhub.pr.product.service.ProductImageUploadService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.orderhub.pr.product.exception.ExceptionMessage.PRODUCT_NOT_FOUND;

class ProductImageServiceImplCreateTest {

    private ProductImageService productImageService;

    @Mock
    private ProductImageUploadService productImageUploadService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productImageService = new ProductImageServiceImpl(productImageUploadService, productRepository);
    }

    @Test
    @DisplayName("이미지를 정상적으로 등록하고 상품 정보를 업데이트한다")
    void shouldProcessProductImageSuccessfully() throws IOException {
        // Given
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("image content".getBytes());

        ProductImageRegisterRequest request = ProductImageRegisterRequest.builder()
                .productId(productId)
                .image(mockFile)
                .build();

        String mockImageUrl = "https://s3.amazonaws.com/test-bucket/products/1/thumbnail.jpg";

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productImageUploadService.registerProductImage(request)).thenReturn(mockImageUrl);

        // When
        productImageService.processProductImage(request);

        // Then
        verify(productImageUploadService, times(1)).registerProductImage(request);
        verify(mockProduct, times(1)).updateProductImage(any(ProductImage.class));
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 요청 시 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() throws IOException {
        // Given
        Long nonExistentProductId = 999L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        ProductImageRegisterRequest request = ProductImageRegisterRequest.builder()
                .productId(nonExistentProductId)
                .image(mockFile)
                .build();

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productImageService.processProductImage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);

        verify(productImageUploadService, never()).registerProductImage(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 이미지가 정상적으로 업데이트되는지 검증")
    void shouldUpdateProductImageCorrectly() throws IOException {
        // Given
        Long productId = 2L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("image.png");
        when(mockFile.getBytes()).thenReturn("image content".getBytes());

        ProductImageRegisterRequest request = ProductImageRegisterRequest.builder()
                .productId(productId)
                .image(mockFile)
                .build();

        String mockImageUrl = "https://s3.amazonaws.com/test-bucket/products/2/thumbnail.png";

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productImageUploadService.registerProductImage(request)).thenReturn(mockImageUrl);

        // When
        productImageService.processProductImage(request);

        // Then
        ArgumentCaptor<ProductImage> captor = ArgumentCaptor.forClass(ProductImage.class);
        verify(mockProduct, times(1)).updateProductImage(captor.capture());

        ProductImage capturedImage = captor.getValue();
        assertThat(capturedImage.getImageUrl()).isEqualTo(mockImageUrl);

        verify(productRepository, times(1)).save(mockProduct);
    }
}
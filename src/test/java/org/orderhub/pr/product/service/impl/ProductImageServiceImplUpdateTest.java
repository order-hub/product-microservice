package org.orderhub.pr.product.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.dto.request.ProductImageUpdateRequest;
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

class ProductImageServiceImplUpdateTest {

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
    @DisplayName("상품 이미지가 정상적으로 업데이트되는지 검증")
    void shouldUpdateProductImageSuccessfully() throws IOException {
        // Given
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("updated-image.jpg");

        ProductImageUpdateRequest request = ProductImageUpdateRequest.builder()
                .productId(productId)
                .image(mockFile)
                .build();

        String updatedImageUrl = "https://s3.amazonaws.com/test-bucket/products/1/updated-thumbnail.jpg";

        Product mockProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(productImageUploadService.updateProductImage(request)).thenReturn(updatedImageUrl);

        // When
        productImageService.updateProductImage(request);

        // Then
        ArgumentCaptor<ProductImage> imageCaptor = ArgumentCaptor.forClass(ProductImage.class);
        verify(mockProduct, times(1)).updateProductImage(imageCaptor.capture());

        // 이미지 URL이 정상적으로 업데이트되었는지 검증
        ProductImage capturedImage = imageCaptor.getValue();
        assertThat(capturedImage.getImageUrl()).isEqualTo(updatedImageUrl);

        // `save()` 호출 검증
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID일 경우 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() throws IOException {
        // Given
        Long nonExistentProductId = 999L;
        MultipartFile mockFile = mock(MultipartFile.class);

        ProductImageUpdateRequest request = ProductImageUpdateRequest.builder()
                .productId(nonExistentProductId)
                .image(mockFile)
                .build();

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productImageService.updateProductImage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);

        // `updateProductImage()` 및 `save()`가 호출되지 않음
        verify(productImageUploadService, never()).updateProductImage(any());
        verify(productRepository, never()).save(any());
    }
}
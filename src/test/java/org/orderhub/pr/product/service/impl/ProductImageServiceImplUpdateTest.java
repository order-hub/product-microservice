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
import org.orderhub.pr.util.dto.InMemoryFile;

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
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("updated-image.jpg")
                .contentType("image/jpeg")
                .content("updated image content".getBytes())
                .build();

        ProductImageUpdateRequest request = ProductImageUpdateRequest.builder()
                .productId(productId)
                .storedFile(inMemoryFile)
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

        ProductImage capturedImage = imageCaptor.getValue();
        assertThat(capturedImage.getImageUrl()).isEqualTo(updatedImageUrl);

        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID일 경우 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() throws IOException {
        // Given
        Long nonExistentProductId = 999L;
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("non-existent.jpg")
                .contentType("image/jpeg")
                .content("dummy".getBytes())
                .build();

        ProductImageUpdateRequest request = ProductImageUpdateRequest.builder()
                .productId(nonExistentProductId)
                .storedFile(inMemoryFile)
                .build();

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productImageService.updateProductImage(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);

        verify(productImageUploadService, never()).updateProductImage(any());
        verify(productRepository, never()).save(any());
    }
}

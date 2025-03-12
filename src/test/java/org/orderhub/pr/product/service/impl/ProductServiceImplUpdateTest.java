package org.orderhub.pr.product.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.service.CategoryService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.dto.request.ProductUpdateRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.orderhub.pr.category.exception.ExceptionMessage.NO_SUCH_CATEGORY;
import static org.orderhub.pr.product.exception.ExceptionMessage.PRODUCT_NOT_FOUND;


class ProductServiceImplUpdateTest {

    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomProductRepository customProductRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ProductImageService productImageService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository, customProductRepository, eventPublisher, categoryService, productImageService);
    }

    @Test
    @DisplayName("상품이 정상적으로 업데이트되는지 검증")
    void shouldUpdateProductSuccessfully() {
        // Given
        Long productId = 1L;
        Long categoryId = 2L;
        String categoryName = "any category";
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated Product")
                .categoryId(2L)
                .conditionStatus(null)
                .saleStatus(null)
                .price("5000")
                .build();

        Category mockCategory = mock(Category.class);
        Product mockProduct = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(categoryService.findById(request.getCategoryId())).thenReturn(mockCategory);
        when(mockProduct.getCategory()).thenReturn(mockCategory);
        when(mockCategory.getId()).thenReturn(categoryId);
        when(mockCategory.getName()).thenReturn(categoryName);
        when(mockProduct.getName()).thenReturn("Updated Product");
        when(mockProduct.getPrice()).thenReturn("5000");

        // When
        ProductResponse response = productService.updateProduct(request, productId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Product");

        // product.updateProduct() 호출 검증
        verify(mockProduct, times(1)).updateProduct(request, mockCategory);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID일 경우 예외 발생")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        Long nonExistentProductId = 999L;
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Nonexistent Product")
                .categoryId(1L)
                .build();

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(request, nonExistentProductId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PRODUCT_NOT_FOUND);

        // product.updateProduct() 및 save()가 호출되지 않음
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID일 경우 예외 발생")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        Long productId = 1L;
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated Product")
                .categoryId(999L) // 존재하지 않는 카테고리 ID
                .build();

        Product mockProduct = mock(Product.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(categoryService.findById(request.getCategoryId())).thenThrow(new IllegalArgumentException(NO_SUCH_CATEGORY));

        // When & Then
        assertThatThrownBy(() -> productService.updateProduct(request, productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(NO_SUCH_CATEGORY);

        // product.updateProduct() 및 save()가 호출되지 않음
        verify(mockProduct, never()).updateProduct(any(), any());
    }
}
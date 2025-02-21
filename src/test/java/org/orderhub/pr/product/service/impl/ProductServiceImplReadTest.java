package org.orderhub.pr.product.service.impl;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.domain.SaleStatus;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplReadTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomProductRepository customProductRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Category mockCategory = mock(Category.class);
        when(mockCategory.getName()).thenReturn("Mock Category");

        sampleProduct = Product.builder()
                .name("Test Product")
                .price("1000")
                .image(ProductImage.builder().imageUrl("test.jpg").build())
                .category(mockCategory)
                .saleStatus(SaleStatus.FOR_SALE)
                .conditionStatus(ConditionStatus.NEW)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("모든 제품 페이징으로 가져오기")
    void getAllProducts_ShouldReturnPagedProductResponses() {
        // Given
        List<Product> products = List.of(sampleProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getAllProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");

        // verify: repository가 호출되었는지 확인
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("검색 조건에 맞는 상품들만 가져오는지 테스트")
    void getProductByPage_ShouldReturnFilteredPagedProductResponses() {
        // Given
        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setName("Test");

        List<Product> products = List.of(sampleProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(customProductRepository.searchProducts(searchRequest, pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProductByPage(pageable, searchRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");

        // verify: repository가 호출되었는지 확인
        verify(customProductRepository, times(1)).searchProducts(searchRequest, pageable);
    }
}
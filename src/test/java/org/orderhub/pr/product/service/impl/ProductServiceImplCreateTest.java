package org.orderhub.pr.product.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.service.CategoryService;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.SaleStatus;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductRegisterRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


class ProductServiceImplCreateTest {

    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomProductRepository customProductRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository, customProductRepository, eventPublisher, categoryService);
    }

    @Test
    @DisplayName("상품이 정상적으로 생성되고 이벤트가 발행되는지 검증")
    void shouldCreateProductAndPublishEvent() throws IOException {
        // Given
        ProductRegisterRequest request = ProductRegisterRequest.builder()
                .name("Test Product")
                .categoryId(1L)
                .conditionStatus(ConditionStatus.NEW)
                .saleStatus(SaleStatus.FOR_SALE)
                .build();

        // MultipartFile을 모킹 (실제 내용은 크게 중요치 않음)
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getBytes()).thenReturn("fake-image-bytes".getBytes());
        // 파일이 있다고 가정
        when(mockFile.isEmpty()).thenReturn(false);

        Category mockCategory = mock(Category.class);

        Product mockProduct = Product.builder()
                .name(request.getName())
                .category(mockCategory)
                .conditionStatus(request.getConditionStatus())
                .saleStatus(request.getSaleStatus())
                .build();

        when(categoryService.findById(request.getCategoryId())).thenReturn(mockCategory);
        when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        // When
        ProductResponse response = productService.createProduct(request, mockFile);

        // Then
        // 1) ProductResponse 검증
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Product");

        // 2) productRepository.save()가 제대로 호출됐는지 검증
        verify(productRepository, times(1)).save(any(Product.class));

        // 3) 이벤트 발행 검증
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        // 발행된 이벤트에서 imageRequest 추출
        ProductCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent).isNotNull();

        // 실제 ProductImageRegisterRequest
        var imageRequest = publishedEvent.getImageRequest();
        assertThat(imageRequest).isNotNull();

        // 상품 ID가 정상적으로 전달되었는지
        assertThat(imageRequest.getProductId()).isEqualTo(mockProduct.getId());

        // InMemoryFile로 변환된 데이터가 잘 들어있는지 확인
        var storedFile = imageRequest.getStoredFile();
        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getOriginalFilename()).isEqualTo("test.jpg");
        assertThat(storedFile.getContentType()).isEqualTo("image/jpeg");
        // byte[] 비교
        assertThat(new String(storedFile.getContent())).isEqualTo("fake-image-bytes");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 사용하면 예외 발생")
    void shouldThrowExceptionWhenCategoryNotFound() {
        // Given
        ProductRegisterRequest request = ProductRegisterRequest.builder()
                .name("Test Product")
                .categoryId(1L)
                .conditionStatus(ConditionStatus.NEW)
                .saleStatus(SaleStatus.FOR_SALE)
                .build();

        MultipartFile mockFile = mock(MultipartFile.class);

        when(categoryService.findById(request.getCategoryId())).thenThrow(new IllegalArgumentException("CATEGORY_NOT_FOUND"));

        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request, mockFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CATEGORY_NOT_FOUND");

        // productRepository.save() 및 eventPublisher 호출 안됨
        verify(productRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
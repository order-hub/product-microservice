package org.orderhub.pr.product.service.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.service.ProductImageService;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductEventListenerTest {

    private ProductEventListener productEventListener;

    @Mock
    private ProductImageService productImageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productEventListener = new ProductEventListener(productImageService);
    }

    @Test
    @DisplayName("상품 생성 이벤트가 발생했을 때 이미지 처리가 정상적으로 실행되는지 검증")
    void shouldProcessProductImageOnProductCreatedEvent() throws IOException {
        // Given
        ProductImageRegisterRequest imageRequest = ProductImageRegisterRequest.builder()
                .productId(1L)
                .image(null)
                .build();

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .imageRequest(imageRequest)
                .build();

        // When
        productEventListener.handleProductCreated(event);

        // Then
        ArgumentCaptor<ProductImageRegisterRequest> captor = ArgumentCaptor.forClass(ProductImageRegisterRequest.class);
        verify(productImageService, times(1)).processProductImage(captor.capture());

        ProductImageRegisterRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getProductId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예외 발생 시 정상적으로 처리되는지 검증")
    void shouldHandleExceptionWhenProcessingImageFails() throws IOException {
        // Given
        ProductImageRegisterRequest imageRequest = ProductImageRegisterRequest.builder()
                .productId(2L)
                .image(null)
                .build();

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .imageRequest(imageRequest)
                .build();

        doThrow(new IOException("S3 Upload Failed")).when(productImageService).processProductImage(imageRequest);

        // When & Then
        assertThatThrownBy(() -> productEventListener.handleProductCreated(event))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("S3 Upload Failed");

        verify(productImageService, times(1)).processProductImage(imageRequest);
    }

}
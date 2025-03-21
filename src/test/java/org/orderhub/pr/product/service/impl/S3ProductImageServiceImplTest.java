package org.orderhub.pr.product.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.service.ProductImageUploadService;
import org.orderhub.pr.util.dto.InMemoryFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ProductImageServiceImplTest {

    @Mock
    private S3Client s3Client;

    private ProductImageUploadService productImageUploadService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        productImageUploadService = new S3ProductImageServiceImpl(s3Client);

        // bucketName 필드 주입
        Field bucketNameField = S3ProductImageServiceImpl.class.getDeclaredField("bucketName");
        bucketNameField.setAccessible(true);
        bucketNameField.set(productImageUploadService, "test-bucket");
        assertThat(bucketNameField.get(productImageUploadService)).isEqualTo("test-bucket");
    }

    @Test
    @DisplayName("업로드된 파일의 URL을 반환하는지 검증")
    void shouldReturnCorrectImageUrl() throws IOException {
        // Given
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("test.jpg")
                .contentType("image/jpeg")
                .content("test image content".getBytes())
                .build();

        ProductImageRegisterRequest request = ProductImageRegisterRequest.builder()
                .productId(3L)
                .storedFile(inMemoryFile)
                .build();

        URL mockUrl = new URL("https://s3.amazonaws.com/test-bucket/products/3/thumbnail.jpg");

        S3Utilities mockS3Utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(mockS3Utilities);
        when(mockS3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn(mockUrl);

        // When
        String imageUrl = productImageUploadService.registerProductImage(request);

        // Then
        assertThat(imageUrl).isEqualTo("https://s3.amazonaws.com/test-bucket/products/3/thumbnail.jpg");
    }

    @Test
    @DisplayName("파일이 정상적으로 S3에 업로드되는지 검증")
    void shouldUploadImageToS3() throws IOException {
        // Given
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("test.png")
                .contentType("image/png")
                .content("test image content".getBytes())
                .build();

        ProductImageRegisterRequest request = ProductImageRegisterRequest.builder()
                .productId(1L)
                .storedFile(inMemoryFile)
                .build();

        URL mockUrl = new URL("https://s3.amazonaws.com/test-bucket/products/1/thumbnail.png");

        S3Utilities mockS3Utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(mockS3Utilities);
        when(mockS3Utilities.getUrl(any(GetUrlRequest.class))).thenReturn(mockUrl);

        // When
        String imageUrl = productImageUploadService.registerProductImage(request);

        // Then
        assertThat(imageUrl).contains("products/1/thumbnail.png");

        // putObject가 정상적으로 호출되었는지 검증
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}

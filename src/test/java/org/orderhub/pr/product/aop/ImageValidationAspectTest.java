package org.orderhub.pr.product.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.system.config.ImageConfig;
import org.orderhub.pr.product.exception.ExceptionMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ImageValidationAspectTest {

    private ImageValidationAspect imageValidationAspect;
    private ImageConfig imageConfig;
    private ProceedingJoinPoint proceedingJoinPoint;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // ImageConfig 모킹
        imageConfig = mock(ImageConfig.class);
        when(imageConfig.getMaxFileSize()).thenReturn(5_242_880L); // 5MB
        when(imageConfig.getSupportedExtensionsSet()).thenReturn(Set.of("jpg", "jpeg", "png", "webp"));

        // AOP 인스턴스 생성
        imageValidationAspect = new ImageValidationAspect(imageConfig);

        // Mock 파일 생성
        mockFile = mock(MultipartFile.class);
        proceedingJoinPoint = mock(ProceedingJoinPoint.class);
    }

    @Test
    @DisplayName("파일 크기가 허용 범위 내일 때 정상 동작")
    void shouldAllowValidFileSize() throws Throwable {
        // Given
        when(mockFile.getSize()).thenReturn(1_000_000L); // 1MB
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        imageValidationAspect.validateImageSize(proceedingJoinPoint);
        verify(proceedingJoinPoint, times(1)).proceed(args);
    }

    @Test
    @DisplayName("파일 크기가 초과될 경우 예외 발생")
    void shouldThrowExceptionWhenFileSizeExceedsLimit() {
        // Given
        when(mockFile.getSize()).thenReturn(10_000_000L); // 10MB
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        assertThatThrownBy(() -> imageValidationAspect.validateImageSize(proceedingJoinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.FILE_SIZE_EXCEEDED);
    }

    @Test
    @DisplayName("파일 확장자가 허용된 경우 정상 동작")
    void shouldAllowValidFileExtension() throws Throwable {
        // Given
        when(mockFile.getSize()).thenReturn(1_000_000L); // 1MB
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        imageValidationAspect.validateImageSize(proceedingJoinPoint);
        verify(proceedingJoinPoint, times(1)).proceed(args);
    }

    @Test
    @DisplayName("파일 확장자가 허용되지 않은 경우 예외 발생")
    void shouldThrowExceptionWhenFileExtensionIsNotAllowed() {
        // Given
        when(mockFile.getSize()).thenReturn(1_000_000L); // 1MB
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        assertThatThrownBy(() -> imageValidationAspect.validateImageSize(proceedingJoinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.UNSUPPORTED_FILE_EXTENSIONS);
    }

    @Test
    @DisplayName("파일 이름이 없는 경우 예외 발생")
    void shouldThrowExceptionWhenFileNameIsNull() {
        // Given
        when(mockFile.getSize()).thenReturn(1_000_000L); // 1MB
        when(mockFile.getOriginalFilename()).thenReturn(null);
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        assertThatThrownBy(() -> imageValidationAspect.validateImageSize(proceedingJoinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.INVALID_FILE_FORMAT);
    }

    @Test
    @DisplayName("파일 이름에 확장자가 없는 경우 예외 발생")
    void shouldThrowExceptionWhenFileNameHasNoExtension() {
        // Given
        when(mockFile.getSize()).thenReturn(1_000_000L); // 1MB
        when(mockFile.getOriginalFilename()).thenReturn("testfile");
        Object[] args = {mockFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        assertThatThrownBy(() -> imageValidationAspect.validateImageSize(proceedingJoinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.INVALID_FILE_FORMAT);
    }

}
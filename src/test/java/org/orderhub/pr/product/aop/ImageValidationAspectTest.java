package org.orderhub.pr.product.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orderhub.pr.product.exception.ExceptionMessage;
import org.orderhub.pr.system.config.ImageConfig;
import org.orderhub.pr.util.dto.InMemoryFile;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ImageValidationAspectTest {

    private ImageValidationAspect imageValidationAspect;
    private ImageConfig imageConfig;
    private ProceedingJoinPoint proceedingJoinPoint;

    @BeforeEach
    void setUp() {
        imageConfig = mock(ImageConfig.class);
        when(imageConfig.getMaxFileSize()).thenReturn(5_242_880L); // 5MB
        when(imageConfig.getSupportedExtensionsSet()).thenReturn(Set.of("jpg", "jpeg", "png", "webp"));

        imageValidationAspect = new ImageValidationAspect(imageConfig);
        proceedingJoinPoint = mock(ProceedingJoinPoint.class);
    }

    @Test
    @DisplayName("파일 크기가 허용 범위 내일 때 정상 동작")
    void shouldAllowValidFileSize() throws Throwable {
        // Given
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("test.jpg")
                .contentType("image/jpeg")
                .content(new byte[1_000_000]) // 1MB
                .build();

        Object[] args = {inMemoryFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When
        imageValidationAspect.validateImageSize(proceedingJoinPoint);

        // Then
        verify(proceedingJoinPoint, times(1)).proceed(args);
    }

    @Test
    @DisplayName("파일 크기가 초과될 경우 예외 발생")
    void shouldThrowExceptionWhenFileSizeExceedsLimit() {
        // Given
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("large.jpg")
                .contentType("image/jpeg")
                .content(new byte[10_000_000]) // 10MB
                .build();

        Object[] args = {inMemoryFile};
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
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("test.png")
                .contentType("image/png")
                .content(new byte[500_000])
                .build();

        Object[] args = {inMemoryFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When
        imageValidationAspect.validateImageSize(proceedingJoinPoint);

        // Then
        verify(proceedingJoinPoint, times(1)).proceed(args);
    }

    @Test
    @DisplayName("파일 확장자가 허용되지 않은 경우 예외 발생")
    void shouldThrowExceptionWhenFileExtensionIsNotAllowed() {
        // Given
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("test.txt")
                .contentType("text/plain")
                .content(new byte[100_000])
                .build();

        Object[] args = {inMemoryFile};
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
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename(null)
                .contentType("image/jpeg")
                .content(new byte[100_000])
                .build();

        Object[] args = {inMemoryFile};
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
        InMemoryFile inMemoryFile = InMemoryFile.builder()
                .originalFilename("testfile") // 확장자 없음
                .contentType("image/jpeg")
                .content(new byte[100_000])
                .build();

        Object[] args = {inMemoryFile};
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        // When & Then
        assertThatThrownBy(() -> imageValidationAspect.validateImageSize(proceedingJoinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.INVALID_FILE_FORMAT);
    }
}

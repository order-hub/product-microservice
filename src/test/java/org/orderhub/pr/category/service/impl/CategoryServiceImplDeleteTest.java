package org.orderhub.pr.category.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryStatus;
import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.category.exception.ExceptionMessage;
import org.orderhub.pr.category.repository.CategoryRepository;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CategoryServiceImplDeleteTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = Category.builder()
                .id(1L)
                .name("전자제품")
                .type(CategoryType.MAJOR)
                .parent(null)
                .build();
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void categoryDeleteSuccess() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // When
        categoryService.categoryDelete(1L);

        // Then
        assertThat(category.getStatus()).isEqualTo(CategoryStatus.DELETED);

        verify(categoryRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }


    @Test
    @DisplayName("카테고리 복구 성공")
    void categoryRestoreSuccess() {
        // Given (초기 상태: 삭제됨)
        category.delete(); // 미리 상태를 DELETED로 변경
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // When
        categoryService.categoryRestore(1L);

        // Then
        assertThat(category.getStatus()).isEqualTo(CategoryStatus.ACTIVE);

        verify(categoryRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("카테고리 삭제 실패, 존재하지 않음")
    void categoryDeleteFail_notExist() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.categoryDelete(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessage.NO_SUCH_CATEGORY);

        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("카테고리 복구 실패, 존재하지 않음")
    void categoryRestoreFail_notExist() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.categoryRestore(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessage.NO_SUCH_CATEGORY);

        verify(categoryRepository, times(1)).findById(999L);
    }
}

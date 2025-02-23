package org.orderhub.pr.category.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.category.dto.request.CategoryUpdateRequest;
import org.orderhub.pr.category.dto.response.CategoryUpdateResponse;
import org.orderhub.pr.category.exception.ExceptionMessage;
import org.orderhub.pr.category.repository.CategoryRepository;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplUpdateTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category majorCategory;
    private Category middleCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        majorCategory = Category.builder()
                .id(1L)
                .name("전자제품")
                .type(CategoryType.MAJOR)
                .parent(null)
                .build();

        middleCategory = Category.builder()
                .id(2L)
                .name("스마트폰")
                .type(CategoryType.MIDDLE)
                .parent(majorCategory)
                .build();
    }

    @Test
    @DisplayName("카테고리 업데이트 성공")
    void categoryUpdateSuccess() {
        // Given
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(middleCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(majorCategory));

        // 요청 데이터
        CategoryUpdateRequest request = new CategoryUpdateRequest(2L, "변경된 스마트폰", 1L, "MIDDLE");

        // When (메서드 실행)
        CategoryUpdateResponse response = categoryService.categoryUpdate(request);

        // Then (검증)
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("변경된 스마트폰");
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getCategoryType()).isEqualTo("MIDDLE");

        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("카테고리 업데이트 성공 부모없음")
    void categoryUpdateSuccess_withoutParent() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(majorCategory));

        CategoryUpdateRequest request = new CategoryUpdateRequest(1L, "변경된 전자제품", null, "MAJOR");

        // When
        CategoryUpdateResponse response = categoryService.categoryUpdate(request);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("변경된 전자제품");
        assertThat(response.getParentId()).isNull();
        assertThat(response.getCategoryType()).isEqualTo("MAJOR");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).findById(null); // 부모가 없으므로 호출되지 않아야 함
    }

    @Test
    @DisplayName("카테고리 업데이트 실패, 존재하지 않음")
    void categoryUpdateFail() {
        // Given
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        CategoryUpdateRequest request = new CategoryUpdateRequest(999L, "없는 카테고리", null, "MAJOR");

        // When & Then (예외 검증)
        assertThatThrownBy(() -> categoryService.categoryUpdate(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessage.NO_SUCH_CATEGORY);

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 업데이트 실패, 자기자신을 부모로 설정")
    void categoryUpdateFail_beOwnParent() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(majorCategory));

        CategoryUpdateRequest request = new CategoryUpdateRequest(1L, "변경된 전자제품", 1L, "MAJOR");

        // When & Then (예외 검증)
        assertThatThrownBy(() -> categoryService.categoryUpdate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.CANNOT_BE_YOUR_OWN_PARENT);

        verify(categoryRepository, times(2)).findById(1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}

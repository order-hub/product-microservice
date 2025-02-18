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
import org.orderhub.pr.category.dto.response.CategoryTreeResponse;
import org.orderhub.pr.category.repository.CategoryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CategoryServiceImplReadTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category majorCategory;
    private Category middleCategory;
    private Category minorCategory;
    private Category deletedCategory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        majorCategory = Category.builder()
                .id(1L)
                .name("전자제품")
                .parent(null)
                .type(CategoryType.MAJOR)
                .build();

        middleCategory = Category.builder()
                .id(2L)
                .name("스마트폰")
                .type(CategoryType.MIDDLE)
                .parent(majorCategory)
                .build();

        minorCategory = Category.builder()
                .id(3L)
                .name("아이폰")
                .type(CategoryType.MINOR)
                .parent(middleCategory)
                .build();

        deletedCategory = Category.builder()
                .id(4L)
                .name("패션")
                .type(CategoryType.MAJOR)
                .parent(null)
                .build();

        deletedCategory.delete();

        majorCategory.addChild(middleCategory);
        middleCategory.addChild(minorCategory);
    }

    @Test
    @DisplayName("모든 카테고리 조회 성공")
    void allCategoryReadSuccess() {
        // Given
        when(categoryRepository.findAllWithChildren()).thenReturn(List.of(majorCategory, deletedCategory));

        // When
        List<CategoryTreeResponse> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2); // ✅ 전자제품, 패션 포함
        assertThat(result.get(0).getName()).isEqualTo("전자제품");
        assertThat(result.get(1).getName()).isEqualTo("패션");

        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getName()).isEqualTo("스마트폰");
        assertThat(result.get(0).getChildren().get(0).getChildren().get(0).getName()).isEqualTo("아이폰");

        verify(categoryRepository, times(1)).findAllWithChildren();
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("ACTIVE 상태의 카테고리 조회 성공")
    void activeCategoryReadSuccess() {
        // Given
        when(categoryRepository.findWithChildrenAndStatus(CategoryStatus.ACTIVE)).thenReturn(List.of(majorCategory));

        // When
        List<CategoryTreeResponse> result = categoryService.getAllCategoriesByActive();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("전자제품");

        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getName()).isEqualTo("스마트폰");
        assertThat(result.get(0).getChildren().get(0).getChildren().get(0).getName()).isEqualTo("아이폰");

        verify(categoryRepository, times(1)).findWithChildrenAndStatus(CategoryStatus.ACTIVE);
        verify(categoryRepository, never()).save(any(Category.class));
        assertThat(result).extracting(CategoryTreeResponse::getName)
                .doesNotContain("패션");

    }
}

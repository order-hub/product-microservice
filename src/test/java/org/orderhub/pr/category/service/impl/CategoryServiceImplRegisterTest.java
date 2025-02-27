package org.orderhub.pr.category.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.category.dto.request.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.response.CategoryRegisterResponse;
import org.orderhub.pr.category.exception.ExceptionMessage;
import org.orderhub.pr.category.repository.CategoryRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.orderhub.pr.category.exception.ExceptionMessage.CANNOT_BE_YOUR_OWN_CHILD;

class CategoryServiceImplRegisterTest {

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
                .parent(null)
                .type(CategoryType.MAJOR)
                .build();

        middleCategory = Category.builder()
                .id(2L)
                .name("스마트폰")
                .type(CategoryType.MIDDLE)
                .parent(majorCategory)
                .build();

        majorCategory.addChild(middleCategory);
    }

    @Test
    @DisplayName("카테고리 등록 성공 부모 없음")
    void categoryRegisterSuccess_withoutParent() {
        // Given
        CategoryRegisterRequest request = new CategoryRegisterRequest("전자제품", null, "MAJOR");
        Category category = Category.builder()
                .id(1L)
                .name("전자제품")
                .parent(null)
                .type(CategoryType.MAJOR)
                .build();
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryRegisterResponse response = categoryService.categoryRegister(request);

        // Then
        assertThat(response.getName()).isEqualTo("전자제품");
        assertThat(response.getCategoryType()).isEqualTo(CategoryType.MAJOR.toString());
        assertThat(response.getParentId()).isNull();

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 등록 성공 부모 존재")
    void categoryRegisterSuccess_withParent() {
        // Given (부모 카테고리 생성)
        Category parentCategory = Category.builder()
                .id(1L)
                .name("전자제품")
                .parent(null)
                .type(CategoryType.MAJOR)
                .build();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(parentCategory));

        CategoryRegisterRequest request = new CategoryRegisterRequest("스마트폰", 1L, "MIDDLE");
        Category newCategory = Category.builder()
                .name("스마트폰")
                .parent(parentCategory)
                .type(CategoryType.MIDDLE)
                .build();
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        CategoryRegisterResponse response = categoryService.categoryRegister(request);

        // Then
        assertThat(response.getName()).isEqualTo("스마트폰");
        assertThat(response.getCategoryType()).isEqualTo(CategoryType.MIDDLE.toString());
        assertThat(response.getParentId()).isEqualTo(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 등록 실패 부모 카테고리 없음")
    void categoryRegisterFail_withoutParent() {
        // Given
        CategoryRegisterRequest request = new CategoryRegisterRequest("스마트폰", 999L, "MIDDLE");
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then (예외 발생 검증)
        assertThatThrownBy(() -> categoryService.categoryRegister(request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessage.NO_SUCH_CATEGORY);

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("부모 카테고리에 자식을 정상적으로 추가할 수 있어야 한다.")
    void addChildTest() {
        // Given
        Category newChild = Category.builder()
                .id(5L)
                .name("갤럭시")
                .type(CategoryType.MINOR)
                .parent(null)
                .build();

        // When
        majorCategory.addChild(newChild);

        // Then
        assertThat(majorCategory.getChildren()).hasSize(2); // 스마트폰, 갤럭시 포함
        assertThat(majorCategory.getChildren()).extracting(Category::getName)
                .contains("스마트폰", "갤럭시"); // 두 자식이 정상적으로 추가되었는지 확인
    }

    @Test
    @DisplayName("자기 자신을 자식으로 추가할 경우 예외가 발생해야 한다.")
    void addChildSelfTest() {
        // When & Then
        assertThatThrownBy(() -> majorCategory.addChild(majorCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CANNOT_BE_YOUR_OWN_CHILD);
    }
}
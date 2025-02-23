package org.orderhub.pr.category.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.orderhub.pr.category.exception.ExceptionMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.orderhub.pr.category.exception.ExceptionMessage.MAJOR_CANNOT_BE_CHILD;
import static org.orderhub.pr.category.exception.ExceptionMessage.MINOR_CANNOT_BE_PARENT;

class CategoryTest {

    private Category majorCategory;
    private Category middleCategory;
    private Category subCategory;

    @BeforeEach
    void setUp() {
        majorCategory = Category.builder()
                .id(1L)
                .name("Major Category")
                .type(CategoryType.MAJOR)
                .build();

        middleCategory = Category.builder()
                .id(2L)
                .name("Middle Category")
                .parent(majorCategory)
                .type(CategoryType.MIDDLE)
                .build();

        subCategory = Category.builder()
                .id(3L)
                .name("Sub Category")
                .parent(middleCategory)
                .type(CategoryType.MINOR)
                .build();
    }

    @Test
    @DisplayName("카테고리를 정상적으로 생성할 수 있는지 검증")
    void shouldCreateCategorySuccessfully() {
        // Then
        assertThat(majorCategory.getName()).isEqualTo("Major Category");
        assertThat(majorCategory.getParent()).isNull();
        assertThat(middleCategory.getParent()).isEqualTo(majorCategory);
        assertThat(subCategory.getParent()).isEqualTo(middleCategory);
    }

    @Test
    @DisplayName("자신을 부모로 설정하면 예외 발생")
    void shouldThrowExceptionWhenSettingSelfAsParent() {
        // When & Then
        assertThatThrownBy(() -> subCategory.applyUpdate("Sub Updated", CategoryType.MINOR, subCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ExceptionMessage.CANNOT_BE_YOUR_OWN_PARENT);
    }

    @Test
    @DisplayName("자신을 자식으로 추가하면 예외 발생")
    void shouldThrowExceptionWhenAddingSelfAsChild() {
        // When & Then
        assertThatThrownBy(() -> subCategory.addChild(subCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ExceptionMessage.CANNOT_BE_YOUR_OWN_CHILD);
    }

    @Test
    @DisplayName("카테고리 삭제 후 상태가 DELETED인지 확인")
    void shouldDeleteCategorySuccessfully() {
        // When
        subCategory.delete();

        // Then
        assertThat(subCategory.getStatus()).isEqualTo(CategoryStatus.DELETED);
    }

    @Test
    @DisplayName("카테고리 복원 후 상태가 ACTIVE인지 확인")
    void shouldRestoreCategorySuccessfully() {
        // Given
        subCategory.delete();

        // When
        subCategory.restore();

        // Then
        assertThat(subCategory.getStatus()).isEqualTo(CategoryStatus.ACTIVE);
    }

    @Test
    @DisplayName("카테고리 정보를 업데이트하면 정상적으로 변경되는지 검증")
    void shouldUpdateCategorySuccessfully() {
        // Given
        Category newParent = Category.builder()
                .id(4L)
                .name("New Parent Category")
                .type(CategoryType.MIDDLE)
                .build();

        // When
        subCategory.applyUpdate("Updated Sub", CategoryType.MINOR, newParent);

        // Then
        assertThat(subCategory.getName()).isEqualTo("Updated Sub");
        assertThat(subCategory.getType()).isEqualTo(CategoryType.MINOR);
        assertThat(subCategory.getParent()).isEqualTo(newParent);
    }

    @Test
    @DisplayName("대분류(MAJOR)는 부모를 가질 수 없음")
    void shouldThrowExceptionWhenMajorCategoryHasParent() {
        // Given
        Category invalidMajorCategory = Category.builder()
                .id(4L)
                .name("Invalid Major")
                .parent(middleCategory) // ✅ 부모가 있는 MAJOR 카테고리
                .type(CategoryType.MAJOR)
                .build();

        // When & Then
        assertThatThrownBy(() -> invalidMajorCategory.applyUpdate("Invalid Major", CategoryType.MAJOR, middleCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MAJOR_CANNOT_BE_CHILD);
    }

    @Test
    @DisplayName("소분류(MINOR)는 자식을 가질 수 없음")
    void shouldThrowExceptionWhenSubCategoryHasChildren() {
        // Given
        Category invalidChild = Category.builder()
                .id(5L)
                .name("Invalid Child")
                .type(CategoryType.MINOR)
                .build();

        // When & Then
        assertThatThrownBy(() -> subCategory.addChild(invalidChild))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(MINOR_CANNOT_BE_PARENT);
    }

}
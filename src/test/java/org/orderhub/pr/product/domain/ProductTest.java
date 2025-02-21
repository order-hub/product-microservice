package org.orderhub.pr.product.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.product.dto.request.ProductUpdateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductTest {

    private Product product;
    private Category majorCategory;
    private Category middleCategory;
    private Category subCategory;


    @BeforeEach
    void setUp() {
        majorCategory = mock(Category.class);
        when(majorCategory.getParent()).thenReturn(null);

        middleCategory = mock(Category.class);
        when(middleCategory.getParent()).thenReturn(majorCategory);

        subCategory = mock(Category.class);
        when(subCategory.getParent()).thenReturn(middleCategory);

        product = Product.builder()
                .name("Test Product")
                .price("1000")
                .image(ProductImage.builder().imageUrl("https://test.com/image.jpg").build())
                .saleStatus(SaleStatus.FOR_SALE)
                .conditionStatus(ConditionStatus.NEW)
                .category(subCategory)
                .build();
    }

    @Test
    @DisplayName("대분류를 올바르게 가져오는지 테스트")
    void shouldReturnMajorCategory() {
        // When
        Category major = product.getMajorCategory();

        // Then
        assertThat(major).isNotNull();
        assertThat(major).isEqualTo(majorCategory);
    }


    @Test
    @DisplayName("중분류를 올바르게 가져오는지 테스트")
    void shouldReturnMiddleCategory() {
        // When
        Category middle = product.getMiddleCategory();

        // Then
        assertThat(middle).isNotNull();
        assertThat(middle).isEqualTo(middleCategory);
    }

    @Test
    @DisplayName("상품의 카테고리가 없을 때 대분류와 중분류가 null인지 테스트")
    void shouldReturnNullWhenNoCategory() {
        // Given
        Product productWithoutCategory = Product.builder()
                .name("Test Product")
                .price("1000")
                .saleStatus(SaleStatus.FOR_SALE)
                .conditionStatus(ConditionStatus.NEW)
                .category(null)
                .build();

        // When
        Category major = productWithoutCategory.getMajorCategory();
        Category middle = productWithoutCategory.getMiddleCategory();

        // Then
        assertThat(major).isNull();
        assertThat(middle).isNull();
    }

    @Test
    @DisplayName("상품 이미지 업데이트 테스트")
    void shouldUpdateProductImage() {
        // Given
        ProductImage newImage = ProductImage.builder().imageUrl("https://test.com/new-image.jpg").build();

        // When
        product.updateProductImage(newImage);

        // Then
        assertThat(product.getImage()).isEqualTo(newImage);
    }


    @Test
    @DisplayName("상품 정보 업데이트 테스트")
    void shouldUpdateProductDetails() {
        // Given
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated Product")
                .price("2000")
                .saleStatus(SaleStatus.OUT_OF_STOCK)
                .conditionStatus(ConditionStatus.NEW)
                .categoryId(2L)
                .build();

        Category updatedCategory = mock(Category.class);

        // When
        product.updateProduct(request, updatedCategory);

        // Then
        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getPrice()).isEqualTo("2000");
        assertThat(product.getSaleStatus()).isEqualTo(SaleStatus.OUT_OF_STOCK);
        assertThat(product.getConditionStatus()).isEqualTo(ConditionStatus.NEW);
        assertThat(product.getCategory()).isEqualTo(updatedCategory);
    }
}
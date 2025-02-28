package org.orderhub.pr.product;

import org.orderhub.pr.category.CategoryTestFactory;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;

public class ProductTestFactory {
    public static Product createProduct(Long id, String name, String price) {
        Category defaultCategory = CategoryTestFactory.createMajorCategory(1L, "기본 카테고리");

        return Product.builder()
                .name(name)
                .price(price)
                .image(ProductImage.builder().build())  // 기본 이미지
                .conditionStatus(ConditionStatus.NEW) // 기본 상태
                .category(defaultCategory) // 기본 카테고리
                .build();
    }
}

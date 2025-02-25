package org.orderhub.pr.category;

import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryType;

public class CategoryTestFactory {
    public static Category createCategory(Long id, String name, CategoryType type) {
        return Category.builder()
                .id(id)
                .name(name)
                .type(type)
                .build();
    }

    public static Category createMajorCategory(Long id, String name) {
        return createCategory(id, name, CategoryType.MAJOR);
    }

    public static Category createMinorCategory(Long id, String name, Category parent) {
        Category minorCategory = createCategory(id, name, CategoryType.MINOR);
        minorCategory.setParent(parent);
        return minorCategory;
    }
}

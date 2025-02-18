package org.orderhub.pr.category.repository;

import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentIsNullAndStatus(CategoryStatus status);
}

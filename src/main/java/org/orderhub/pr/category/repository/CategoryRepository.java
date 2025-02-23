package org.orderhub.pr.category.repository;

import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentIsNullAndStatus(CategoryStatus status);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findAllWithChildren();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL AND c.status = :status")
    List<Category> findWithChildrenAndStatus(@Param("status") CategoryStatus status);
}

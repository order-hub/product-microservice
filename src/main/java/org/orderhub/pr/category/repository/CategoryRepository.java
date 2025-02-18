package org.orderhub.pr.category.repository;

import org.orderhub.pr.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

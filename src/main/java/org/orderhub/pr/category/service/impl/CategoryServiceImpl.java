package org.orderhub.pr.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.category.dto.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.CategoryRegisterResponse;
import org.orderhub.pr.category.exception.ExceptionMessage;
import org.orderhub.pr.category.repository.CategoryRepository;
import org.orderhub.pr.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryRegisterResponse categoryRegister(CategoryRegisterRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .parent(request.getParentCategoryId() == null ? null : findById(request.getParentCategoryId()))
                .type(CategoryType.fromString(request.getCategoryType()))
                .build();

        Category savedCategory = categoryRepository.save(category);

        return CategoryRegisterResponse.of(savedCategory);
    }

    private Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ExceptionMessage.NO_SUCH_CATEGORY));
    }

}

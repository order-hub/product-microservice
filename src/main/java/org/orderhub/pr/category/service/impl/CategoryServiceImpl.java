package org.orderhub.pr.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.domain.CategoryStatus;
import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.category.dto.request.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.request.CategoryUpdateRequest;
import org.orderhub.pr.category.dto.response.CategoryRegisterResponse;
import org.orderhub.pr.category.dto.response.CategoryTreeResponse;
import org.orderhub.pr.category.dto.response.CategoryUpdateResponse;
import org.orderhub.pr.category.exception.ExceptionMessage;
import org.orderhub.pr.category.repository.CategoryRepository;
import org.orderhub.pr.category.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryTreeResponse> getAllCategories() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return rootCategories.stream()
                .map(CategoryTreeResponse::new)
                .collect(Collectors.toList());
    }

    public List<CategoryTreeResponse> getAllCategoriesByActive() {
        List<Category> rootCategories = categoryRepository.findByParentIsNullAndStatus(CategoryStatus.ACTIVE);
        return rootCategories.stream()
                .map(CategoryTreeResponse::new)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public CategoryUpdateResponse categoryUpdate(CategoryUpdateRequest request) {
        Category currentCategory = findById(request.getId());

        Category parentCategory = (request.getParentCategoryId() != null)
                ? findById(request.getParentCategoryId())
                : null;

        currentCategory.applyUpdate(
                request.getName(),
                CategoryType.fromString(request.getCategoryType()),
                parentCategory
        );

        return CategoryUpdateResponse.of(currentCategory);
    }

    @Transactional
    public void categoryDelete(Long id) {
        Category category = findById(id);
        category.delete();
    }

    @Transactional
    public void categoryRestore(Long id) {
        Category category = findById(id);
        category.restore();
    }
}

package org.orderhub.pr.category.service;

import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.dto.request.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.request.CategoryUpdateRequest;
import org.orderhub.pr.category.dto.response.CategoryRegisterResponse;
import org.orderhub.pr.category.dto.response.CategoryTreeResponse;
import org.orderhub.pr.category.dto.response.CategoryUpdateResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeResponse> getAllCategories();
    List<CategoryTreeResponse> getAllCategoriesByActive();
    public CategoryRegisterResponse categoryRegister(CategoryRegisterRequest request);
    public CategoryUpdateResponse categoryUpdate(CategoryUpdateRequest request);
    public void categoryDelete(Long id);
    public void categoryRestore(Long id);
    public Category findById(Long id);

}

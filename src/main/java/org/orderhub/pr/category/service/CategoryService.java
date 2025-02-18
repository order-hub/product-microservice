package org.orderhub.pr.category.service;

import org.orderhub.pr.category.dto.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.CategoryRegisterResponse;

public interface CategoryService {

    public CategoryRegisterResponse categoryRegister(CategoryRegisterRequest request);

}

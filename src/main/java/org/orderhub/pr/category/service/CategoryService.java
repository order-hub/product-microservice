package org.orderhub.pr.category.service;

import org.orderhub.pr.category.dto.request.CategoryRegisterRequest;
import org.orderhub.pr.category.dto.response.CategoryRegisterResponse;

public interface CategoryService {

    public CategoryRegisterResponse categoryRegister(CategoryRegisterRequest request);

}

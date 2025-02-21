package org.orderhub.pr.product.service;

import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductImageUpdateRequest;

import java.io.IOException;

public interface ProductImageUploadService {
    String registerProductImage(ProductImageRegisterRequest productImageRegisterRequest) throws IOException;
    String updateProductImage(ProductImageUpdateRequest productImageUpdateRequest) throws IOException;
}

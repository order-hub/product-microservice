package org.orderhub.pr.product.service;

import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;

import java.io.IOException;

public interface ProductImageService {
    void processProductImage(ProductImageRegisterRequest request) throws IOException;
}

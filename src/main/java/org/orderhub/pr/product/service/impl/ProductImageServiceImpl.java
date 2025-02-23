package org.orderhub.pr.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.ProductImage;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductImageUpdateRequest;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.orderhub.pr.product.service.ProductImageUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.orderhub.pr.product.exception.ExceptionMessage.PRODUCT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageUploadService productImageUploadService;
    private final ProductRepository productRepository;

    @Transactional
    public void processProductImage(ProductImageRegisterRequest request) throws IOException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND));

        String imageUrl = productImageUploadService.registerProductImage(request);

        product.updateProductImage(ProductImage.builder()
                .imageUrl(imageUrl)
                .build());

        productRepository.save(product);
    }

    @Transactional
    public void updateProductImage(ProductImageUpdateRequest request) throws IOException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND));

        String imageUrl = productImageUploadService.updateProductImage(request);

        product.updateProductImage(ProductImage.builder()
                .imageUrl(imageUrl)
                .build());

        productRepository.save(product);
    }
}

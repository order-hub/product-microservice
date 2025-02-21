package org.orderhub.pr.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomProductRepository customProductRepository;

    public Page<ProductResponse> getProductByPage(Pageable pageable, ProductSearchRequest searchRequest) {
        Page<Product> productPage = customProductRepository.searchProducts(searchRequest, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();
        return new PageImpl<>(productResponses, pageable, productPage.getTotalElements());
    }



}

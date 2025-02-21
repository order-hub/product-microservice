package org.orderhub.pr.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.category.service.CategoryService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomProductRepository customProductRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;

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

    @Transactional
    public ProductResponse createProduct(ProductRegisterRequest request, MultipartFile productImage) {
        Product product = Product.builder()
                .name(request.getName())
                .category(categoryService.findById(request.getCategoryId()))
                .conditionStatus(request.getConditionStatus())
                .saleStatus(request.getSaleStatus())
                .build();
        productRepository.save(product);

        eventPublisher.publishEvent(ProductCreatedEvent.builder()
                .imageRequest(
                    ProductImageRegisterRequest.builder()
                            .productId(product.getId())
                            .image(productImage)
                            .build()
                )
                .build());
        return ProductResponse.from(product);
    }





}

package org.orderhub.pr.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.category.service.CategoryService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.SaleStatus;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.dto.request.*;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.orderhub.pr.product.service.ProductService;
import org.orderhub.pr.product.service.producer.ProductEventProducer;
import org.orderhub.pr.util.dto.InMemoryFile;
import org.orderhub.pr.util.service.InMemoryFileStorage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.orderhub.pr.product.exception.ExceptionMessage.PRODUCT_NOT_FOUND;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CustomProductRepository customProductRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;
    private final ProductEventProducer productEventProducer;

    public List<Product> findAllById(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    public Page<ProductResponse> getProductByPage(Pageable pageable, ProductSearchRequest searchRequest) {
        Page<Product> productPage = customProductRepository.searchProducts(searchRequest, pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .filter(p -> !p.getSaleStatus().equals(SaleStatus.DELETED))
                .map(ProductResponse::from)
                .toList();
        return new PageImpl<>(productResponses, pageable, productResponses.size());
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .filter(p -> !p.getSaleStatus().equals(SaleStatus.DELETED))
                .map(ProductResponse::from)
                .toList();
        return new PageImpl<>(productResponses, pageable, productResponses.size());
    }

    public Page<ProductResponse> getDeletedProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .filter(p -> p.getSaleStatus().equals(SaleStatus.DELETED))
                .map(ProductResponse::from)
                .toList();
        return new PageImpl<>(productResponses, pageable, productResponses.size());
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ProductResponse createProduct(ProductRegisterRequest request, MultipartFile productImage) throws IOException {
        Product product = Product.builder()
                .name(request.getName())
                .category(categoryService.findById(request.getCategoryId()))
                .conditionStatus(request.getConditionStatus())
                .saleStatus(request.getSaleStatus())
                .price(request.getPrice())
                .build();
        productRepository.save(product);

        if (productImage != null && !productImage.isEmpty()) {
            InMemoryFile inMemoryFile = InMemoryFile.builder()
                    .originalFilename(productImage.getOriginalFilename())
                    .contentType(productImage.getContentType())
                    .content(productImage.getBytes())
                    .build();

            ProductImageRegisterRequest imageRequest = ProductImageRegisterRequest.builder()
                    .productId(product.getId())
                    .storedFile(inMemoryFile)
                    .build();

            eventPublisher.publishEvent(
                    ProductCreatedEvent.builder()
                            .imageRequest(imageRequest)
                            .build()
            );
        }

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(ProductUpdateRequest request, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND));
        Category category = categoryService.findById(request.getCategoryId());
        product.updateProduct(request, category);

        productEventProducer.sendProductUpdate(
                ProductUpdateEventRequest.builder()
                        .productId(productId)
                        .name(request.getName())
                        .price(request.getPrice())
                        .build()
        );
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND));
        product.deleteProduct();
    }


}

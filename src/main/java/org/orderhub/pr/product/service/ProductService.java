package org.orderhub.pr.product.service;

import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.dto.request.ProductRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.dto.request.ProductUpdateRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Page<ProductResponse> getProductByPage(Pageable pageable, ProductSearchRequest searchRequest);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> getDeletedProducts(Pageable pageable);
    Product getProductById(Long id);
    List<Product> findAllById(List<Long> ids);
    ProductResponse createProduct(ProductRegisterRequest request, MultipartFile productImage) throws IOException;
    ProductResponse updateProduct(ProductUpdateRequest request, Long productId);
    void deleteProduct(Long productId);
}

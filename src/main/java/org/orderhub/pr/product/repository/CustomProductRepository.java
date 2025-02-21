package org.orderhub.pr.product.repository;

import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CustomProductRepository {
    List<Product> findByJsonAttributes(Map<String, Object> attributes);
    public Page<Product> searchProducts(ProductSearchRequest criteria, Pageable pageable);
}

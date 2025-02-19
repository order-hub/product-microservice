package org.orderhub.pr.product.repository;

import org.orderhub.pr.product.domain.Product;

import java.util.List;
import java.util.Map;

public interface CustomProductRepository {
    List<Product> findByJsonAttributes(Map<String, Object> attributes);
}

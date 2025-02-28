package org.orderhub.pr.discount.repository;

import org.orderhub.pr.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleDiscountProduct extends JpaRepository<BundleDiscountProduct, Long> {
    Optional<BundleDiscountProduct> findByProduct(Product product);
}

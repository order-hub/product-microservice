package org.orderhub.pr.discount.repository;

import org.orderhub.pr.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleDiscountProductRepository extends JpaRepository<BundleDiscountProductRepository, Long> {
    Optional<BundleDiscountProductRepository> findByProduct(Product product);
}

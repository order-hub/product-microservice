package org.orderhub.pr.discount.repository;

import org.orderhub.pr.discount.domain.BundleDiscount;
import org.orderhub.pr.discount.domain.BundleDiscountProduct;
import org.orderhub.pr.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleDiscountProductRepository extends JpaRepository<BundleDiscountProduct, Long> {
    Optional<BundleDiscountProductRepository> findByProduct(Product product);
    void deleteByBundleDiscount(BundleDiscount bundleDiscount);
}

package org.orderhub.pr.discount.repository;

import io.micrometer.observation.ObservationFilter;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    Optional<ProductDiscount> findByProduct(Product product);

    Optional<ProductDiscount> findByProductAndStatus(Product product, DiscountStatus status);
    List<ProductDiscount> findAllByStatus(DiscountStatus status);
}

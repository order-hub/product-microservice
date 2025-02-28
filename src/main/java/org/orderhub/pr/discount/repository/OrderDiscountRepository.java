package org.orderhub.pr.discount.repository;

import org.orderhub.pr.discount.domain.OrderDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDiscountRepository extends JpaRepository<OrderDiscount, Long> {
}

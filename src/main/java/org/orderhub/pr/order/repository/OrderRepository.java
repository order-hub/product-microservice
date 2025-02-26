package org.orderhub.pr.order.repository;

import org.orderhub.pr.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

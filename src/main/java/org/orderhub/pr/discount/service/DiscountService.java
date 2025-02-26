package org.orderhub.pr.discount.service;

import org.orderhub.pr.order.domain.OrderItem;

public interface DiscountService {
    Integer applyProductDiscount(OrderItem orderItem);
}

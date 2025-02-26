package org.orderhub.pr.discount.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.discount.service.DiscountService;
import org.orderhub.pr.order.domain.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final ProductDiscountRepository productDiscountRepository;

    public Integer applyProductDiscount(OrderItem orderItem) {
        return productDiscountRepository.findByProduct(orderItem.getProduct())
                .map(discount -> discount.getDiscountType().applyDiscount(discount, orderItem))
                .orElse(0);
    }

}

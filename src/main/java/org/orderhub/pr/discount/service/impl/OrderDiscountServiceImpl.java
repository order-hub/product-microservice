package org.orderhub.pr.discount.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.domain.OrderDiscount;
import org.orderhub.pr.discount.dto.request.OrderDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.OrderDiscountResponse;
import org.orderhub.pr.discount.repository.OrderDiscountRepository;
import org.orderhub.pr.discount.service.OrderDiscountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderDiscountServiceImpl implements OrderDiscountService {

    private final OrderDiscountRepository orderDiscountRepository;

    @Transactional
    public OrderDiscountResponse create(OrderDiscountCreateRequest request) {
        OrderDiscount saved = orderDiscountRepository.save(OrderDiscountCreateRequest.of(request));
        return OrderDiscountResponse.from(saved);
    }


}

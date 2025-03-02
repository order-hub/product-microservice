package org.orderhub.pr.discount.service;

import org.orderhub.pr.discount.dto.request.OrderDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.OrderDiscountResponse;

import java.util.List;

public interface OrderDiscountService {

    OrderDiscountResponse create(OrderDiscountCreateRequest request);
    List<OrderDiscountResponse> findAll();
    List<OrderDiscountResponse> findActiveAll();
    OrderDiscountResponse findById(Long id);
}

package org.orderhub.pr.discount.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.domain.OrderDiscount;
import org.orderhub.pr.discount.dto.request.OrderDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.OrderDiscountResponse;
import org.orderhub.pr.discount.repository.OrderDiscountRepository;
import org.orderhub.pr.discount.service.OrderDiscountService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.orderhub.pr.discount.exception.ExceptionMessage.NOT_FOUND_DISCOUNT;

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

    public List<OrderDiscountResponse> findAll() {
        return orderDiscountRepository.findAll().stream()
                .map(OrderDiscountResponse::from).collect(Collectors.toList());
    }

    public List<OrderDiscountResponse> findActiveAll() {
        return orderDiscountRepository.findAll().stream()
                .filter(OrderDiscount::isActive)
                .map(OrderDiscountResponse::from).collect(Collectors.toList());
    }

    public OrderDiscountResponse findById(Long id) {
        return OrderDiscountResponse.from(orderDiscountRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_DISCOUNT)));
    }




}

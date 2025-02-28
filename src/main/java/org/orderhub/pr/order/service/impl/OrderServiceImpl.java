package org.orderhub.pr.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.order.repository.OrderRepository;
import org.orderhub.pr.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;



}

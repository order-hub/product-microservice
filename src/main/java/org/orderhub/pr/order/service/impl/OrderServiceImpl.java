package org.orderhub.pr.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.order.domain.Order;
import org.orderhub.pr.order.dto.request.OrderCreateRequest;
import org.orderhub.pr.order.dto.request.OrderProducts;
import org.orderhub.pr.order.dto.response.OrderResponse;
import org.orderhub.pr.order.repository.OrderRepository;
import org.orderhub.pr.order.service.OrderService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Order order = Order.builder()
                .memberId(request.getMemberId())
                .build();

        for (OrderProducts orderProducts : request.getProducts()) {
            Product productById = productService.getProductById(orderProducts.getProductId());
            order.addOrderItem(productById, orderProducts.getQuantity(), Integer.parseInt(productById.getPrice()));
        }

        order.updateOrderStatus();

        return OrderResponse.from(orderRepository.save(order));
    }


}

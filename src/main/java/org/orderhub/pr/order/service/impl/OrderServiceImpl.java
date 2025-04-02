package org.orderhub.pr.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.order.domain.Order;
import org.orderhub.pr.order.domain.OrderItem;
import org.orderhub.pr.order.dto.request.*;
import org.orderhub.pr.order.dto.response.OrderResponse;
import org.orderhub.pr.order.repository.OrderRepository;
import org.orderhub.pr.order.service.OrderService;
import org.orderhub.pr.order.service.producer.OrderProducer;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

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
        Order savedOrder = orderRepository.save(order);
        orderProducer.sendOrderCreateEvent(OrderEventRequest.from(savedOrder));
        return OrderResponse.from(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrder(OrderUpdateRequest request) {
        Order order = findOrderById(request.getId());
        for (OrderProducts orderProducts : request.getProducts()) {
            Optional<OrderItem> existingItem = order.getOrderItem(orderProducts.getProductId());
            if (existingItem.isPresent()) {
                order.updateOrderItem(OrderItemUpdateRequest.builder()
                                .orderItemId(existingItem.get().getId())
                                .quantity(orderProducts.getQuantity())
                        .build());
            } else {
                Product productById = productService.getProductById(orderProducts.getProductId());
                order.addOrderItem(productById, orderProducts.getQuantity(), Integer.parseInt(productById.getPrice()));
            }
        }
        order.updateOrderStatus();
        Order savedOrder = orderRepository.save(order);
        orderProducer.sendOrderUpdateEvent(OrderEventRequest.from(savedOrder));
        return OrderResponse.from(order);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public void deleteOrder(Long orderId) {
        Order order = findOrderById(orderId);
        order.delete();
        orderProducer.sendOrderUpdateEvent(OrderEventRequest.from(order));
    }


}

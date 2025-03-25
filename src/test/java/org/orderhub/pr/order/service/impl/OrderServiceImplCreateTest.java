package org.orderhub.pr.order.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.orderhub.pr.category.domain.Category;
import org.orderhub.pr.order.domain.Order;
import org.orderhub.pr.order.dto.request.OrderCreateRequest;
import org.orderhub.pr.order.dto.request.OrderProducts;
import org.orderhub.pr.order.dto.response.OrderResponse;
import org.orderhub.pr.order.repository.OrderRepository;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplCreateTest {
    private ProductService productService;
    private OrderRepository orderRepository;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        orderRepository = mock(OrderRepository.class);
        orderService = new OrderServiceImpl(productService, orderRepository);
    }

    @Test
    void createOrder_shouldCreateOrderAndReturnResponse() {
        // given
        Long memberId = 1L;
        Long productId = 10L;
        int quantity = 2;
        String price = "1000";
        Long categoryId = 100L;

        Category category = Category.builder()
                .id(categoryId)
                .name("test")
                .parent(null)
                .build();

        Product product = Product.builder()
                .name("Test Product")
                .price(price)
                .category(category)
                .build();

        OrderProducts orderProducts = OrderProducts.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(memberId)
                .products(List.of(orderProducts))
                .build();

        when(productService.getProductById(productId)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderResponse response = orderService.createOrder(request);

        // then
        assertNotNull(response);
        assertEquals(memberId, response.getMemberId());
        assertEquals(1, response.getItems().size());
        assertEquals(quantity, response.getItems().get(0).getQuantity());

        verify(productService, times(1)).getProductById(productId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
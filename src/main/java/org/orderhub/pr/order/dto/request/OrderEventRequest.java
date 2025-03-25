package org.orderhub.pr.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.order.domain.Order;
import org.orderhub.pr.order.domain.OrderStatus;
import org.orderhub.pr.order.dto.response.OrderItemResponse;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEventRequest {
    private Long orderId;
    private Long storeId;
    private Long memberId;
    private OrderStatus status;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    public static OrderEventRequest from(Order order) {
        return OrderEventRequest.builder()
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .memberId(order.getMemberId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getOrderItems().stream().map(OrderItemResponse::from).collect(Collectors.toList()))
                .build();
    }

}

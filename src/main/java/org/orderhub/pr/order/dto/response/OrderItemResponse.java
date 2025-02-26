package org.orderhub.pr.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.order.domain.OrderItem;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Integer price;
    private Integer quantity;
    private String imageUrl;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .categoryId(orderItem.getProduct().getCategory().getId())
                .categoryName(orderItem.getProduct().getCategory().getName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .imageUrl(orderItem.getProduct().getImage().getImageUrl())
                .build();
    }

}

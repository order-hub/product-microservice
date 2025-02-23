package org.orderhub.pr.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = OrderStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateOrderStatus() {
        boolean allShipped = orderItems.stream().allMatch(item -> item.getStatus() == OrderItemStatus.SHIPPED);
        boolean anyProcessing = orderItems.stream().anyMatch(item -> item.getStatus() == OrderItemStatus.PROCESSING);

        if (allShipped) {
            this.status = OrderStatus.SHIPPED;
            return;
        }

        if (anyProcessing) {
            this.status = OrderStatus.PROCESSING;
            return;
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        if (orderItem.getStatus() == OrderItemStatus.PENDING) {
            orderItems.remove(orderItem);
            return;
        }
        orderItem.updateOrderItemStatus(OrderItemStatus.CANCELLED);
    }



}

package org.orderhub.pr.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.orderhub.pr.order.dto.request.OrderItemUpdateRequest;
import org.orderhub.pr.order.dto.request.OrderProducts;
import org.orderhub.pr.order.dto.request.OrderUpdateRequest;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long storeId;

    private Long totalPrice = 0L;

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

    @Builder
    public Order(Long memberId) {
        this.memberId = memberId;
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

    public void addOrderItem(Product product, int quantity, int price) {
        OrderItem orderItem = OrderItem.create(this, product, quantity, price);
        this.orderItems.add(orderItem);
        this.totalPrice += (long) price * quantity;
    }

    public void updateOrderItemStatus(Long orderItemId, OrderItemStatus newStatus) {
        this.orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .ifPresent(item -> item.updateStatus(newStatus));
    }

    public void removeOrderItem(OrderItem orderItem) {
        if (orderItem.getStatus() == OrderItemStatus.PENDING) {
            orderItems.remove(orderItem);
            return;
        }
        orderItem.updateOrderItemStatus(OrderItemStatus.CANCELLED);
    }

    public void updateOrderItem(OrderItemUpdateRequest request) {
        orderItems.stream().filter(orderItem -> orderItem.getId().equals(request.getOrderItemId()))
                .findFirst()
                .ifPresent(item -> item.updateQuantity(request.getQuantity()));
    }

    public Optional<OrderItem> getOrderItem(Long orderItemId) {
        return orderItems.stream().filter(orderItem -> orderItem.getId().equals(orderItemId)).findFirst();
    }

    public void delete() {
        this.status = OrderStatus.CANCELLED;
    }



}

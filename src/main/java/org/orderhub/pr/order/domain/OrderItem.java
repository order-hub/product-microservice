package org.orderhub.pr.order.domain;

import jakarta.persistence.*;
import lombok.*;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private Integer price;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Builder
    public OrderItem(Product product, int quantity, int price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.status = OrderItemStatus.PENDING;
    }

    public void updateStatus(OrderItemStatus newStatus) {
        this.status = newStatus;
    }

    public void updateOrderItemStatus(OrderItemStatus newStatus) {
        this.status = newStatus;
    }

    public void deleteOrderItem() {
        if (this.status == OrderItemStatus.PENDING) {
            this.order.getOrderItems().remove(this);
            return;
        }
        this.updateOrderItemStatus(OrderItemStatus.CANCELLED);
    }





}

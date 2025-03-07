package org.orderhub.pr.discount.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue;

    private String provider;

    private Instant startDate;
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    private DiscountStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = DiscountStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Builder
    public OrderDiscount(DiscountType discountType, Integer discountValue, String provider, Instant startDate, Instant endDate) {
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.provider = provider;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isActive() {
        Instant now = Instant.now();
        return (getStartDate().isBefore(now) && getEndDate().isAfter(now));
    }


}

package org.orderhub.pr.discount.domain;

import org.orderhub.pr.order.domain.OrderItem;
import java.time.Instant;

public enum DiscountType {
    FIXED {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0L;
            return (long) Math.min(discount.getDiscountValue(), orderItem.getPrice());
        }
    },
    PERCENTAGE {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0L;
            return (long) (orderItem.getPrice() * (discount.getDiscountValue() / 100.0));
        }
    },
    THRESHOLD_PRICE {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0L;
            if (orderItem.getQuantity() >= discount.getThresholdQuantity()) {
                Long originalTotalPrice = ((long) orderItem.getPrice() * orderItem.getQuantity());
                Long discountedTotalPrice = ((long) discount.getDiscountUnitPrice() * orderItem.getQuantity());
                return originalTotalPrice - discountedTotalPrice;
            }
            return 0L;
        }
    };

    public abstract Long applyDiscount(ProductDiscount discount, OrderItem orderItem);

    private static boolean isActive(ProductDiscount discount) {
        Instant now = Instant.now();
        return discount.getStartDate().isBefore(now) && discount.getEndDate().isAfter(now);
    }
}

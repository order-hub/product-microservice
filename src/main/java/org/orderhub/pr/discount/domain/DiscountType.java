package org.orderhub.pr.discount.domain;

import org.orderhub.pr.category.domain.CategoryType;
import org.orderhub.pr.order.domain.OrderItem;
import java.time.Instant;

public enum DiscountType {
    FIXED {
        @Override
        public Integer applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0;
            return Math.min(discount.getDiscountValue(), orderItem.getPrice());
        }
    },
    PERCENTAGE {
        @Override
        public Integer applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0;
            return (int) (orderItem.getPrice() * (discount.getDiscountValue() / 100.0));
        }
    },
    THRESHOLD_PRICE {
        @Override
        public Integer applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (!isActive(discount)) return 0;
            if (orderItem.getQuantity() >= discount.getThresholdQuantity()) {
                Integer originalTotalPrice = (orderItem.getPrice() * orderItem.getQuantity());
                Integer discountedTotalPrice = (discount.getDiscountUnitPrice() * orderItem.getQuantity());
                return originalTotalPrice - discountedTotalPrice;
            }
            return 0;
        }
    };

    public abstract Integer applyDiscount(ProductDiscount discount, OrderItem orderItem);

    private static boolean isActive(ProductDiscount discount) {
        Instant now = Instant.now();
        return discount.getStartDate().isBefore(now) && discount.getEndDate().isAfter(now);
    }

    public static DiscountType fromString(String string) {
        if (string == null) return null;
        return DiscountType.valueOf(string.toUpperCase());
    }
}

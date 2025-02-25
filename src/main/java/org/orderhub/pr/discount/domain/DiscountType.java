package org.orderhub.pr.discount.domain;

import org.orderhub.pr.order.domain.OrderItem;

public enum DiscountType {
    FIXED {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            return (long) Math.min(discount.getDiscountValue(), orderItem.getPrice());
        }
    },
    PERCENTAGE {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            return (long) (orderItem.getPrice() * (discount.getDiscountValue() / 100.0));
        }
    },
    THRESHOLD_PRICE {
        @Override
        public Long applyDiscount(ProductDiscount discount, OrderItem orderItem) {
            if (orderItem.getQuantity() >= discount.getThresholdQuantity()) {
                Long originalTotalPrice = ((long) orderItem.getPrice() * orderItem.getQuantity());
                Long discountedTotalPrice = ((long) discount.getDiscountUnitPrice() * orderItem.getQuantity());
                return originalTotalPrice - discountedTotalPrice;
            }
            return 0L;
        }
    };

    public abstract Long applyDiscount(ProductDiscount discount, OrderItem orderItem);
}

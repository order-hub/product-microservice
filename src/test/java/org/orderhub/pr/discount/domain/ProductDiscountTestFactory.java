package org.orderhub.pr.discount.domain;

import org.orderhub.pr.product.domain.Product;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ProductDiscountTestFactory {
    public static ProductDiscount createProductDiscount(Product product, DiscountType discountType,
                                                        Integer discountValue, Integer thresholdQuantity,
                                                        Integer discountUnitPrice) {
        return ProductDiscount.builder()
                .product(product)
                .discountType(discountType)
                .discountValue(discountValue)
                .thresholdQuantity(thresholdQuantity)
                .discountUnitPrice(discountUnitPrice)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS)) // 기본값: 1일 전 시작
                .endDate(Instant.now().plus(1, ChronoUnit.DAYS)) // 기본값: 1일 후 종료
                .build();
    }

    public static ProductDiscount createFixedDiscount(Product product, int discountValue) {
        return createProductDiscount(product, DiscountType.FIXED, discountValue, null, null);
    }

    public static ProductDiscount createPercentageDiscount(Product product, int discountValue) {
        return createProductDiscount(product, DiscountType.PERCENTAGE, discountValue, null, null);
    }

    public static ProductDiscount createThresholdPriceDiscount(Product product, int thresholdQuantity, int discountUnitPrice) {
        return createProductDiscount(product, DiscountType.THRESHOLD_PRICE, null, thresholdQuantity, discountUnitPrice);
    }
}

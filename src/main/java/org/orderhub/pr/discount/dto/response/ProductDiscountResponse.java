package org.orderhub.pr.discount.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.product.domain.Product;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscountResponse {
    private Long productDiscountId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private String productDiscountType;
    private Integer discountValue;

    private Integer thresholdQuantity; // N개 이상 구매 시 적용될 개수(THRESHOLD_PRICE 전용)
    private Integer discountUnitPrice; // N개 이상 구매 시 적용될 개당 가격(THRESHOLD_PRICE 전용)

    private Instant startDate;
    private Instant endDate;

    private Instant createdAt;
    private Instant updatedAt;

    public static ProductDiscountResponse from(ProductDiscount productDiscount) {
        return ProductDiscountResponse.builder()
                .productDiscountId(productDiscount.getId())
                .productId(productDiscount.getProduct().getId())
                .productName(productDiscount.getProduct().getName())
                .productImageUrl(productDiscount.getProduct().getImage().getImageUrl())
                .productDiscountType(productDiscount.getDiscountType().name())
                .discountValue(productDiscount.getDiscountValue())
                .thresholdQuantity(productDiscount.getThresholdQuantity())
                .discountUnitPrice(productDiscount.getDiscountUnitPrice())
                .startDate(productDiscount.getStartDate())
                .endDate(productDiscount.getEndDate())
                .createdAt(productDiscount.getCreatedAt())
                .updatedAt(productDiscount.getUpdatedAt())
                .build();
    }

}

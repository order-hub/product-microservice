package org.orderhub.pr.discount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.discount.domain.OrderDiscount;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDiscountResponse {

    private Long id;
    private String discountType;
    private Integer discountAmount;
    private String provider;
    private Instant startDate;
    private Instant endDate;
    private Instant createdAt;
    private Instant updatedAt;

    public static OrderDiscountResponse from(OrderDiscount orderDiscount) {
        return OrderDiscountResponse.builder()
                .id(orderDiscount.getId())
                .discountType(orderDiscount.getDiscountType().name())
                .discountAmount(orderDiscount.getDiscountValue())
                .provider(orderDiscount.getProvider())
                .startDate(orderDiscount.getStartDate())
                .endDate(orderDiscount.getEndDate())
                .createdAt(orderDiscount.getCreatedAt())
                .updatedAt(orderDiscount.getUpdatedAt())
                .build();
    }
}

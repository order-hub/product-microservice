package org.orderhub.pr.discount.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.OrderDiscount;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDiscountCreateRequest {

    private String discountType;
    private Integer discountAmount;
    private String provider;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant endDate;

    public OrderDiscount of(OrderDiscountCreateRequest request) {
        return OrderDiscount.builder()
                .discountType(DiscountType.fromString(request.getDiscountType()))
                .discountValue(request.getDiscountAmount())
                .provider(request.getProvider())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

}

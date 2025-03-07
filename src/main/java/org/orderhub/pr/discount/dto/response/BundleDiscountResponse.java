package org.orderhub.pr.discount.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.DiscountStatus;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleDiscountResponse {
    private Long id;
    private Integer discountValue;
    private DiscountType discountType;
    private List<Long> productIds;
    private Instant startDate;
    private Instant endDate;
    private DiscountStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

package org.orderhub.pr.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.SaleStatus;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String name;
    private Long categoryId;
    private SaleStatus saleStatus;
    private ConditionStatus conditionStatus;
    private String price;
    private Map<String, Object> attributes;
}

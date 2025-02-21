package org.orderhub.pr.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.SaleStatus;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegisterRequest {
    private String name;
    private Long categoryId;
    private SaleStatus saleStatus;
    private ConditionStatus conditionStatus;
    private Long price;
    private Map<String, Object> attributes;
}

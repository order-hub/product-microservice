package org.orderhub.pr.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.product.domain.ConditionStatus;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.SaleStatus;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private SaleStatus saleStatus;
    private ConditionStatus conditionStatus;
    private String price;
    private Map<String, Object> attributes;
    private String imageUrl;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .saleStatus(product.getSaleStatus())
                .conditionStatus(product.getConditionStatus())
                .price(product.getPrice())
                .attributes(product.getAttributes())
                .imageUrl(product.getImage() == null ? null : product.getImage().getImageUrl())
                .build();
    }
}

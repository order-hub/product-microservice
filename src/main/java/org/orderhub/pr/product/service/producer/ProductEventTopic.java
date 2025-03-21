package org.orderhub.pr.product.service.producer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductEventTopic {
    public static final String PRODUCT_UPDATED = "product-updated";
}

package org.orderhub.pr.product.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ProductImage {
    private String imageUrl;

    @Builder
    public ProductImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

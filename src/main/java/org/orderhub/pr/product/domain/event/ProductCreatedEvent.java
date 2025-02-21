package org.orderhub.pr.product.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class ProductCreatedEvent {

    private final Long productId;
    private final MultipartFile multipartFile;

}

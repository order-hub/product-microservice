package org.orderhub.pr.product.service.listener;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.service.ProductImageService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductImageService productImageService;

    @Async
    @EventListener
    public void handleProductCreated(ProductCreatedEvent event) throws IOException {
        productImageService.processProductImage(event.getImageRequest());
    }

}

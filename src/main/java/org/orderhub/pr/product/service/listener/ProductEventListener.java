package org.orderhub.pr.product.service.listener;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.event.ProductCreatedEvent;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductImageService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    @Async
    @EventListener
    @Transactional
    public void handleProductCreated(ProductCreatedEvent event) {

    }



}

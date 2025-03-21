package org.orderhub.pr.product.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderhub.pr.product.dto.request.ProductUpdateEventRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.orderhub.pr.product.service.producer.ProductEventTopic.PRODUCT_UPDATED;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendProductUpdate(ProductUpdateEventRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(PRODUCT_UPDATED, message);
            log.info("Sent product update event: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize product update event", e);
        }
    }

}

package org.orderhub.pr.order.service.producer;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.order.dto.request.OrderEventRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreateEvent(OrderEventRequest orderEventRequest) {
        kafkaTemplate.send("order-created", orderEventRequest);
    }

    public void sendOrderUpdateEvent(OrderEventRequest orderEventRequest) {
        kafkaTemplate.send("order-updated", orderEventRequest);
    }

}

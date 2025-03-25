package org.orderhub.pr.product.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orderhub.pr.product.dto.request.ProductUpdateEventRequest;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductEventProducerTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private ProductEventProducer productEventProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
        productEventProducer = new ProductEventProducer(kafkaTemplate, objectMapper);
    }

    @Test
    void sendProductUpdate_shouldSendSerializedMessageToKafka() {
        // given
        ProductUpdateEventRequest request = ProductUpdateEventRequest.builder()
                .productId(1L)
                .name("테스트 상품")
                .price("10000")
                .build();

        // when
        assertDoesNotThrow(() -> productEventProducer.sendProductUpdate(request));

        // then
        verify(kafkaTemplate, times(1))
                .send(eq(ProductEventTopic.PRODUCT_UPDATED), anyString());
    }

    @Test
    void sendProductUpdate_shouldHandleJsonProcessingException() throws JsonProcessingException {
        // given
        ProductUpdateEventRequest request = mock(ProductUpdateEventRequest.class);

        // ObjectMapper가 JsonProcessingException을 던지도록 설정
        ObjectMapper faultyMapper = mock(ObjectMapper.class);
        productEventProducer = new ProductEventProducer(kafkaTemplate, faultyMapper);

        when(faultyMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON 오류") {});

        // when & then
        assertDoesNotThrow(() -> productEventProducer.sendProductUpdate(request));

        // 예외가 발생해도 KafkaTemplate.send는 호출되지 않아야 함
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

}
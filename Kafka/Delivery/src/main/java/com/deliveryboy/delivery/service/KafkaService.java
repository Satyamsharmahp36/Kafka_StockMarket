package com.deliveryboy.delivery.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebSocketHandler webSocketHandler;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, WebSocketHandler webSocketHandler) {
        this.kafkaTemplate = kafkaTemplate;
        this.webSocketHandler = webSocketHandler;
    }

    public void updateLocation(String message) {
        kafkaTemplate.send("stock-updates", message);  // ✅ Now, this will work
    }

    @KafkaListener(topics = "stock-updates", groupId = "stock-group")
    public void listenStockUpdates(String stockUpdate) {
        System.out.println("Received Stock Update from Kafka: " + stockUpdate);
        webSocketHandler.sendMessage("Stock Update: " + stockUpdate);
    }
}

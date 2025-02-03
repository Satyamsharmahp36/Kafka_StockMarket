package com.deliveryboy.delivery.websocket;

import com.deliveryboy.delivery.config.AppConstants;
import org.springframework.lang.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class KafkaConsumerService {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @KafkaListener(topics = AppConstants.LOCATION_TOPIC_NAME, groupId = AppConstants.GROUP_ID)
    public void consumeStockUpdates(@NonNull String stockUpdate) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(stockUpdate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void registerSession(@NonNull WebSocketSession session) {
        sessions.add(session);
    }
}

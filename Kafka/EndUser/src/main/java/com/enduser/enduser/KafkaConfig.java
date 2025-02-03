import com.enduser.enduser.AppConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class KafkaConfig extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @KafkaListener(topics = AppConstants.Location_UPDATE_TOPIC, groupId = AppConstants.GROUP_ID)
    public void updateLocation(String value) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Received Stock Price: " + value);
    }
}
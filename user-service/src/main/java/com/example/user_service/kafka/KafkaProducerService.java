package com.example.user_service.kafka;

import com.example.common_models.event.UserEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 16-04-2026
 * Description: the class which is responsible for sending an event when a user is created or deleted
 */
@Getter
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    private final String topic;

    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate,
                                @Value("${app.kafka.user-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * @ Method Name: sendMessage
     * @ Description: Sends a message to Kafka with information about user's event and handles the error if something proceeded wrong
     * @ param      : [com.example.common_models.event.UserEvent]
     * @ return     : void
     */
    public void sendMessage(UserEvent userEvent) {
        try {
            kafkaTemplate.send(topic, userEvent).get();
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }
}

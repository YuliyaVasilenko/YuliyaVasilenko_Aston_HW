package com.example.userservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 16-04-2026
 * Description: the class which is responsible for sending an event when a user is created or deleted
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    private final String topic;

    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate,
                                @Value("${app.kafka.user-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(UserEvent userEvent) {
        logger.info("Received a request to send to Kafka event: {}", userEvent);
        try {
            kafkaTemplate.send(topic, userEvent.getEmail(), userEvent).get();
            logger.info("Sent to Kafka event: {}", userEvent);
        } catch (Exception exception) {
            logger.warn("Failed to send to Kafka event: {}", userEvent);
            throw new RuntimeException();
        }
    }
}

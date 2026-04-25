package com.example.user_service.service;

import com.example.common_models.event.UserEvent;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 22-04-2026
 * Description: вспомогательный класс консьюмера для интеграционных тестов для класса UserService (взаимодействие с Kafka)
 */
@Getter
@Component
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    private UserEvent receivedEvent;

    public void reset() {
        latch = new CountDownLatch(1);
        receivedEvent = null;
    }

    @KafkaListener(topics = "${app.kafka.user-events-topic}")
    public void consumeUserEvent(UserEvent event) {
        this.receivedEvent = event;
        latch.countDown();
    }
}

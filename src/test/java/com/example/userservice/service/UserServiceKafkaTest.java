package com.example.userservice.service;

import com.example.userservice.BaseIntegrationTest;
import com.example.userservice.kafka.KafkaProducerService;
import com.example.userservice.kafka.UserEvent;
import com.example.userservice.kafka.UserOperation;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 19-04-2026
 * Description: интеграционные тесты для класса UserService (взаимодействие с Kafka)
 */
@SpringBootTest
public class UserServiceKafkaTest extends BaseIntegrationTest {

    private static final String TOPIC = "test-user-events";

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public KafkaTemplate<String, UserEvent> createKafkaTemplate(String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, UserEvent> factory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(factory);
    }

    private Consumer<String, UserEvent> createConsumer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(properties);
    }

    @Test
    void sendMessage() {
        String bootstrapServers = kafka.getBootstrapServers();
        KafkaProducerService service = new KafkaProducerService(createKafkaTemplate(bootstrapServers), TOPIC);
        service.sendMessage(new UserEvent(UserOperation.CREATE, "test@email.com"));
        //kafkaProducerService.sendMessage(new UserEvent(UserOperation.CREATE, "test@email.com"));

        Consumer<String, UserEvent> consumer = createConsumer(bootstrapServers);
        consumer.subscribe(Arrays.asList(TOPIC));
        ConsumerRecords<String, UserEvent> records = consumer.poll(Duration.ofMillis(100));
        consumer.close();

        assertNotNull(records);
    }
}

package com.example.notificationprocessor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {
    public static final String TOPIC = "notifications";
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationPublisher(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(NotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.eventId(), event);
    }
}

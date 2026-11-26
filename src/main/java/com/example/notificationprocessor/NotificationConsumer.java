package com.example.notificationprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    public static final String DLQ_TOPIC = "notifications.dlq";
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final IdempotencyService idempotencyService;
    private final ExternalNotificationService externalService;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationConsumer(IdempotencyService idempotencyService,
                                ExternalNotificationService externalService,
                                KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.idempotencyService = idempotencyService;
        this.externalService = externalService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = NotificationPublisher.TOPIC, groupId = "notification-processor")
    public void consume(NotificationEvent event) {
        if (!idempotencyService.tryStart(event.eventId())) {
            log.info("Duplicate notification ignored eventId={}", event.eventId());
            return;
        }

        try {
            externalService.send(event);
            log.info("Notification processed eventId={}", event.eventId());
        } catch (RuntimeException ex) {
            idempotencyService.release(event.eventId());
            log.error("Notification processing failed eventId={}", event.eventId(), ex);
            kafkaTemplate.send(DLQ_TOPIC, event.eventId(), event);
        }
    }
}

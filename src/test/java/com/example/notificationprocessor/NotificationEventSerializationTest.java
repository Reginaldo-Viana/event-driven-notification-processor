package com.example.notificationprocessor;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEventSerializationTest {
    @Test
    void serializesNotificationEventWithInstant() {
        NotificationEvent event = new NotificationEvent(
                "event-123",
                "user@example.com",
                "EMAIL",
                "Hello",
                Instant.parse("2026-11-26T23:45:00Z"));

        byte[] serialized = new JsonSerializer<NotificationEvent>()
                .serialize(NotificationPublisher.TOPIC, event);

        assertThat(new String(serialized))
                .contains("\"eventId\":\"event-123\"")
                .contains("\"createdAt\":");
    }
}

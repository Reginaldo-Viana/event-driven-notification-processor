package com.example.notificationprocessor;

import java.time.Instant;

public record NotificationEvent(
        String eventId,
        String recipient,
        String channel,
        String message,
        Instant createdAt) {
}

package com.example.notificationprocessor;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalNotificationService {
    private static final Logger log = LoggerFactory.getLogger(ExternalNotificationService.class);

    @Retry(name = "notifications")
    @CircuitBreaker(name = "notifications", fallbackMethod = "fallback")
    public void send(NotificationEvent event) {
        if ("FAIL".equalsIgnoreCase(event.channel())) {
            throw new NotificationDeliveryException("Simulated provider failure", null);
        }
        log.info("Sent notification eventId={} recipient={} channel={}", event.eventId(), event.recipient(), event.channel());
    }

    private void fallback(NotificationEvent event, Throwable throwable) {
        throw new NotificationDeliveryException("External notification provider unavailable", throwable);
    }
}

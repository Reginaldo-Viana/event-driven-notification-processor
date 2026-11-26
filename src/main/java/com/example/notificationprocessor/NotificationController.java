package com.example.notificationprocessor;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationPublisher publisher;

    public NotificationController(NotificationPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CreateNotificationRequest request) {
        String eventId = UUID.randomUUID().toString();
        publisher.publish(new NotificationEvent(eventId, request.recipient(), request.channel(), request.message(), Instant.now()));
        return ResponseEntity.accepted().body(Map.of("eventId", eventId, "status", "PENDING"));
    }
}

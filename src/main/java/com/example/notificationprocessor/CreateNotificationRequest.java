package com.example.notificationprocessor;

import jakarta.validation.constraints.NotBlank;

public record CreateNotificationRequest(
        @NotBlank String recipient,
        @NotBlank String channel,
        @NotBlank String message) {
}

package com.example.notificationprocessor;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name(NotificationPublisher.TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationsDlqTopic() {
        return TopicBuilder.name(NotificationConsumer.DLQ_TOPIC).partitions(3).replicas(1).build();
    }
}

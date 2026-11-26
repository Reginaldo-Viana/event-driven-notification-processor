package com.example.notificationprocessor;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {
    @Test
    void createsAndPublishesNotification() throws Exception {
        NotificationPublisher publisher = mock(NotificationPublisher.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new NotificationController(publisher)).build();

        mvc.perform(post("/api/notifications")
                        .contentType("application/json")
                        .content("{\"recipient\":\"user@example.com\",\"channel\":\"EMAIL\",\"message\":\"Hello\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));

        ArgumentCaptor<NotificationEvent> event = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(publisher).publish(event.capture());
        assertThat(event.getValue().recipient()).isEqualTo("user@example.com");
    }
}

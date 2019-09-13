package com.example.springboot.gcp.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PubSubAsyncService {

    private final PubSubTemplate template;

    public PubSubAsyncService(PubSubTemplate template) {
        this.template = template;
    }

    public void publishAsync(String message) {
        this.template.publish("greetings-topic", "greetings Message: " + message + "!");
    }
}

package com.example.springboot.gcp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PubSubAsyncService {

    static private final Logger logger = LoggerFactory.getLogger(OrdersPubSubService.class);


    private final PubSubTemplate template;

    public PubSubAsyncService(PubSubTemplate template) {
        this.template = template;
    }

    void publishAsync(String message){
        this.template.publish("greetings-topic", "greetings Message: " + message + "!");
    }
}

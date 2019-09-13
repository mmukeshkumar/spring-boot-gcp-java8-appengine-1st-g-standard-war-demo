package com.example.springboot.gcp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@RestController
public class OrdersPubSubService {

    static private final Logger logger = LoggerFactory.getLogger(OrdersPubSubService.class);


    private final PubSubTemplate template;

    public OrdersPubSubService(PubSubTemplate template) {
        this.template = template;
    }

    @PostMapping("/greet/{message}")
    ListenableFuture<String> greet(@PathVariable String message) {
        logger.info("Publishing greeting message: {} to topic: {}", message, "greetings-topic");
        return this.template.publish("greetings-topic", "greetings Message: " + message + "!");
    }


    @Component
// This will run on application startup
    class ConsumerRunner implements ApplicationRunner {

        private final PubSubTemplate pubSubTemplate;

        ConsumerRunner(PubSubTemplate pubSubTemplate) {
            this.pubSubTemplate = pubSubTemplate;
        }

        @Override
        public void run(ApplicationArguments args) {
            logger.info("Running  ConsumerRunner: ");
            pubSubTemplate.subscribe("greetings-subscription", new Consumer<BasicAcknowledgeablePubsubMessage>() {
                @Override
                public void accept(BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage) {
                    logger.info("Received message: " + basicAcknowledgeablePubsubMessage.getPubsubMessage().toString());
                    basicAcknowledgeablePubsubMessage.ack();
                }
            });
        }
    }


}

package com.example.springboot.gcp.web;

import com.example.springboot.gcp.model.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@RestController
public class OrdersPubSubService {

    static private final Logger logger = LoggerFactory.getLogger(OrdersPubSubService.class);


    private final PubSubTemplate template;
    private final PubSubAsyncService pubSubAsyncService;

    public OrdersPubSubService(PubSubTemplate template, PubSubAsyncService pubSubAsyncService) {
        this.pubSubAsyncService = pubSubAsyncService;
        this.template = template;
    }

    @PostMapping("/greet")
    @ResponseStatus(HttpStatus.CREATED)
    public String greet(@RequestBody Greeting greeting) {
        logger.info("Publishing greeting message: {} to topic: {}", greeting.getMessage(), "greetings-topic");
        pubSubAsyncService.publishAsync(greeting.getMessage());
        return "success";

    }


    @PostMapping("/greetAsync")
    @ResponseStatus(HttpStatus.CREATED)
    public WebAsyncTask<String> greetAsync(@RequestBody Greeting greeting) {
        logger.info("Publishing greeting message: {} to topic: {}", greeting.getMessage(), "greetings-topic");
        Callable callable = new Callable() {
            @Override
            public String call() throws Exception {
                pubSubAsyncService.publishAsync(greeting.getMessage());
                return "success";
            }
        };
        ConcurrentTaskExecutor t = new ConcurrentTaskExecutor(
                Executors.newFixedThreadPool(1));
        return new WebAsyncTask<>(10000L, t, callable);
    }

/*
    @PostMapping("/greet/{message}")
    ListenableFuture<String> greet(@PathVariable String message) {
        logger.info("Publishing greeting message: {} to topic: {}", message, "greetings-topic");
        return this.template.publish("greetings-topic", "greetings Message: " + message + "!");
    }*/


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

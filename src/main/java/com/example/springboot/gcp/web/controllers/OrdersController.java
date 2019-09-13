package com.example.springboot.gcp.web.controllers;

import com.example.springboot.gcp.model.Greeting;
import com.example.springboot.gcp.model.Order;
import com.example.springboot.gcp.model.OrderRepository;
import com.example.springboot.gcp.services.SpannerOrdersDbService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RequestMapping("/orders")
@RestController
@Slf4j
public class OrdersController {

    static private final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private final RestTemplate restTemplate;
    private final String ordersPaymentsApprovalBaseUrl;
    private final OrdersPaymentsController ordersPaymentsController;
    private final OrdersPubSubController ordersPubSubController;
    private final SpannerOrdersDbService spannerOrdersDbService;

    public OrdersController(OrderRepository orderRepository, @Value("${retailapi.ordersPaymentsApprovalBaseUrl}") String ordersPaymentsApprovalBaseUrl, OrdersPaymentsController ordersPaymentsController, RestTemplate restTemplate, OrdersPubSubController ordersPubSubController, SpannerOrdersDbService spannerOrdersDbService) {
        this.ordersPaymentsApprovalBaseUrl = ordersPaymentsApprovalBaseUrl;
        this.ordersPaymentsController = ordersPaymentsController;
        this.restTemplate = restTemplate;
        this.ordersPubSubController = ordersPubSubController;
        this.spannerOrdersDbService = spannerOrdersDbService;
    }

    /**
     * Create Order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@Valid @RequestBody Order order) {
        String orderId = spannerOrdersDbService.createOrder(order);
        ordersPaymentsController.getPaymentApprovalFromPaymentService(orderId);
        Greeting greeting = new Greeting("Hello");
        ordersPubSubController.greet(greeting);
        return orderId;
    }

    /**
     * Read single Order
     */
    @GetMapping(value = "/{orderId}")
    public Order findOrder(@PathVariable("orderId") String orderId) {
        log.info("Getting Order ID: {}", orderId);
        return spannerOrdersDbService.findOrder(orderId);
    }

    /**
     * Delete an Order
     */
    @DeleteMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(
            @PathVariable("orderId") String orderId) {
        log.info("Deleting Order ID: {}", orderId);
        spannerOrdersDbService.deleteOrder(orderId);
    }

    /**
     * Delete all Orders
     */
    @DeleteMapping(value = "/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllOrders() {
        log.info("Deleting all order: {}");
        spannerOrdersDbService.deleteAllOrders();
    }

    /**
     * Read List of Orders
     */
    @GetMapping
    public Iterable<Order> getAllOrders() throws ExecutionException, InterruptedException {
        log.info("sending a greeting from inside GET /orders");
        return spannerOrdersDbService.getAllOrders();
    }


}

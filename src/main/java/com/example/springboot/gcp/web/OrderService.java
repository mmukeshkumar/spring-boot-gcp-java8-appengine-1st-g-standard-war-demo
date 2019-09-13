package com.example.springboot.gcp.web;

import com.example.springboot.gcp.model.Order;
import com.example.springboot.gcp.model.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping("/orders")
@RestController
@Slf4j
public class OrderService {

    static private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final String ordersPaymentsApprovalBaseUrl;
    private final OrderPaymentsService orderPaymentsService;
    private final OrdersPubSubService ordersPubSubService;

    public OrderService(OrderRepository orderRepository, @Value("${retailapi.ordersPaymentsApprovalBaseUrl}") String ordersPaymentsApprovalBaseUrl, OrderPaymentsService orderPaymentsService, RestTemplate restTemplate, OrdersPubSubService ordersPubSubService) {
        this.orderRepository = orderRepository;
        this.ordersPaymentsApprovalBaseUrl = ordersPaymentsApprovalBaseUrl;
        this.orderPaymentsService = orderPaymentsService;
        this.restTemplate = restTemplate;
        this.ordersPubSubService = ordersPubSubService;
    }

    /**
     * Create Order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@Valid @RequestBody Order order, boolean skipPamentCall) {
        String orderId = UUID.randomUUID().toString();
        order.setId(UUID.randomUUID().toString());
        order.setFirstName(order.getFirstName());
        order.setLastName(order.getLastName());
        order.setCustomerId(order.getCustomerId());
        order.setOrderDate(order.getOrderDate());
        logger.info("Adding Order ID: {}", orderId);
        orderRepository.save(order);
        if (!skipPamentCall) {
            orderPaymentsService.getPaymentApprovalFromPaymentService(orderId, ordersPaymentsApprovalBaseUrl);
            ordersPubSubService.greet("Hello from Mukesh from inside POST /orders");
        }
        return orderId;
    }

    /**
     * Read single Order
     */
    @GetMapping(value = "/{orderId}")
    public Order findOrder(@PathVariable("orderId") String orderId) {
        logger.info("Getting Order ID: {}", orderId);
        return orderRepository.findById(orderId).get();
    }

    /**
     * Delete an Order
     */
    @DeleteMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(
            @PathVariable("orderId") String orderId) {
        logger.info("Deleting Order ID: {}", orderId);
        orderRepository.deleteById(orderId);
    }

    /**
     * Delete all Orders
     */
    @DeleteMapping(value = "/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllOrders() {
        logger.info("Deleting all order: {}");
        orderRepository.deleteAll();
    }

    /**
     * Read List of Orders
     */
    @GetMapping
    public Iterable<Order> getAllOrders() {
        logger.info("sending a greeting from inside GET /orders");
        ordersPubSubService.greet("Hello from Mukesh from inside GET /orders");
        logger.info("Listing all orders");
        return orderRepository.findAll();
    }


}

package com.example.springboot.gcp.services;

import com.example.springboot.gcp.model.Order;
import com.example.springboot.gcp.model.OrderRepository;
import com.example.springboot.gcp.web.controllers.OrdersPaymentsController;
import com.example.springboot.gcp.web.controllers.OrdersPubSubController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SpannerOrdersDbService {

    private final OrderRepository orderRepository;
    private final OrdersPaymentsController ordersPaymentsController;
    private final OrdersPubSubController ordersPubSubController;


    public SpannerOrdersDbService(OrderRepository orderRepository, OrdersPaymentsController ordersPaymentsController, OrdersPubSubController ordersPubSubController) {
        this.orderRepository = orderRepository;
        this.ordersPaymentsController = ordersPaymentsController;
        this.ordersPubSubController = ordersPubSubController;
    }

    public boolean deleteAllOrders() {
        log.info("Deleting all orders");
        orderRepository.deleteAll();
        return true;
    }

    public Iterable<Order> getAllOrders() {
        log.info("Listing all orders from the DB");
        return orderRepository.findAll();
    }

    public Order findOrder(String orderId) {
        log.info("Getting Order ID: {}", orderId);
        return orderRepository.findById(orderId).get();
    }

    public void deleteOrder(String orderId) {
        log.info("Deleting Order ID: {}", orderId);
        orderRepository.deleteById(orderId);
    }


    public String createOrder(Order order) {
        String orderId = UUID.randomUUID().toString();
        order.setId(UUID.randomUUID().toString());
        order.setFirstName(order.getFirstName());
        order.setLastName(order.getLastName());
        order.setCustomerId(order.getCustomerId());
        order.setOrderDate(order.getOrderDate());
        log.info("Adding Order ID: {}", orderId);
        orderRepository.save(order);
        return orderId;
    }


}

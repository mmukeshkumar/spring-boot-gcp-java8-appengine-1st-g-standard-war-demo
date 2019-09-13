package com.example.springboot.gcp.services;

import com.example.springboot.gcp.model.Order;
import com.example.springboot.gcp.web.controllers.OrdersController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SpannerOrdersStartupRunnerService implements ApplicationRunner {


// This will run on application startup
// for setting up some data on startup


    private final SpannerOrdersDbService spannerOrdersDbService;

    SpannerOrdersStartupRunnerService(SpannerOrdersDbService spannerOrdersDbService) {
        this.spannerOrdersDbService = spannerOrdersDbService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        spannerOrdersDbService.deleteAllOrders();
        Order order1 = new Order();
        order1.setCustomerId(UUID.randomUUID().toString());
        order1.setFirstName("Mukesh");
        order1.setLastName("Kumar");

           /* SimpleDateFormat sdfAmerica = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
            sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
             String dateTime = sdfAmerica.format(new Date());
*/
        order1.setOrderDate(new Date());

        Order order2 = new Order();
        order2.setCustomerId(UUID.randomUUID().toString());
        order2.setFirstName("Zippy");
        order2.setLastName("Kumar");
        order2.setOrderDate(new Date());

        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        String newOrderId = spannerOrdersDbService.createOrder(order1);
        log.info("Order created, order Id:  {}", newOrderId);
        newOrderId = spannerOrdersDbService.createOrder(order2);
        log.info("Order created, order Id:  {}", newOrderId);

    }
}


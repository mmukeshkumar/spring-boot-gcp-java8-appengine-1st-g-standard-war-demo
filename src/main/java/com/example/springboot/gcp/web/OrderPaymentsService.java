package com.example.springboot.gcp.web;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/orders/payments")
@RestController
@Slf4j
public class OrderPaymentsService {

    static private final Logger logger = LoggerFactory.getLogger(OrderPaymentsService.class);

    private  final RestTemplate restTemplate;

    public OrderPaymentsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Trace demo
     */
    @GetMapping(value = "/{orderId}")
    public String getPaymentApproval(@PathVariable String orderId) {
        logger.info("Getting payment approval for orderId: {}", orderId);
        return "APPROVED";

    }

    /**
     * Trace demo
     */
    public String getPaymentApprovalFromPaymentService(String orderId, String ordersPaymentsApprovalBaseUrl) {
        logger.info("Inside getPaymentApprovalFromPaymentService,  getting payment approval for orderId: {}", orderId);
        //I am just calling the current web app itself, but inn the real world this would be a call to another micro service
        String result = restTemplate.getForObject(ordersPaymentsApprovalBaseUrl + "/{orderId}", String.class, orderId);
        logger.info("Inside getPaymentApprovalFromPaymentService,  Finished payment approval for orderId: {}", orderId);
        return result;
    }



}

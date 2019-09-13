package com.example.springboot.gcp.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/orders/payments")
@RestController
@Slf4j
public class OrdersPaymentsController {

    private final RestTemplate restTemplate;
    private final String ordersPaymentsApprovalBaseUrl;

    public OrdersPaymentsController(@Value("${retailapi.ordersPaymentsApprovalBaseUrl}") String ordersPaymentsApprovalBaseUrl, RestTemplate restTemplate) {
        this.ordersPaymentsApprovalBaseUrl = ordersPaymentsApprovalBaseUrl;
        this.restTemplate = restTemplate;
    }


    /**
     * Trace demo
     */
    @GetMapping(value = "/{orderId}")
    public String getPaymentApproval(@PathVariable String orderId) {
        log.info("Getting payment approval for orderId: {}", orderId);
        return "APPROVED";

    }

    /**
     * Trace demo
     */
    public String getPaymentApprovalFromPaymentService(String orderId) {
        log.info("Inside getPaymentApprovalFromPaymentService,  getting payment approval for orderId: {}", orderId);
        //I am just calling the current web app itself, but in the real world this would be a call to another micro service
        String result = restTemplate.getForObject(ordersPaymentsApprovalBaseUrl + "/{orderId}", String.class, orderId);
        log.info("Inside getPaymentApprovalFromPaymentService,  Finished payment approval for orderId: {}", orderId);
        return result;
    }


}

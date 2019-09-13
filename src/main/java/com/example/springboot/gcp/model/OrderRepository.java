package com.example.springboot.gcp.model;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Service;

@Service
public interface OrderRepository extends SpannerRepository<Order, String> {
}


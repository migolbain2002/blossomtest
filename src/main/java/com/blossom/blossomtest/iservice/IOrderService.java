package com.blossom.blossomtest.iservice;

import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.Response;
import org.springframework.http.ResponseEntity;

public interface IOrderService {

    ResponseEntity<Response> create(Order order);

    ResponseEntity<Response> getOrderById(Long id);
}

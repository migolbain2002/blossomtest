package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IOrderService;
import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${controller.properties.base-path}")
@Tag(name = "Order Controller", description = "Order management")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    IOrderService orderService;


    @PostMapping("orders/create")
    @Operation(summary = "Create new order.")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Response> createOrder(@RequestBody Order request) {
        return orderService.create(request);
    }

    @GetMapping("orders/{id}")
    @Operation(summary = "Get order by ID")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Response> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}

package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.iservice.IOrderService;
import com.blossom.blossomtest.model.*;
import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.order.OrderItem;
import com.blossom.blossomtest.model.order.OrderStatus;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.ProductRepository;
import com.blossom.blossomtest.persistence.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public ResponseEntity<Response> create(Order order) {

        if (order.getUser() == null || order.getUser().getId() == null) {
            return ApiResponse.error("Must specify the user.", HttpStatus.BAD_REQUEST);
        }

        var userOpt = userRepository.findById(order.getUser().getId());
        if (userOpt.isEmpty()) {
            return ApiResponse.error("User not found.", HttpStatus.NOT_FOUND);
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return ApiResponse.error("The order has to got at least one product.", HttpStatus.BAD_REQUEST);
        }

        double total = 0.0;

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                return ApiResponse.error("Each item has to got a valid product.", HttpStatus.BAD_REQUEST);
            }

            var productOpt = productRepository.findById(item.getProduct().getId());
            if (productOpt.isEmpty()) {
                return ApiResponse.error("Product not found: " + item.getProduct().getId(), HttpStatus.NOT_FOUND);
            }

            var product = productOpt.get();
            if (product.getStock() < item.getQuantity()) {
                return ApiResponse.error("Not enough stock for: " + product.getName(), HttpStatus.CONFLICT);
            }


            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            item.setOrder(order);
            item.setPriceAtPurchase(product.getPrice());
            total += product.getPrice() * item.getQuantity();
        }

        order.setUser(userOpt.get());
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        var savedOrder = orderRepository.save(order);
        return ApiResponse.success("Order created successfully.", savedOrder);
    }

    @Override
    public ResponseEntity<Response> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> ApiResponse.success("Order found.", order))
                .orElseGet(() -> ApiResponse.error("Order not found.", HttpStatus.NOT_FOUND));
    }

}

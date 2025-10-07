package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.iservice.IPaymentService;
import com.blossom.blossomtest.model.order.OrderStatus;
import com.blossom.blossomtest.model.payment.Payment;
import com.blossom.blossomtest.model.payment.PaymentStatus;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ResponseEntity<Response> create(Payment payment) {

        if (payment.getOrder() == null || payment.getOrder().getId() == null) {
            return ApiResponse.error("Must specify the order to be paid.", HttpStatus.BAD_REQUEST);
        }

        var orderOpt = orderRepository.findById(payment.getOrder().getId());
        if (orderOpt.isEmpty()) {
            return ApiResponse.error("Order not found.", HttpStatus.NOT_FOUND);
        }

        var order = orderOpt.get();

        if (order.getStatus() != OrderStatus.PENDING) {
            return ApiResponse.error("Only pending order can be paid.", HttpStatus.BAD_REQUEST);
        }

        payment.setOrder(order);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        var savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        return ApiResponse.success("Payment registered successfully.", savedPayment);
    }
}

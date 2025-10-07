package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IPaymentService;
import com.blossom.blossomtest.model.payment.Payment;
import com.blossom.blossomtest.model.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${controller.properties.base-path}")
@Tag(name = "Payment Controller", description = "Payment creation")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    IPaymentService paymentService;

    @PostMapping("payments/create")
    @Operation(summary = "Register a payment for a created order.")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Response> createPayment(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }
}

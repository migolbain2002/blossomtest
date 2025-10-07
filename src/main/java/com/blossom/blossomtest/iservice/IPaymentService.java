package com.blossom.blossomtest.iservice;

import com.blossom.blossomtest.model.payment.Payment;
import com.blossom.blossomtest.model.Response;
import org.springframework.http.ResponseEntity;

public interface IPaymentService {

    ResponseEntity<Response> create(Payment payment);
}

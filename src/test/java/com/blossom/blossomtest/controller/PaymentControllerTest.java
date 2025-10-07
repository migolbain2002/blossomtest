package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IPaymentService;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.payment.Payment;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@TestPropertySource(properties = "controller.properties.base-path=/")
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPaymentService paymentService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment_Success() throws Exception {
        Payment mockPayment = new Payment() {};
        Response mockResponse = new Response(200, "Payment created successfully.", mockPayment);

        when(paymentService.create(any(Payment.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(post("/payments/create")
                        .contentType("application/json")
                        .content("{\"order\":{\"id\":1},\"amount\":120.50,\"payment_type\":\"CARD\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Payment created successfully."));
    }
}

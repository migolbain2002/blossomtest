package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.order.OrderStatus;
import com.blossom.blossomtest.model.payment.Payment;
import com.blossom.blossomtest.model.payment.PaymentStatus;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        payment = mock(Payment.class, CALLS_REAL_METHODS);
        payment.setId(1L);
        payment.setOrder(order);
    }

    // 🔹 Test: orden nula
    @Test
    void testCreate_OrderNull() {
        payment.setOrder(null);

        ResponseEntity<Response> response = paymentService.create(payment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe especificar la orden a pagar.", response.getBody().getMessage());
        verify(orderRepository, never()).findById(any());
    }

    // 🔹 Test: orden sin ID
    @Test
    void testCreate_OrderIdNull() {
        payment.getOrder().setId(null);

        ResponseEntity<Response> response = paymentService.create(payment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe especificar la orden a pagar.", response.getBody().getMessage());
        verify(orderRepository, never()).findById(any());
    }

    // 🔹 Test: orden no encontrada
    @Test
    void testCreate_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Response> response = paymentService.create(payment);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orden no encontrada.", response.getBody().getMessage());
        verify(paymentRepository, never()).save(any());
    }

    // 🔹 Test: orden no pendiente
    @Test
    void testCreate_OrderNotPending() {
        order.setStatus(OrderStatus.COMPLETED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        ResponseEntity<Response> response = paymentService.create(payment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Solo se pueden pagar órdenes pendientes.", response.getBody().getMessage());
        verify(paymentRepository, never()).save(any());
    }

    // 🔹 Test: pago exitoso
    @Test
    void testCreate_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Response> response = paymentService.create(payment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment registered successfully.", response.getBody().getMessage());

        Payment savedPayment = (Payment) response.getBody().getData();
        assertEquals(PaymentStatus.COMPLETED, savedPayment.getStatus());
        assertNotNull(savedPayment.getPaidAt());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(any(Order.class));

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }
}

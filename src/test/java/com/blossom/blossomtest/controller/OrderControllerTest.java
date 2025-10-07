package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.iservice.IOrderService;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.order.Order;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@TestPropertySource(properties = "controller.properties.base-path=/")
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOrderService orderService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        Order mockOrder = new Order();
        Response mockResponse = new Response(200, "Order created successfully.", mockOrder);
        when(orderService.create(any(Order.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(post("/orders/create")
                        .contentType("application/json")
                        .content("{\"user\":{\"id\":1},\"items\":[]}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Order created successfully."));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        Long id = 1L;
        Order mockOrder = new Order();
        Response mockResponse = new Response(200, "Order found.", mockOrder);
        when(orderService.getOrderById(id))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Order found."));
    }
}

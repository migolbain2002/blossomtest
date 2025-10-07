package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.service.ProductService;
import com.blossom.blossomtest.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@TestPropertySource(properties = "controller.properties.base-path=/")
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;

    private Product mockProduct;
    private Response successResponse;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(19.99);

        successResponse = Response.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(mockProduct)
                .build();
    }

    @Test
    void testCreateProduct() throws Exception {
        Mockito.when(productService.create(any(Product.class)))
                .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(post("/products/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    void testSearchProducts() throws Exception {
        Mockito.when(productService.search(any(Map.class)))
                .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(get("/products/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Collections.singletonMap("name", "Test Product"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetProductById() throws Exception {
        Mockito.when(productService.getProductById(1L))
                .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Mockito.when(productService.updateProduct(any(Product.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(put("/products/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mockProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        Mockito.when(productService.deleteProduct(1L))
                .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}

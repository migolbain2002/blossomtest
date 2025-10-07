package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.order.OrderItem;
import com.blossom.blossomtest.model.order.OrderStatus;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.model.user.User;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.ProductRepository;
import com.blossom.blossomtest.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;
    private OrderItem item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(5);

        item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        order = new Order();
        order.setUser(user);
        order.setItems(List.of(item));
    }

    @Test
    void testCreateOrder_UserNotSpecified() {
        order.setUser(null);

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Must specify the user.", response.getBody().getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreateOrder_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found.", response.getBody().getMessage());
    }

    @Test
    void testCreateOrder_EmptyItems() {
        order.setItems(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("The order has to got at least one product.", response.getBody().getMessage());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("Product not found"));
    }

    @Test
    void testCreateOrder_NotEnoughStock() {
        product.setStock(1); // menos del requerido
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("Not enough stock"));
    }

    @Test
    void testCreateOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(10L);
            return o;
        });

        ResponseEntity<Response> response = orderService.create(order);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Order created successfully.", response.getBody().getMessage());
        assertEquals(OrderStatus.PENDING, ((Order) response.getBody().getData()).getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetOrderById_Found() {
        order.setId(5L);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        ResponseEntity<Response> response = orderService.getOrderById(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Order found.", response.getBody().getMessage());
        assertEquals(order, response.getBody().getData());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = orderService.getOrderById(99L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Order not found.", response.getBody().getMessage());
    }
}

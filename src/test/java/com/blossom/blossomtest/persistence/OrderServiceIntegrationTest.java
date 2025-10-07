package com.blossom.blossomtest.persistence;

import com.blossom.blossomtest.model.order.Order;
import com.blossom.blossomtest.model.order.OrderItem;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.model.user.User;
import com.blossom.blossomtest.persistence.OrderRepository;
import com.blossom.blossomtest.persistence.ProductRepository;
import com.blossom.blossomtest.persistence.UserRepository;
import com.blossom.blossomtest.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@Rollback
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    private User user;
    private Product product;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("john@example.com");
        user.setPassword("pass");
        userRepository.save(user);

        product = new Product();
        product.setName("Keyboard");
        product.setCategory("Tech");
        product.setPrice(250.0);
        product.setStock(10);
        productRepository.save(product);
    }

    @Test
    void testCreateOrderWithRealDB() {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setUser(user);
        order.setItems(List.of(item));

        var response = orderService.create(order);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("Order created successfully"));

        // Validar persistencia real
        var saved = orderRepository.findAll();
        assertEquals(1, saved.size());
        assertEquals(user.getEmail(), saved.get(0).getUser().getEmail());
        assertEquals(1, saved.get(0).getItems().size());
    }
}

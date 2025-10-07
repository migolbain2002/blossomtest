package com.blossom.blossomtest.persistence;

import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.persistence.ProductRepository;
import com.blossom.blossomtest.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // usa H2 en memoria
@Transactional
@Rollback
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testCreateAndFindProduct() {
        // Crear producto
        Product p = new Product();
        p.setName("Mouse Gamer");
        p.setCategory("Tech");
        p.setPrice(120.0);
        p.setStock(10);

        var response = productService.create(p);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getData());

        Product saved = (Product) response.getBody().getData();

        // Verificar que se guardó realmente en H2
        Product found = productRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Mouse Gamer", found.getName());
    }
}

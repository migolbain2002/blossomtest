package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ItemFilterHelper;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setCategory("Electronics");
        product.setPrice(1500.0);
        product.setStock(10);
    }

    // 🔹 Test: crear producto exitosamente
    @Test
    void testCreateProduct_Success() {
        when(productRepository.existsByNameAndCategory(any(), any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ResponseEntity<Response> response = productService.create(product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product created successfully.", response.getBody().getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // 🔹 Test: producto duplicado
    @Test
    void testCreateProduct_Duplicate() {
        when(productRepository.existsByNameAndCategory(any(), any())).thenReturn(true);

        ResponseEntity<Response> response = productService.create(product);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Product already exists.", response.getBody().getMessage());
        verify(productRepository, never()).save(any());
    }

    // 🔹 Test: precio inválido
    @Test
    void testCreateProduct_InvalidPrice() {
        product.setPrice(0.0);

        ResponseEntity<Response> response = productService.create(product);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Price must be greater than 0.", response.getBody().getMessage());
    }

    // 🔹 Test: obtener producto por ID (éxito)
    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Response> response = productService.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product found.", response.getBody().getMessage());
        assertEquals(product, response.getBody().getData());
    }

    // 🔹 Test: obtener producto por ID (no existe)
    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = productService.getProductById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found.", response.getBody().getMessage());
    }

    // 🔹 Test: actualizar producto exitosamente
    @Test
    void testUpdateProduct_Success() {
        Product updated = new Product();
        updated.setName("Laptop Gamer");
        updated.setPrice(2000.0);
        updated.setStock(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        ResponseEntity<Response> response = productService.updateProduct(updated, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully.", response.getBody().getMessage());
        verify(productRepository).save(any(Product.class));
    }

    // 🔹 Test: actualizar producto no existente
    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = productService.updateProduct(product, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found.", response.getBody().getMessage());
    }

    // 🔹 Test: eliminar producto exitosamente
    @Test
    void testDeleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<Response> response = productService.deleteProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully.", response.getBody().getMessage());
        verify(productRepository, times(1)).delete(product);
    }

    // 🔹 Test: eliminar producto no encontrado
    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Response> response = productService.deleteProduct(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found.", response.getBody().getMessage());
        verify(productRepository, never()).delete(any());
    }

    // 🔹 Test: búsqueda con filtros (usa ItemFilterHelper mockeado)
    @Test
    void testSearchProducts() {
        Product p1 = new Product();
        p1.setName("Laptop");

        when(productRepository.findAll()).thenReturn(List.of(p1));
        Map<String, String> filters = Map.of("name", "Laptop");

        // Simular comportamiento del helper
        assertTrue(ItemFilterHelper.matches(p1, filters));

        ResponseEntity<Response> response = productService.search(filters);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Search successful.", response.getBody().getMessage());
    }
}

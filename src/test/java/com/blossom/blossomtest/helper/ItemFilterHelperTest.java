package com.blossom.blossomtest.helper;

import com.blossom.blossomtest.exception.InvalidFilterException;
import com.blossom.blossomtest.model.product.Product;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemFilterHelperTest {

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setCategory("Electronics");
        product.setDescription("High performance laptop");
        product.setPrice(1200.50);
        product.setStock(5);
        return product;
    }

    @Test
    void testMatches_withBasicField_shouldReturnTrue() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("name", "laptop");
        filters.put("category", "Electronics");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertTrue(result);
    }

    @Test
    void testMatches_withNonMatchingField_shouldReturnFalse() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("name", "Phone");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertFalse(result);
    }

    @Test
    void testMatches_withExactPrice_shouldReturnTrue() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("price", "1200.50");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertTrue(result);
    }

    @Test
    void testMatches_withMinPrice_shouldReturnTrue() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("minPrice", "1000.00");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertTrue(result);
    }

    @Test
    void testMatches_withMaxPrice_shouldReturnFalse() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("maxPrice", "1000.00");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertFalse(result);
    }

    @Test
    void testMatches_withInvalidField_shouldThrowException() {
        Product product = createSampleProduct();
        Map<String, String> filters = new HashMap<>();
        filters.put("invalidField", "value");

        assertThrows(InvalidFilterException.class, () -> {
            ItemFilterHelper.matches(product, filters);
        });
    }

    @Test
    void testMatches_withNullField_shouldReturnFalse() {
        Product product = createSampleProduct();
        product.setCategory(null);
        Map<String, String> filters = new HashMap<>();
        filters.put("category", "Electronics");

        boolean result = ItemFilterHelper.matches(product, filters);
        assertFalse(result);
    }
}

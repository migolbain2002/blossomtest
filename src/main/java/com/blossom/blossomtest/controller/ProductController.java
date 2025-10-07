package com.blossom.blossomtest.controller;

import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${controller.properties.base-path}")
@Tag(name = "Product Controller", description = "Product management")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("products/create")
    @Operation(summary = "Create product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping("products/search")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(summary = "Search products by filter")
    public ResponseEntity<Response> search(@RequestBody Map<String, String> filters){
        return productService.search(filters);
    }

    @GetMapping("products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Response> getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @PutMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product")
    public ResponseEntity<Response> updateProduct(@RequestBody Product product, @PathVariable Long id){
        return productService.updateProduct(product, id);
    }

    @DeleteMapping("products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product by ID")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }
}

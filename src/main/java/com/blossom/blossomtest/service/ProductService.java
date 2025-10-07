package com.blossom.blossomtest.service;

import com.blossom.blossomtest.helper.ApiResponse;
import com.blossom.blossomtest.helper.ItemFilterHelper;
import com.blossom.blossomtest.iservice.IProductService;
import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.model.Response;
import com.blossom.blossomtest.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ResponseEntity<Response> create(Product product) {
        if (productRepository.existsByNameAndCategory(product.getName(), product.getCategory())) {
            return ApiResponse.error("Product already exists.", HttpStatus.CONFLICT);
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            return ApiResponse.error("Price must be greater than 0.", HttpStatus.BAD_REQUEST);
        }

        product.setId(null);
        Product saved = productRepository.save(product);

        return ApiResponse.success("Product created successfully.", saved);
    }

    @Override
    public ResponseEntity<Response> search(Map<String, String> filters) {
        List<Product> itemList = productRepository.findAll().stream()
                .filter(item -> ItemFilterHelper.matches(item, filters))
                .collect(Collectors.toList());

        return ApiResponse.success("Search successful.", itemList);
    }

    @Override
    public ResponseEntity<Response> getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> ApiResponse.success("Product found.", product))
                .orElseGet(() -> ApiResponse.error("Product not found.", HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Response> updateProduct(Product product, Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    if (product.getName() != null) existing.setName(product.getName());
                    if (product.getCategory() != null) existing.setCategory(product.getCategory());
                    if (product.getPrice() != null && product.getPrice() > 0) existing.setPrice(product.getPrice());
                    if (product.getStock() != null && product.getStock() >= 0) existing.setStock(product.getStock());

                    Product updated = productRepository.save(existing);
                    return ApiResponse.success("Product updated successfully.", updated);
                })
                .orElseGet(() -> ApiResponse.error("Product not found.", HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Response> deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return ApiResponse.success("Product deleted successfully.", null);
                })
                .orElseGet(() -> ApiResponse.error("Product not found.", HttpStatus.NOT_FOUND));
    }
}

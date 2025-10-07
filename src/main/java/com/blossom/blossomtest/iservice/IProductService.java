package com.blossom.blossomtest.iservice;

import com.blossom.blossomtest.model.product.Product;
import com.blossom.blossomtest.model.Response;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface IProductService {

    ResponseEntity<Response> create(Product product);

    ResponseEntity<Response> search(Map<String, String> filters);

    ResponseEntity<Response> getProductById(Long id);

    ResponseEntity<Response> updateProduct(Product product, Long id);

    ResponseEntity<Response> deleteProduct(Long id);

}

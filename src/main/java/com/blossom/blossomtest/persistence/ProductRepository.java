package com.blossom.blossomtest.persistence;

import com.blossom.blossomtest.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameAndCategory(String name, String category);
}

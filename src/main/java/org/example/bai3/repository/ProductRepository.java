package org.example.bai3.repository;

import org.example.bai3.entity.Product;

public interface ProductRepository {
    Product findById(Long id);
    void save(Product product);
}

package org.example.bai3.repository;

import org.example.bai3.entity.ShoppingCart;

import java.util.Optional;

public interface CartRepository {
    Optional<ShoppingCart> findByUserId(Long userId);
    ShoppingCart save(ShoppingCart cart);
}

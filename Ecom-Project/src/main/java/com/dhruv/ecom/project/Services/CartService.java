package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
      List<CartDTO> getAllCarts();


     CartDTO addProductToCart(Long productId, Integer quantity);

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}

package com.dhruv.ecom.project.controller;


import com.dhruv.ecom.project.Model.Cart;
import com.dhruv.ecom.project.Services.CartService;
import com.dhruv.ecom.project.Util.AuthUtil;
import com.dhruv.ecom.project.payload.CartDTO;
import com.dhruv.ecom.project.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    private CartService cartService;


    @PostMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO , HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOs = cartService.getAllCarts();

        return new ResponseEntity<List<CartDTO>>(cartDTOs , HttpStatus.FOUND);

    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){


        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId , cartId);
        return new ResponseEntity<>(cartDTO , HttpStatus.OK);

    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation){
        CartDTO cartDTO=  cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1 );

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);

    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId){

        String status = cartService.deleteProductFromCart(cartId , productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);


    }


}

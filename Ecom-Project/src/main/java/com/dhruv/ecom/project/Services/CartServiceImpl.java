package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.Model.Cart;
import com.dhruv.ecom.project.Model.CartItem;
import com.dhruv.ecom.project.Model.Product;
import com.dhruv.ecom.project.Util.AuthUtil;
import com.dhruv.ecom.project.exceptions.APIException;
import com.dhruv.ecom.project.exceptions.ResourceNotFoundException;
import com.dhruv.ecom.project.payload.CartDTO;
import com.dhruv.ecom.project.payload.ProductDTO;
import com.dhruv.ecom.project.repositories.CartItemRepository;
import com.dhruv.ecom.project.repositories.CartRepository;
import com.dhruv.ecom.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{



    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;



    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //Find existing cart or create one
        Cart cart = createCart();

        //Retrieve product details.
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId)); // Checking if product is present or not

        //Perform Validations

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId); // Checking weather a product with X productId is present in X cartId

        if(cartItem!=null){
            throw new APIException("Product" + product.getProductName() + "already exists in the cart");
        }
        if(product.getQuantity() == 0){
            throw new APIException( product.getProductName() +"Product is  not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make order of the " + product.getProductName() + "less then or equal to the qty" + product.getQuantity() );
        }


        CartItem newCartItem =  new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity()- quantity);

        cart.setTotalPrice(cart.getTotalPrice() +  (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }



    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No cart present");
        }

        List<CartDTO> cartDTOs =carts.stream()
                .map(cart ->{
                    CartDTO cartDTO = modelMapper.map(cart , CartDTO.class);
                    List<ProductDTO> prodcts = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                            .collect(Collectors.toList());
                    cartDTO.setProducts(prodcts);
                    return cartDTO;
                }).collect(Collectors.toList());



        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId , cartId);
        if (cart == null){
            throw new ResourceNotFoundException("Cart","CartId", cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class);
        cart.getCartItems().forEach(c->
                c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p-> modelMapper.map(p.getProduct(), ProductDTO.class))
                .toList();

        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        //getting authenticated user cart emailId and cartId
        String emailId=  authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        //checking for the cart weather it exists or not
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart", "cartId", cartId));
        //getting the product
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId));

        //checking for stock
        if(product.getQuantity() == 0){
            throw new APIException( product.getProductName() +"Product is  not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make order of the " + product.getProductName() + "less then or equal to the qty" + product.getQuantity() );
        }

        //Getting cart item from the DB
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        //If cart is null we are performing this validation
        if(cartItem==null){
            throw new APIException("Product " + product.getProductName()  + " not avb in the cart");
        }

        //Calculate new Quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        //Validation to prevent negative quantities
        if(newQuantity <0 ){
            throw new APIException("The resulting quantity can not be negative.");
        }

        if(newQuantity ==0){
            deleteProductFromCart(cartId , productId);
        }else {

            //If cart is not null we start performing the cart item update
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantity);
            cartRepository.save(cart);
        }
        CartItem updatedItem = cartItemRepository.save(cartItem);
        if (updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        //creating a response to sending the update to the user
        CartDTO cartDTO = modelMapper.map(cart , CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        //Creating a list of productDTO using stream
        Stream<ProductDTO> productStream = cartItems.stream().map(item ->{
            ProductDTO prd  = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("Cart", "CartId" ,  cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if(cartItem==null){
            throw new ResourceNotFoundException("Product " , "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()-
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId , productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        //getting the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not found in the cart.");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() + cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);

    }

    //Helper method to create a cart.Can be used in different methods to avoid multiple implementations.
    private Cart createCart(){
        // Current userCart fetching from Cart repository
        Cart userCart =  cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart; //If the Current user cart is !null then return it.
        }
        //If the cart is empty we'll set the total price of the cart as 0.00
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());

        Cart newCart = cartRepository.save(cart); //Saving the cart into the repository/DB
        return newCart;

    }
}

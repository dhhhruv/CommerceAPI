package com.dhruv.ecom.project.controller;



import com.dhruv.ecom.project.Configurations.AppConstants;
import com.dhruv.ecom.project.Services.ImageService;
import com.dhruv.ecom.project.Services.ProductService;
import com.dhruv.ecom.project.payload.ProductDTO;
import com.dhruv.ecom.project.payload.ProductResponse;
import jakarta.validation.Valid;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ImageService imageService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){
        ProductDTO savedproductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(
            @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortBy,
            @RequestParam(name ="sortOrder" , defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder)
{


        ProductResponse productResponse=productService.getAllProducts(pageNumber , pageSize , sortBy ,sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsbyCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortBy,
                                                                 @RequestParam(name ="sortOrder" , defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber , pageSize , sortBy ,sortOrder);
        return new ResponseEntity<>(productResponse , HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword,
                                                               @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                               @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCT_BY, required = false) String sortBy,
                                                               @RequestParam(name ="sortOrder" , defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){

        ProductResponse productResponse= productService.searchProductByKeyword(keyword,pageNumber , pageSize , sortBy ,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);

    }


    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                        @PathVariable Long productId){

       ProductDTO updatedProductDTO= productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.CREATED);

    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

        ProductDTO deletedProductDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);

    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable Long productId,
                                                  @RequestParam("image")MultipartFile image) throws IOException {

       ProductDTO updateProduct= imageService.updateProductImage(productId,image);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);


    }

}

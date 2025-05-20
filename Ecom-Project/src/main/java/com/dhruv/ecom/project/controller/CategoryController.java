package com.dhruv.ecom.project.controller;


import com.dhruv.ecom.project.Configurations.AppConstants;
import com.dhruv.ecom.project.Model.Category;
import com.dhruv.ecom.project.Services.CategoryService;
import com.dhruv.ecom.project.payload.CategoryDTO;
import com.dhruv.ecom.project.payload.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;



    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY, required = false) String sortBy,
            @RequestParam(name ="sortOrder" , defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder)
    {
        CategoryResponse entry = categoryService.getAllCategories(pageNumber , pageSize , sortBy ,sortOrder);
        //if(entry!=null && !entry.isEmpty()){
            return new ResponseEntity<>(entry , HttpStatus.OK);
        //}
        //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }





    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO , HttpStatus.CREATED);

    }
    @DeleteMapping("/admin/categories/{categoryId}")
    // Uses ResponseEntity to wrap both the response body (status message) and the HTTP status code.
    public ResponseEntity<CategoryDTO>  deleteCategory(@PathVariable Long categoryId){

        // The try-catch block handles exceptions thrown by the service;
        // if a ResponseStatusException occurs,
        //try {
            CategoryDTO status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status , HttpStatus.OK);

       // }
       /* catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode()); // it captures the custom error message and status code to return a proper error response.
        }*/

    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory (@RequestBody CategoryDTO categoryDTO , @PathVariable Long categoryId){

            CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO , categoryId);
            return new ResponseEntity<>(savedCategoryDTO , HttpStatus.OK );



    }
}

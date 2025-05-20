package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.Model.Category;
import com.dhruv.ecom.project.payload.CategoryDTO;
import com.dhruv.ecom.project.payload.CategoryResponse;

import java.util.List;


public interface CategoryService {



    CategoryResponse getAllCategories(Integer pageNumber , Integer pageSize , String sortBy , String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO , Long categoryId);
}

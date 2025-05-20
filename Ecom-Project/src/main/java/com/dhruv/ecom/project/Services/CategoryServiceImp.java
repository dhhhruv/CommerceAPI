package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.Model.Category;
import com.dhruv.ecom.project.exceptions.APIException;
import com.dhruv.ecom.project.exceptions.ResourceNotFoundException;
import com.dhruv.ecom.project.payload.CategoryDTO;
import com.dhruv.ecom.project.payload.CategoryResponse;
import com.dhruv.ecom.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImp implements CategoryService {

    //private List<Category> categories  = new ArrayList<>();
    //private Long nextId = 1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber , Integer pageSize, String sortBy ,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize , sortByAndOrder );
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> avbCategory = categoryPage.getContent();

        /*List<Category> avbCategory = categoryRepository.findAll();*/
        // As we are using paging so we will not use this
        // instead we will take categoryPage and get the page content from there.
        if (avbCategory.isEmpty()) {
            throw new APIException("Please add a category");
        }
            List<CategoryDTO> categoryDTOS = avbCategory.stream()
                    .map(category -> modelMapper.map(category, CategoryDTO.class))
                    .toList();

            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalpages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }




    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category CategoryfromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (CategoryfromDb!=null){
            throw new APIException("Category with name " + category.getCategoryName() +"is already present");
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);



        //category.setCategoryId(nextId++);
//        categoryRepository.save(category);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category Deletecategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("category", "categoryId" , categoryId));

         categoryRepository.delete(Deletecategory);
         return modelMapper.map(Deletecategory, CategoryDTO.class);
        //return "Category Deleted successfully";

        // Previous implementation when there was no db
//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entered ID not present"));


      /* So here what we did is we want the category object that user wants us to delete,
        //  but user is only passing the category ID.So with the help of ID we make use of
        //  the concept of Java streams.And we filter the list.And we try to match by comparing
         every object's id with the ID that we have got as the input.*/

//        if (category == null) {
//            return "  Category not found";
//        } else {
//            categoryRepository.delete(category);
//            return "Category Deleted successfully";
//        }
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        //Previous implementation when there was no db
       /* {
            List<Category> categories = categoryRepository.findAll();
            Optional<Category> optionalCategory = categories.stream()
                    .filter(c -> c.getCategoryId().equals(categoryId))
                    .findFirst();

            if (optionalCategory.isPresent()) {
                Category existingCategory = optionalCategory.get();
                existingCategory.setCategoryName(category.getCategoryName());
                Category savedCategory = categoryRepository.save(existingCategory);
                return savedCategory;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category id not found");
            }

        }*/

        // Implementation with db to update the details using the input categoryId

        Category saveEntry = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "categoryId" , categoryId));
        Category category=modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        saveEntry= categoryRepository.save(category);
        return modelMapper.map(saveEntry, CategoryDTO.class);
    }


}

package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.Model.Product;
import com.dhruv.ecom.project.exceptions.ResourceNotFoundException;
import com.dhruv.ecom.project.payload.ProductDTO;
import com.dhruv.ecom.project.repositories.CategoryRepository;
import com.dhruv.ecom.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements  ImageService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${project.image}")
    private String path;

    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId));

        //String path = "images/";
        String filename = uploadImage(path,image);
        productFromDB.setImage(filename);

        Product updatedProduct = productRepository.save(productFromDB);

        return modelMapper.map(updatedProduct,ProductDTO.class);

    }

    private String uploadImage(String path, MultipartFile file) throws IOException {

        //File name of current file
        String originalfilename = file.getOriginalFilename();

        //Generating a unique file name using UUID
        String randomId = UUID.randomUUID().toString();

        // Here we are taking out originalfilename & generating a randomId & concating it in filename with substring till last '.' appears
        // Eg: originalfilename is dhruv.jpg & out unique UUID is suppose 1234 so our file name after concat & substring will be 1234.jpg
        String filename = randomId.concat(originalfilename.substring(originalfilename.lastIndexOf('.')));


        String filePath = path + File.separator + filename;

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return filename;


    }
}

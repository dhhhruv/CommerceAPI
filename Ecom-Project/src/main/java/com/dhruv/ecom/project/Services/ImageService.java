package com.dhruv.ecom.project.Services;

import com.dhruv.ecom.project.payload.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface ImageService {

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}

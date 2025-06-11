package com.dhruv.ecom.project.Services;


import com.dhruv.ecom.project.Model.Cart;
import com.dhruv.ecom.project.Model.Category;
import com.dhruv.ecom.project.Model.Product;
import com.dhruv.ecom.project.exceptions.APIException;
import com.dhruv.ecom.project.exceptions.ResourceNotFoundException;
import com.dhruv.ecom.project.payload.CartDTO;
import com.dhruv.ecom.project.payload.ProductDTO;
import com.dhruv.ecom.project.payload.ProductResponse;
import com.dhruv.ecom.project.repositories.CartRepository;
import com.dhruv.ecom.project.repositories.CategoryRepository;
import com.dhruv.ecom.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;


    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));


        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();

        for (Product value : products) {

            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize , sortByAndOrder );
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> avbProduct = productPage.getContent();
        if(avbProduct.isEmpty()){
            throw new APIException("No product present..!!");
        }

    List<Product> products= productPage.getContent();
    List<ProductDTO> productDTOS = products.stream()
            .map(product -> modelMapper.map(product,ProductDTO.class))
            .toList();



    ProductResponse productResponse=new ProductResponse();
    productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalpages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

    return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {


        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize , sortByAndOrder );
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = productPage.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);

        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalpages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;


    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize , sortByAndOrder );
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);

        List<Product> products=productPage.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();

        if(products.size()==0){
            throw new APIException("Product not found with keyword: " + keyword);
        }

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);

        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalpages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId));

        Product product=modelMapper.map(productDTO,Product.class);
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setSpecialPrice(product.getSpecialPrice());
        productFromDB.setDiscount(product.getDiscount());

        Product  savedProduct= productRepository.save(productFromDB);

        //Getting carts from cart repository
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        //converted the cart in streams and mapped into cartDTO
        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO  cartDTO = modelMapper.map(cart , CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream()//Extracted the Cart-items from cart to get the products
                    .map(p->modelMapper.map(p.getProduct() , ProductDTO.class)) // mapped them into the productDTO
                    .toList();
            cartDTO.setProducts(products); //Set the products in cartDTO
            return cartDTO;
        }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct,ProductDTO.class);


    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(productFromDB);
        return modelMapper.map(productFromDB,ProductDTO.class);


    }

    /*@Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product" , "productId", productId));

        String path = "images/";
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


    }*/
}

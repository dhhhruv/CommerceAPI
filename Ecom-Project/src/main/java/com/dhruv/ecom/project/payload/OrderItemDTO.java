package com.dhruv.ecom.project.payload;

import com.dhruv.ecom.project.Model.Order;
import com.dhruv.ecom.project.Model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {

    private Long orderItemId;
    private ProductDTO productDTO;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;

}

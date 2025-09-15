package com.dhruv.ecom.project.payload;

import com.dhruv.ecom.project.Model.Address;
import com.dhruv.ecom.project.Model.OrderItem;
import com.dhruv.ecom.project.Model.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;
    private PaymentDTO payment;
    private AddressDTO address;
    private Long addressId;



}

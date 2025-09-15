package com.dhruv.ecom.project.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payments" , cascade = {CascadeType.PERSIST , CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min = 4 , message = "Payment method atleast contain 4 characters")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;


    public Payment(Long paymentId , String pgPaymentId , String pgName , String pgStatus , String pgResponseMessage){

        this.paymentId=paymentId;
        this.pgPaymentId=pgPaymentId;
        this.pgName=pgName;
        this.pgStatus=pgStatus;
        this.pgResponseMessage=pgResponseMessage;


    }



}

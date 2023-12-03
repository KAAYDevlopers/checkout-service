package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    @Id
    private String cartItemId;
    private Long cartItemQuantity;
    private ProductDTO productDetails;
}

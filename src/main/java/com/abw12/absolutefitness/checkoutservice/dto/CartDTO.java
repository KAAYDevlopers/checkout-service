package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    @Id
    private String cartId;
    private BigDecimal cartTotal;
    private List<CartItemDTO> items;
}

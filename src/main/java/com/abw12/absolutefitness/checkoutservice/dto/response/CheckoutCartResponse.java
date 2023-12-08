package com.abw12.absolutefitness.checkoutservice.dto.response;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutCartResponse {

    @Id
    private String cartId;
    private BigDecimal cartTotal;
    private List<CheckoutCartItem> items;
    private String cartCreatedAt;
    private String cartModifiedAt;
}

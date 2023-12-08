package com.abw12.absolutefitness.checkoutservice.dto.response;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutCartItem {

    @Id
    private String cartItemId;
    private Long cartItemQuantity;
    private VariantDTO variantDetails;
}

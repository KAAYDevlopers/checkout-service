package com.abw12.absolutefitness.checkoutservice.dto.response;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    @Id
    private String cartItemId;
    private VariantInventoryData cartItemInventoryData;
    private Long cartQuantity;
    private VariantDTO variantDetails;
}

package com.abw12.absolutefitness.checkoutservice.dto.request;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartAPIItem {

    @Id
    private String cartItemId;
    private String variantId;
    private Long cartItemQuantity;
    private String cartItemCreatedAt;
    private String cartItemModifiedAt;
}


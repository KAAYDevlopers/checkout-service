package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantInventoryData {

    private String stockStatus;
    private Long inventoryQuantity;
}

package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantDTO {

    @Id
    private String variantId;
    private String variantValue;
    private String variantType;
    private Long stockQuantity;
    private BigDecimal buyPrice;
    private BigDecimal onSalePrice;

}

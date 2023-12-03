package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @Id
    private String productId;
    private String variantName;
    private List<VariantDTO> variantList;
}

package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponVariantDTO {
    private UUID id;
    private String variantId;

}

package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {

    private String offerId;
    private String discountType;
    private Integer discountValue;
}

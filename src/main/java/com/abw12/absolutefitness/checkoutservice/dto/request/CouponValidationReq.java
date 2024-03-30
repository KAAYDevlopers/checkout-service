package com.abw12.absolutefitness.checkoutservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationReq {

    private String couponCode;  //end-user specific field
    private Set<String> variantIds;
}

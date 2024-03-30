package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationRes {

    private GetCouponResponse couponData;
    private Set<CouponVariantDTO> applicableToRequestedVariantIds;
    private String msg;
    private String statusCode;
}

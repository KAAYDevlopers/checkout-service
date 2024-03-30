package com.abw12.absolutefitness.checkoutservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
public class ApplyCouponResponse {

    private BigDecimal reCalculatedCartTotal;
    private Set<String> applicableToRequestedVariantIds;
    private String msg;
    private String statusCode;
}

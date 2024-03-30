package com.abw12.absolutefitness.checkoutservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCouponReq {

    private String couponCode;
    private String cartTotal;
    private Map<String,Integer> variantInfo;  //key: variantId , value: variant count in cart
}

package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {

    private String couponId;
    private String description;
    private String discountType;
    private Integer discountValue;
    private Double minOrderValue;
    private String couponCode;
}

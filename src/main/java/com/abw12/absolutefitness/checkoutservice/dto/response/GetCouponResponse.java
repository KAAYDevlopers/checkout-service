package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCouponResponse {

    private String couponId;
    private String couponCode;
    private String description;
    private String discountType;
    private Integer discountValue;
    private Double minOrderValue;
}

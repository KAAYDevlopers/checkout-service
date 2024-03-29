package com.abw12.absolutefitness.checkoutservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    private CartResponse cartData;
    private UserInfoDTO userInfo;
    private List<CouponResponse> couponsData;
}

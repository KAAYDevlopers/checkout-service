package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import com.abw12.absolutefitness.checkoutservice.dto.response.GetCouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "offer-mgmt-coupons-controller")
public interface OfferMgmtClient {

    @GetMapping("/listCoupons/{userId}")
    ResponseEntity<List<GetCouponResponse>> fetchCouponsList(@PathVariable String userId);

    @GetMapping("/validateCoupon")
    ResponseEntity<Map<String,Object>> validateCoupon(@RequestParam("couponCode") String couponCode,
                                                      @RequestParam("variantId") List<String> variantIds);


}

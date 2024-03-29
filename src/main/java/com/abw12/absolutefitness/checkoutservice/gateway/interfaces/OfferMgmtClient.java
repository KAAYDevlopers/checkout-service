package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import com.abw12.absolutefitness.checkoutservice.dto.response.CouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "offer-mgmt-coupons-controller")
public interface OfferMgmtClient {

    @GetMapping("/listCoupons/{userId}")
    ResponseEntity<List<CouponResponse>> fetchCouponsList(@PathVariable String userId);


}

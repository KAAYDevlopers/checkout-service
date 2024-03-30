package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "shopping-cart-ms")
public interface ShoppingCartClient {

    @GetMapping("/getCartDataByUserId/{userId}")
    ResponseEntity<Map<String,Object>> getShoppingCartDetails(@PathVariable("userId") String userId);
}

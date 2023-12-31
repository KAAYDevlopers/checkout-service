package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-cart-ms")
public interface ShoppingCartClient {

    @GetMapping("/getCartDataByUserId/{userId}")
    String getShoppingCartDetails(@PathVariable("userId") String userId);
}

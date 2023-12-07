package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import com.abw12.absolutefitness.checkoutservice.dto.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-cart-ms")
public interface ShoppingCartClient {

    @GetMapping("/shoppingcart/{cartId}")
    CartDTO getShoppingCartDetails(@PathVariable("cartId") String cartId);
}

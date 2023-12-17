package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient("product-inventory-controller")
public interface ProductCatalogInventoryClient {

    @GetMapping("/checkStockStatus/{variantId}")
    ResponseEntity<Map<String,Object>> cartValidation(@PathVariable String variantId);
}

package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "product-catalog-ms")
public interface ProductCatalogClient {

    @GetMapping("/getVariantData/{variantId}")
    ResponseEntity<Map<String, Object>> getVariantDetails(@PathVariable String variantId);
}

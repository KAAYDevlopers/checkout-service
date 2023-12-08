package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-catalog-ms")
public interface ProductCatalogClient {

    @GetMapping("/getVariantData/{variantId}")
    String getVariantDetails(@PathVariable String variantId);
}

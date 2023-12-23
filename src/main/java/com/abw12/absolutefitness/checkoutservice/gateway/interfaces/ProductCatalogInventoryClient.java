package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import com.abw12.absolutefitness.checkoutservice.dto.request.InventoryValidationReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("product-inventory-controller")
public interface ProductCatalogInventoryClient {

    @GetMapping("/checkStockStatus")
    ResponseEntity<Map<String,Object>> cartValidation(@RequestBody InventoryValidationReq request);
}

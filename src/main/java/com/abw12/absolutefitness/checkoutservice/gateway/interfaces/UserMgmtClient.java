package com.abw12.absolutefitness.checkoutservice.gateway.interfaces;

import com.abw12.absolutefitness.checkoutservice.dto.response.UserDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usermgmt-ms")
public interface UserMgmtClient {

    @GetMapping("/usermgmt/{userId}")
    UserDataDTO getUserId(@PathVariable("userId") String userId);
}

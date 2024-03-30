package com.abw12.absolutefitness.checkoutservice.controller;

import com.abw12.absolutefitness.checkoutservice.dto.request.ApplyCouponReq;
import com.abw12.absolutefitness.checkoutservice.service.CheckoutService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout-ms")
public class CheckoutServiceController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("/getCheckoutPageInfo/{userId}")
    private ResponseEntity<?> checkoutShoppingCart(@PathVariable String userId){
        logger.info("Inside checkout page :: getting checkout page details by userId : {}",userId);
        try{
            if(StringUtils.isEmpty(userId)) throw new RuntimeException("userId cannot be null/empty...");
            return new ResponseEntity<>(checkoutService.getCheckoutPageData(userId), HttpStatus.OK);
        }catch (Exception e){
            logger.error("Exception while fetching checkout page data  by userId : {} => {}",userId,e.getMessage());
            return new ResponseEntity<>("Exception while fetching checkout data by userId ",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/applyCoupon")
    private ResponseEntity<?> applyCouponCode(@RequestBody ApplyCouponReq req){
        logger.info("Inside applyCouponCode rest API call : request={} ",req);
        if(StringUtils.isEmpty(req.getCouponCode()) || (req.getVariantInfo() == null || req.getVariantInfo().isEmpty())){
            logger.error("Invalid data request for applyCouponCode couponCode or variantId list is empty");
            return new ResponseEntity<>("Invalid data request for applyCouponCode",HttpStatus.BAD_REQUEST);
        }
        try{
            return new ResponseEntity<>(checkoutService.applyCoupon(req),HttpStatus.OK);
        }catch (Exception e){
            logger.error("Exception while validating coupon :: ERROR Msg={} => {}", e.getMessage(),e.getStackTrace());
            throw e;
        }
    }
}

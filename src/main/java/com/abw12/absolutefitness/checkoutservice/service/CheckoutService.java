package com.abw12.absolutefitness.checkoutservice.service;

import com.abw12.absolutefitness.checkoutservice.constants.Constants;
import com.abw12.absolutefitness.checkoutservice.constants.ErrorCode;
import com.abw12.absolutefitness.checkoutservice.dto.request.ApplyCouponReq;
import com.abw12.absolutefitness.checkoutservice.dto.request.ShoppingCartAPIRequest;
import com.abw12.absolutefitness.checkoutservice.dto.response.*;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CheckoutService {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);
    @Autowired
    private ShoppingCartClient shoppingCartClient;
    @Autowired
    private ProductCatalogClient productCatalogClient;
    @Autowired
    private ProductCatalogInventoryClient productCatalogInventoryClient;
    @Autowired
    private UserMgmtClient userMgmtClient;

    @Autowired
    private OfferMgmtClient offerMgmtClient;
    @Autowired
    private ObjectMapper objectMapper;

    public ResponseData getCheckoutPageData(String userId){
        logger.info("Retrieving the checkout page info for user with userId :: {}",userId);
        ResponseData responseData = new ResponseData();
        responseData.setCartData(getShoppingCartData(userId));
        responseData.setUserInfo(getUserInfo(userId));
        responseData.setCouponsData(fetchCoupons(userId));
        logger.info("Successfully retrieved checkout page data for user with userId :: {} => {}",userId,responseData);
        return responseData;
    }

    private List<GetCouponResponse> fetchCoupons(String userId) {
        logger.info("Fetching the coupons applicable for user with userId={}",userId);
        List<GetCouponResponse> response;
        ResponseEntity<List<GetCouponResponse>> offerMgmtResponse = offerMgmtClient.fetchCouponsList(userId);
        if(offerMgmtResponse.getStatusCode().is2xxSuccessful() && offerMgmtResponse.hasBody()){
            response = offerMgmtResponse.getBody();
            logger.info("Response from the fetchCouponsList API call for userId={} => {}",userId,response);
        }else{
            logger.error("Failed to fetch the user info from userMgmt rest call using userId={} => {}", userId,offerMgmtResponse.getStatusCode());
            throw new RuntimeException("Failed to fetch the user info from userMgmt rest call using userId :: " + userId);
        }
        return response;
    }

    private CartResponse getShoppingCartData(String userId) {
        logger.info("Fetching the cart data by userId :: {}",userId);
        CartResponse response = new CartResponse();

        //calling the shopping cart ms rest api to get cart details for user
        ShoppingCartAPIRequest cartRestApiResponse;
        ResponseEntity<Map<String, Object>> shoppingCartResponse = shoppingCartClient.getShoppingCartDetails(userId);
        if(shoppingCartResponse.getStatusCode().is2xxSuccessful() && shoppingCartResponse.hasBody()){
            cartRestApiResponse=objectMapper.convertValue(shoppingCartResponse.getBody(), ShoppingCartAPIRequest.class);
            logger.info("Fetched cart details for userId={} => {}",userId,cartRestApiResponse);
        }else{
            logger.error("Failed to fetch cart details with userId : {} => {}",userId,shoppingCartResponse.getStatusCode());
            throw new RuntimeException(String.format("Failed to fetch cart details with userId :: %s",userId));
        }

        List<CartItemResponse> cartItemListResData = cartRestApiResponse.getCartItem().stream()
                .map(dbItem -> {
                    CartItemResponse cartItem = new CartItemResponse();
                    cartItem.setCartItemId(dbItem.getCartItemId());
                    cartItem.setCartQuantity(dbItem.getCartItemQuantity());

                    String variantId = dbItem.getVariantId();
                    //calling the product catalog ms rest api to get product variant details using variantId
                    logger.info("Retrieving Product Variant Data by variantId :: {}", variantId);

                    VariantDTO variantData;
                    ResponseEntity<Map<String, Object>> variantDetailsResponse = productCatalogClient.getVariantDetails(variantId);
                    if(variantDetailsResponse.getStatusCode().is2xxSuccessful() && variantDetailsResponse.hasBody()){
                        variantData = objectMapper.convertValue(variantDetailsResponse.getBody(), VariantDTO.class);
                        logger.info("Variant details fetched form product-catalog-ms by variantId :: {} => {}", variantId, variantData);
                    }else {
                        logger.error("Failed to fetch Variant details form product-catalog-ms for variantId :: {} => {}", variantId,variantDetailsResponse.getStatusCode());
                        throw new RuntimeException("Failed to fetch the variant data using variantId :: {}" + variantId);
                    }
                    cartItem.setVariantDetails(variantData);

                    //calling cartValidation api to fetch variant inventory
                    InventoryValidationRes inventoryValidationRes;
                    Map<String, Object> reqParam = Map.of("variantId", variantId,
                            "quantityRequested", dbItem.getCartItemQuantity());
                    logger.info("Request Params for car validation API call : {}",reqParam);
                    ResponseEntity<Map<String, Object>> validationRes = productCatalogInventoryClient.cartValidation(reqParam);
                    if(validationRes.getStatusCode().is2xxSuccessful() && validationRes.hasBody()){
                        inventoryValidationRes = objectMapper.convertValue(validationRes.getBody(), InventoryValidationRes.class);
                        logger.info("Cart Item variant inventory data {} :: with  variantId :: {} ", inventoryValidationRes,variantId);
                        cartItem.setCartItemInventoryData(inventoryValidationRes);
                    }else {
                        logger.error("Failed to do the cart validation of variant using variantId={} :: StatusCode={}",variantId,validationRes.getStatusCode());
                        throw new RuntimeException(String.format("Error while calling the product inventory checkStockStatus API :: %s",validationRes.getStatusCode()));
                    }
                    return cartItem;
                })
                .toList();

        BigDecimal cartTotal = calculateCartTotal(cartItemListResData);

        response.setCartId(cartRestApiResponse.getCartId());
        response.setCartCreatedAt(cartRestApiResponse.getCartCreatedAt());
        response.setCartModifiedAt(cartRestApiResponse.getCartModifiedAt());
        response.setItems(cartItemListResData);
        response.setCartTotal(cartTotal);
        logger.info("Successfully retrieved the shopping cart details for user with userId : {} => {}",userId,response);
        return response;
    }


    private UserInfoDTO getUserInfo(String userId){
        logger.info("Inside getUserInfo method :: Fetching user info by userId => {}",userId);
        UserInfoDTO userData;
        ResponseEntity<Map<String,Object>> userMgmtResponse = userMgmtClient.getUserId(userId);
        if(userMgmtResponse.getStatusCode().is2xxSuccessful() && userMgmtResponse.hasBody()){
            Map<String, Object> responseBody = userMgmtResponse.getBody();
            userData = objectMapper.convertValue(responseBody, UserInfoDTO.class);
            logger.info("User Info fetched from userMgmt MS with userId :: {} => {}",userId,userData);
        }else{
            throw new RuntimeException("Failed to fetch the user info from userMgmt rest call using userId :: " + userId);
        }
        return userData;
    }

    /**
     * @param itemList list of all the variants present in the cart
     * @return total cart amount calculated with GST/Tax and based on the applied offers
     * Note:
     * check If onSalePrice is present since onSalePrice is calculated based on the applied offer in the offer-mgmt-ms
     * If the onSalePrice is null that means there is no offer applied on this variant and to be sold on the onBuyPrice itself
     * The onBuyPrice is the price inclusive of 18% GST in the current implementation
     */
    private BigDecimal calculateCartTotal(List<CartItemResponse> itemList) {
        return itemList.stream()
                .map(cartItem -> {

                    if(cartItem.getVariantDetails().getOnSalePrice()!=null)
                        return Map.entry(cartItem.getCartItemInventoryData().getQuantityRequested(), cartItem.getVariantDetails().getOnSalePrice());
                    else
                        return Map.entry(cartItem.getCartItemInventoryData().getQuantityRequested(), cartItem.getVariantDetails().getBuyPrice());
                })
                .map(entry -> entry.getValue().multiply(BigDecimal.valueOf(entry.getKey())))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("error while calculating the cart total"));
    }

    public Map<String,Object> applyCoupon(ApplyCouponReq req) {
        logger.info("Inside applyCoupon method ::validate the couponCode for all variantIds provided");

        List<String> variantIdReqList= req.getVariantInfo().keySet().stream().toList();

        CouponValidationRes couponValidationRes;
        //check on which requested variantIds applied coupon is applicable
        ResponseEntity<Map<String, Object>> response = offerMgmtClient.validateCoupon(req.getCouponCode(), variantIdReqList);
        if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
            logger.info("Response from validateCoupon API call :: {}",response.getBody());
            couponValidationRes = objectMapper.convertValue(Objects.requireNonNull(response.getBody()).get("result"), CouponValidationRes.class);
        }else{
            String errCode = String.valueOf(Objects.requireNonNull(response.getBody()).get(ErrorCode.ERROR_CODE_KEY));
            if( errCode.equals(ErrorCode.COUPON_INACTIVE_ERROR_CODE)){
                logger.error("Response from validateCoupon API call :: coupon code ={} is In-Active :: StatusCode={} :: ErrCode={} ",req.getCouponCode(),response.getStatusCode(),errCode);
                return Map.of("result",new ErrorResponse("Coupon is In-Active or Usage Limit has exceeded",HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),ErrorCode.COUPON_INACTIVE_ERROR_CODE));
            }else if(errCode.equals(ErrorCode.COUPON_NOT_APPLICABLE_ERROR_CODE)){
                logger.error("Response from validateCoupon API call :: Provided coupon is In-Active = {} :: StatusCode={} :: ErrCode={} ",req.getCouponCode(),response.getStatusCode(),errCode);
                return Map.of("result",new ErrorResponse("Coupon is not applicable to any variant",HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),ErrorCode.COUPON_NOT_APPLICABLE_ERROR_CODE));
            }else{
                logger.error("Error Response from validate coupon API call with coupon code={} :: StatusCode={} :: ErrCode={} ",req.getCouponCode(),response.getStatusCode(),errCode);
                throw new RuntimeException(String.format("Error while calling the validate coupon API :: %s",response.getStatusCode()));
            }

        }

        GetCouponResponse couponData = couponValidationRes.getCouponData();
        //verify if the coupon is applicable on the request by checking the cartTotal with respect to the coupon minOrderValue
        double total = Double.parseDouble(req.getCartTotal());
        if(total < couponData.getMinOrderValue()){
            logger.error("cart total is less than the coupon min. order value so coupon cannot be applied :: couponCode={} :: minOrderValue={} :: ErrCode={}",req.getCouponCode(),couponData.getMinOrderValue(),ErrorCode.COUPON_NOT_APPLICABLE_MIN_ORDER_VALUE_ERROR_CODE);
            return Map.of("result", new ErrorResponse(String.format("coupon is applicable only on min. Order Value of %s",couponData.getMinOrderValue())
                    , HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),ErrorCode.COUPON_NOT_APPLICABLE_MIN_ORDER_VALUE_ERROR_CODE));
        }

        //fetch all variant Id details on which coupon is applicable from product-catalog-ms to calculate the onSalePrice after applying the coupon
        String discountType = couponData.getDiscountType();
        String discountValue = String.valueOf(couponData.getDiscountValue());

        Set<String> applicableVariantIds = couponValidationRes.getApplicableToRequestedVariantIds().stream().map(CouponVariantDTO::getVariantId).collect(Collectors.toSet());
        logger.info("re-calculate the cart total after applying the coupon on applicable variant Id :: {}",applicableVariantIds);
        BigDecimal reCalculatedCartTotal = variantIdReqList.stream().map(variantId -> {
                    ResponseEntity<Map<String, Object>> variantResponse = productCatalogClient.getVariantDetails(variantId);
                    if (variantResponse.getStatusCode().is2xxSuccessful() && variantResponse.hasBody()) {
                        logger.info("Response from get variant details API call :: {}", variantResponse.getBody());
                        return objectMapper.convertValue(variantResponse.getBody(), VariantDTO.class);
                    } else {
                        logger.error("Error Response from get variant details API call with variantId={} :: StatusCode={}", variantId, variantResponse.getStatusCode());
                        throw new RuntimeException(String.format("Error while calling the get variant details API :: %s", variantResponse.getStatusCode()));
                    }
                }).map(variantDTO -> {
                    if (applicableVariantIds.contains(variantDTO.getVariantId())) {
                        return calculatePriceAfterCouponDiscountOnVariant(discountType, discountValue, variantDTO);
                    } else {
                        return variantDTO;
                    }
                }).map(variantDTO -> {
                    if(variantDTO.getOnSalePrice()!=null)
                        return variantDTO.getOnSalePrice().multiply(BigDecimal.valueOf(req.getVariantInfo().get(variantDTO.getVariantId()))); //multiply the calculated onSalePrice if present with the amount of time that variant is present in the cart
                    else
                        return variantDTO.getBuyPrice().multiply(BigDecimal.valueOf(req.getVariantInfo().get(variantDTO.getVariantId()))); // multiply the calculated buyPrice with the amount of time that variant is present in the cart
                })
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("Error while reCalculatedCartTotal the cart total"));



        return Map.of("result",new ApplyCouponResponse(reCalculatedCartTotal,applicableVariantIds,
                String.format("Coupon is successfully applied and cart total is re-calculated after applying coupon with code=%s",couponData.getCouponCode()),HttpStatus.OK.getReasonPhrase()));
    }

    /**
     * @param discountType  percentage or fixed
     * @param discountValue numeric value
     * @param variantDetails variant data on which coupon discount is to be applied
     */
    private VariantDTO calculatePriceAfterCouponDiscountOnVariant(String discountType, String discountValue,VariantDTO variantDetails) {
        //if onSalePrice is present then calculate the discounted price over the top of present onSalePrice else calculate it on the current buyPrice
        //place the discountedPrice on onSalePrice in both the cases as its the filed reflecting the discount from offer and coupon on the UI
        if(variantDetails.getOnSalePrice() !=null ){
            BigDecimal discountedPrice = calculateDiscount(variantDetails.getOnSalePrice(),discountType,discountValue);
            variantDetails.setOnSalePrice(discountedPrice);
        }else{
            BigDecimal discountedPrice = calculateDiscount(variantDetails.getBuyPrice(),discountType,discountValue);
            variantDetails.setOnSalePrice(discountedPrice);
        }
        return variantDetails;

    }

    private BigDecimal calculateDiscount(BigDecimal price ,String discountType,String couponDiscountValue){
        BigDecimal hundred = new BigDecimal(Constants.HUNDRED);
        switch (discountType){
            //calculate price by percentage
            case Constants.DISCOUNT_TYPE_PERCENT -> {
                BigDecimal discountValue = new BigDecimal(couponDiscountValue);
                // Convert discount rate from percentage to fraction
                BigDecimal  discountRate = discountValue.divide(hundred);
                // Calculate the discount amount
                BigDecimal discountAmount = price.multiply(discountRate);
                // Calculate the on-sale price
                BigDecimal onSalePrice = price.subtract(discountAmount);
                // rounding the on-sale price by 2 decimal
                return onSalePrice.setScale(2, RoundingMode.HALF_UP);
            }
            //calculate price by fixed amount
            case Constants.DISCOUNT_TYPE_FIXED -> {
                // fixed price to substract in Bigdecimal
                BigDecimal fixedAmount = new BigDecimal(couponDiscountValue);
                // Calculate the on-sale price for fixed amount
                return price.subtract(fixedAmount);
            }
            default -> throw new RuntimeException(String.format("Invalid value received for discountType=%s while calculating coupon discount",discountType));
        }
    }

}

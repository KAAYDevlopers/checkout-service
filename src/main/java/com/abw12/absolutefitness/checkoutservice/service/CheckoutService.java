package com.abw12.absolutefitness.checkoutservice.service;

import com.abw12.absolutefitness.checkoutservice.dto.request.InventoryValidationReq;
import com.abw12.absolutefitness.checkoutservice.dto.request.ShoppingCartAPIRequest;
import com.abw12.absolutefitness.checkoutservice.dto.response.*;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.ProductCatalogClient;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.ProductCatalogInventoryClient;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.ShoppingCartClient;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.UserMgmtClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    private ObjectMapper objectMapper;

    public ResponseData getCheckoutPageData(String userId){
        logger.info("Retrieving the checkout page info for user with userId :: {}",userId);
        ResponseData responseData = new ResponseData();
        responseData.setCartData(getShoppingCartData(userId));
        responseData.setUserInfo(getUserInfo(userId));
        logger.info("Successfully retrieved checkout page data for user with userId :: {} => {}",userId,responseData);
        return responseData;
    }

    private CartResponse getShoppingCartData(String userId) {
        logger.info("Fetching the cart data by userId :: {}",userId);
        CartResponse response = new CartResponse();

        //calling the shopping cart ms rest api to get cart details for user
        String shoppingCartClientResponse = shoppingCartClient.getShoppingCartDetails(userId);
        ShoppingCartAPIRequest cartRestApiResponse;
        try {
            cartRestApiResponse= objectMapper.readValue(shoppingCartClientResponse, ShoppingCartAPIRequest.class);
            logger.info("Fetched cart details with userId : {} => {}",userId,cartRestApiResponse);
        } catch (JsonProcessingException e) {
            logger.error("Error while parsing shopping cart client response => {}" , e.getMessage());
            throw new RuntimeException(e);
        }

        List<CartItemResponse> cartItemListResData = cartRestApiResponse.getCartItem().stream()
                .map(dbItem -> {
                    CartItemResponse cartItem = new CartItemResponse();
                    cartItem.setCartItemId(dbItem.getCartItemId());
                    cartItem.setCartQuantity(dbItem.getCartItemQuantity());

                    String variantId = dbItem.getVariantId();
                    //calling the product catalog ms rest api to get product variant details using variantId
                    logger.info("Retrieving Product Variant Data by variantId :: {}", variantId);

                    VariantDTO variantDataRes;
                    ResponseEntity<Map<String, Object>> variantDetails = productCatalogClient.getVariantDetails(variantId);
                    if(variantDetails.getStatusCode().is2xxSuccessful() && variantDetails.hasBody()){
                        variantDataRes = objectMapper.convertValue(variantDetails.getBody(), VariantDTO.class);
                        logger.info("Variant details fetched form product-catalog-ms by variantId :: {} => {}", variantId, variantDataRes);
                    }else {
                        throw new RuntimeException("Failed to fetch the variant data using variantId :: {}" + variantId);
                    }
                    cartItem.setVariantDetails(variantDataRes);

                    //calling cartValidation api to fetch variant inventory
                    InventoryValidationRes variantInventoryData;
                    InventoryValidationReq inventoryValidationReq = new InventoryValidationReq(variantId, dbItem.getCartItemQuantity());
                    ResponseEntity<Map<String, Object>> variantInventoryRes = productCatalogInventoryClient.cartValidation(inventoryValidationReq);
                    if(variantInventoryRes.getStatusCode().is2xxSuccessful() && variantInventoryRes.hasBody()){
                        variantInventoryData = objectMapper.convertValue(variantInventoryRes.getBody(), InventoryValidationRes.class);
                        logger.info("Cart Item variant inventory data {} :: with  variantId :: {} ", variantInventoryData,variantId);
                        cartItem.setCartItemInventoryData(variantInventoryData);
                    }else {
                        throw new RuntimeException("Failed to check the cart validation of variant using variantId :: {}" + variantId);
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
     * @return total cart amount calculated without GST/Tax
     * todo - decide and implement tax and offer calculation logic
     */
    private BigDecimal calculateCartTotal(List<CartItemResponse> itemList) {
        return itemList.stream()
                .map(cartItem -> Map.entry(cartItem.getCartItemInventoryData().getQuantityRequested(), cartItem.getVariantDetails().getOnSalePrice()))
                .map(entry -> entry.getValue().multiply(BigDecimal.valueOf(entry.getKey())))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("error while calculating the cart total"));
    }
}

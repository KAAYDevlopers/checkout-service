package com.abw12.absolutefitness.checkoutservice.service;

import com.abw12.absolutefitness.checkoutservice.dto.request.ShoppingCartDTO;
import com.abw12.absolutefitness.checkoutservice.dto.response.CheckoutCartItem;
import com.abw12.absolutefitness.checkoutservice.dto.response.CheckoutCartResponse;
import com.abw12.absolutefitness.checkoutservice.dto.response.VariantDTO;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.ProductCatalogClient;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.ShoppingCartClient;
import com.abw12.absolutefitness.checkoutservice.gateway.interfaces.UserMgmtClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserMgmtClient userMgmtClient;
    @Autowired
    private ObjectMapper objectMapper;

    public CheckoutCartResponse getBagData(String userId) {
        logger.info("Fetching the cart data by userId :: {}",userId);
        CheckoutCartResponse response = new CheckoutCartResponse();

        //calling the shopping cart ms rest api to get cart details for user
        String shoppingCartClientResponse = shoppingCartClient.getShoppingCartDetails(userId);
        ShoppingCartDTO cartRestApiResponse;
        try {
            cartRestApiResponse= objectMapper.readValue(shoppingCartClientResponse, ShoppingCartDTO.class);
            logger.debug("Fetched cart details with userId : {} => {}",userId,cartRestApiResponse);
        } catch (JsonProcessingException e) {
            logger.error("Error while parsing shopping cart client response => {}" , e.getMessage());
            throw new RuntimeException(e);
        }

        List<CheckoutCartItem> cartItemListResData = cartRestApiResponse.getCartItem().stream()
                .map(dbItem -> {
                    CheckoutCartItem cartItem = new CheckoutCartItem();
                    cartItem.setCartItemId(dbItem.getCartItemId());
                    cartItem.setCartItemQuantity(dbItem.getCartItemQuantity());
                    String variantId = dbItem.getVariantId();
                    //calling the product catalog ms rest api to get product variant details using variantId
                    logger.info("Retrieving Product Variant Data by variantId :: {}", variantId);
                    String variantDetails = productCatalogClient.getVariantDetails(variantId);
                    VariantDTO variantDataRes;
                    try {
                        variantDataRes = objectMapper.readValue(variantDetails, VariantDTO.class);
                        logger.debug("Variant details fetched form product-catalog-ms by variantId :: {} => {}", variantId, variantDataRes);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    cartItem.setVariantDetails(variantDataRes);
                    return cartItem;
                })
                .toList();

        BigDecimal cartTotal = calculateCartTotal(cartItemListResData);

        response.setCartId(cartRestApiResponse.getCartId());
        response.setCartCreatedAt(cartRestApiResponse.getCartCreatedAt());
        response.setCartModifiedAt(cartRestApiResponse.getCartModifiedAt());
        response.setItems(cartItemListResData);
        response.setCartTotal(cartTotal);
        logger.info("Successfully retrieve the shooping cart details for user with userId : {}",userId);
        return response;
    }

    private BigDecimal calculateCartTotal(List<CheckoutCartItem> itemList) {
        return itemList.stream()
                .map(cartItem -> Map.entry(cartItem.getCartItemQuantity(), cartItem.getVariantDetails().getOnSalePrice()))
                .map(entry -> entry.getValue().multiply(BigDecimal.valueOf(entry.getKey())))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("error while calculating the cart total"));
    }
}

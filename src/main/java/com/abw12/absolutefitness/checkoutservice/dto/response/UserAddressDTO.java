package com.abw12.absolutefitness.checkoutservice.dto.response;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDTO {

    @Id
    private String addressId;
    private String userId;
    private String addressLine;
    private String localityArea;
    private String landmark;
    private Integer pinCode;
    private String city;
    private String state;
    private String addressCreatedAt;
    private String addressModifiedAt;
}

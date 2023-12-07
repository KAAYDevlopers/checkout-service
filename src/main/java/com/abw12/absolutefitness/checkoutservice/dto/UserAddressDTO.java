package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String addressLine;
    private String localityArea;
    private String landmark;
    @NotNull
    private Integer pinCode;
    @NotNull
    private String city;
    @NotNull
    private String state;
    private String addressCreatedAt;
    private String addressModifiedAt;
}

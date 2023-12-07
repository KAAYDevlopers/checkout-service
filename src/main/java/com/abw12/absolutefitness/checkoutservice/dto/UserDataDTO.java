package com.abw12.absolutefitness.checkoutservice.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {

    @Id
    private String userId;
    @NotBlank
    private String userName;
    @NotNull
    private Long phoneNumber;
    @NotBlank
    private String emailId;
    @NotNull
    private List<UserAddressDTO> userAddresses;
    private String userCreatedAt;
    private String userModifiedAt;
}

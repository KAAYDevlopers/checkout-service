package com.abw12.absolutefitness.checkoutservice.dto.response;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    @Id
    private String userId;
    private String userName;
    private Long phoneNumber;
    private String emailId;
    private List<UserAddressDTO> userAddresses;
    private String userCreatedAt;
    private String userModifiedAt;
}

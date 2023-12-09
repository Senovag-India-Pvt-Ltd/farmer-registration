package com.sericulture.registration.model.dto.govtSmsService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GovtSmsServiceDTO {
    private String username;
    private String password;
    private String message;
    private String senderId;
    private String mobileNumber;
    private String secureKey;
    private String templateid;
}

package com.sericulture.registration.model.dto.otp;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "UserId must contain only letters and numbers")
    private String userId;
    @Pattern(regexp = "^[0-9\\s]*$", message = "Otp must contain only numbers")
    private String otp;
}
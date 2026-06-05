package com.sericulture.registration.model.dto.fruitsApi;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request body received by the backend for the Get-Farmer-By-Aadhaar-Hash endpoint.
 * The caller sends the raw 12-digit Aadhaar number; the backend hashes it (SHA-256)
 * before forwarding to FRUITS. Credentials are added server-side, never by the caller.
 */
@Data
public class FruitsAadharDTO {
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadharNo;

    public FruitsAadharDTO() {
    }

    public FruitsAadharDTO(String aadharNo) {
        this.aadharNo = aadharNo;
    }
}

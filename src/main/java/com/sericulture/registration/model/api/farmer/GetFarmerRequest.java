package com.sericulture.registration.model.api.farmer;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetFarmerRequest extends RequestBody {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Fruits Id must contain only letters and numbers")
    @Schema(name = "fruitsId", example = "2")
    String fruitsId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Farmer number must contain only letters and numbers")
    @Schema(name = "farmerNumber", example = "2")
    String farmerNumber;

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Mobile number must contain only numbers")
    @Schema(name = "mobileNumber", example = "7788990066")
    String mobileNumber;

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Farmer Account number must contain only numbers")
    @Schema(name = "farmerBankAccountNumber", example = "123")
    String farmerBankAccountNumber;
}
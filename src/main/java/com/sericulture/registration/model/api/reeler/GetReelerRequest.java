package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetReelerRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Reeler number must contain only letters and numbers")
    @Schema(name = "reelerNumber", example = "2")
    String reelerNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Reeler license number must contain only letters and numbers")
    @Schema(name = "reelingLicenseNumber", example = "2")
    String reelingLicenseNumber;

    @Pattern(regexp = "^[+0-9\\s]*$", message = "Mobile number must contain only numbers")
    @Schema(name = "mobileNumber", example = "7788990066")
    String mobileNumber;

    @Schema(name = "marketId", example = "1")
    Long marketId;

}
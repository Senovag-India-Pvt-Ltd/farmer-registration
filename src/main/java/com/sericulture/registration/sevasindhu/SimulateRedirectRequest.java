package com.sericulture.registration.sevasindhu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SimulateRedirectRequest {

    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @Schema(name = "fruitsId", example = "FID2806000009439")
    String fruitsId;

    @Schema(name = "sevaSindhuReference", example = "SSAPP-TEST-000001")
    String sevaSindhuReference;
}

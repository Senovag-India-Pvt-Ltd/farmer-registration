package com.sericulture.registration.sevasindhu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulateRedirectResponse {

    @Schema(name = "sevaSindhuReference", example = "SSAPP-TEST-000001")
    String sevaSindhuReference;

    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @Schema(name = "fruitsId", example = "FID2806000009439")
    String fruitsId;

    @Schema(name = "farmerRegistered", example = "false")
    Boolean farmerRegistered;

    // "FARMER_REGISTRATION" or "SCHEME_APPLICATION" -- matches the nextAction
    // values used in the source requirement document's own example payloads.
    @Schema(name = "nextAction", example = "FARMER_REGISTRATION")
    String nextAction;

    @Schema(name = "farmerId", example = "12345")
    Long farmerId;

    @Schema(name = "farmerName", example = "MUNIHANUMAPPA")
    String farmerName;

    // Whether a live FRUITS government-API lookup was attempted for an
    // unregistered farmer, and its outcome -- purely informational, this
    // service is not reachable from every environment.
    @Schema(name = "fruitsLookupAttempted", example = "true")
    Boolean fruitsLookupAttempted;

    @Schema(name = "fruitsLookupSuccess", example = "false")
    Boolean fruitsLookupSuccess;

    @Schema(name = "fruitsLookupMessage", example = "FRUITS service unavailable in this environment")
    String fruitsLookupMessage;

    @Schema(name = "fruitsLookupFarmerName", example = "MUNIHANUMAPPA")
    String fruitsLookupFarmerName;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "FRUITS ID or mobile number is required")
    String error_description;
}

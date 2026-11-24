package com.sericulture.registration.model.api.farmerRepresentative;

import com.sericulture.registration.model.api.ResponseBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = false)
public class FarmerRepresentativeResponse extends ResponseBody {
    @Schema(name = "farmerRepresentativeId", example = "1")
    Long farmerRepresentativeId;

    @Schema(name = "farmerId", example = "1")
    Long farmerId;

    @Schema(name = "name", example = "Ramesh K")
    String name;

    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @Schema(name = "address", example = "Ramanagara, Karnataka")
    String address;

    @Schema(name = "relationshipId", example = "1")
    Long relationshipId;

    @Schema(name = "relationshipName", example = "Son")
    String relationshipName;

    @Schema(name = "idProofType", example = "VOTER_ID")
    String idProofType;

    @Schema(name = "idProofNumber", example = "ABC1234567", description = "Aadhaar is returned masked")
    String idProofNumber;

    @Schema(name = "authorisedFrom", example = "2026-11-24")
    LocalDate authorisedFrom;

    @Schema(name = "authorisedTo", example = "2027-11-23")
    LocalDate authorisedTo;

    @Schema(name = "authorisedToday", example = "true", description = "Within the authorised period today")
    Boolean authorisedToday;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Maximum 3 agents allowed for a farmer")
    String error_description;
}

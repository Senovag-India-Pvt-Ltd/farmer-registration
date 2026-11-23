package com.sericulture.registration.model.api.reelerRepresentative;

import com.sericulture.registration.model.api.ResponseBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = false)
public class ReelerRepresentativeResponse extends ResponseBody {
    @Schema(name = "reelerRepresentativeId", example = "1")
    Long reelerRepresentativeId;

    @Schema(name = "reelerId", example = "1")
    Long reelerId;

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

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "error_description", example = "Maximum 3 representatives allowed for a reeler")
    String error_description;
}

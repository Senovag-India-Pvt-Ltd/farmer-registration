package com.sericulture.registration.model.api.reelerRepresentative;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Add (reelerRepresentativeId empty) or edit (reelerRepresentativeId set) one reeler representative. */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReelerRepresentativeRequest extends RequestBody {
    @Schema(name = "reelerRepresentativeId", example = "1", description = "Only for edit")
    Long reelerRepresentativeId;

    @NotNull(message = "reelerId is required")
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @NotBlank(message = "Representative name is required")
    @Size(max = 150, message = "Representative name must be at most 150 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M} .']*$", message = "Representative name must contain only letters")
    @Schema(name = "name", example = "Ramesh K")
    String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    @Schema(name = "mobileNumber", example = "9876543210")
    String mobileNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must be at most 500 characters")
    @Schema(name = "address", example = "Ramanagara, Karnataka")
    String address;

    @Schema(name = "relationshipId", example = "1")
    Long relationshipId;

    @Pattern(regexp = "^(AADHAAR|VOTER_ID|DRIVING_LICENCE|PAN|RATION_CARD|OTHER)?$", message = "Invalid ID proof type")
    @Schema(name = "idProofType", example = "VOTER_ID")
    String idProofType;

    @Size(max = 30, message = "ID proof number must be at most 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9/ -]*$", message = "ID proof number must contain only letters and numbers")
    @Schema(name = "idProofNumber", example = "ABC1234567")
    String idProofNumber;
}

package com.sericulture.registration.model.api.traderLicense;

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
public class TraderLicenseRequest extends RequestBody {
    @Pattern(regexp = "^[a-zA-Z0-9-/\\s]*$", message = "ARN Number must contain only letters and numbers")
    @Schema(name = "arnNumber", example = "167676")
    String arnNumber;

    @Schema(name = "traderTypeMasterId", example = "1")
    Long traderTypeMasterId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "First name must contain only letters and numbers")
    @Schema(name = "firstName", example = "test")
    String firstName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Middle name must contain only letters and numbers")
    @Schema(name = "middleName", example = "test")
    String middleName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Last name must contain only letters and numbers")
    @Schema(name = "lastName", example = "test")
    String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Father name must contain only letters and numbers")
    @Schema(name = "fatherName", example = "Test")
    String fatherName;

    @Schema(name = "districtId", example = "1")
    Long districtId;

    @Schema(name = "stateId", example = "1")
    Long stateId;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Address must contain only letters and numbers")
    @Schema(name = "address", example = "Test")
    String address;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Premises description must contain only letters and numbers")
    @Schema(name = "premisesDescription", example = "Test")
    String premisesDescription;

    @Schema(name = "applicationDate", example = "2023-11-03 16:27:35.907")
    Date applicationDate;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Application number must contain only letters and numbers")
    @Schema(name = "applicationNumber", example = "4561")
    String applicationNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Trader license number must contain only letters and numbers")
    @Schema(name = "traderLicenseNumber", example = "34541")
    String traderLicenseNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Representative details must contain only letters and numbers")
    @Schema(name = "representativeDetails", example = "Test")
    String representativeDetails;

    @Schema(name = "licenseFee", example = "14.56")
    Double licenseFee;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "License challan number must contain only letters and numbers")
    @Schema(name = "licenseChallanNumber", example = "Test")
    String licenseChallanNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "godown details must contain only letters and numbers")
    @Schema(name = "godownDetails", example = "Test")
    String godownDetails;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Silk exchange mahajar must contain only letters and numbers")
    @Schema(name = "silkExchangeMahajar", example = "Test")
    String silkExchangeMahajar;

    @Schema(name = "licenseNumberSequence", example = "1")
    Long licenseNumberSequence;
}
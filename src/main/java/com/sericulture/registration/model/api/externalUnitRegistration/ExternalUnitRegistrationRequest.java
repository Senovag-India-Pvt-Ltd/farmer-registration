package com.sericulture.registration.model.api.externalUnitRegistration;

import com.sericulture.registration.model.api.RequestBody;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseDetailsRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ExternalUnitRegistrationRequest extends RequestBody {
    @Schema(name = "externalUnitTypeId", example = "1")
    Long externalUnitTypeId;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Name must contain only letters and numbers")
    @Schema(name = "name", example = "Test")
    String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Address must contain only letters and numbers")
    @Schema(name = "address", example = "Test")
    String address;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "License number must contain only letters and numbers")
    @Schema(name = "licenseNumber", example = "435435")
    String licenseNumber;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "External unit number must contain only letters and numbers")
    @Schema(name = "externalUnitNumber", example = "S5346")
    String externalUnitNumber;

//    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Organization name must contain only letters and numbers")
    @Schema(name = "organisationName", example = "Test")
    String organisationName;

    @Schema(name = "raceMasterId", example = "1")
    Long raceMasterId;

    @Schema(name = "userMasterId", example = "1")
    Long userMasterId;

    @Schema(name = "capacity", example = "Test")
    String capacity;

    @Schema(name = "lotNumberNomenclature", example = "Test")
    String lotNumberNomenclature;

    @Schema(name = "bankName", example = "SBI")
    String bankName;

    @Schema(name = "bankAccountNumber", example = "1234567890")
    String bankAccountNumber;

    @Schema(name = "bankBranchName", example = "Bengaluru")
    String bankBranchName;

    @Schema(name = "bankIfscCode", example = "SBIN0005463")
    String bankIfscCode;

    private List<ExternalUnitRegistrationDetailsRequest> externalUnitRegistrationDetailsRequests;
    @Column(name = "tsc_master_id")
    private Long tscMasterId;
    @Column(name = "DISTRICT_ID")
    private Long districtId;

    @Column(name = "TALUK_ID")
    private Long talukId;
    @Column(name = "name_kan")
    private String nameKan;
}
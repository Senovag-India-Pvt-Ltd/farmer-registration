package com.sericulture.registration.model.api.externalUnitRegistration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalUnitRegistrationResponse {
    @Schema(name = "externalUnitRegistrationId", example = "1")
    Long externalUnitRegistrationId;

    @Schema(name = "externalUnitTypeId", example = "1")
    Long externalUnitTypeId;

    @Schema(name = "name", example = "Test")
    String name;

    @Schema(name = "address", example = "Test")
    String address;

    @Schema(name = "licenseNumber", example = "435435")
    String licenseNumber;

    @Schema(name = "externalUnitNumber", example = "S5346")
    String externalUnitNumber;

    @Schema(name = "organisationName", example = "Test")
    String organisationName;

    @Schema(name = "raceMasterId", example = "1")
    Long raceMasterId;

    @Schema(name = "externalUnitTypeName", example = "Test")
    String externalUnitTypeName;

    @Schema(name = "raceMasterName", example = "Test")
    String raceMasterName;

    @Schema(name = "capacity", example = "Test")
    String capacity;

    @Schema(name = "userMasterId", example = "1")
    Long userMasterId;

    @Schema(name = "virtualAccountNumber", example = "3654643675")
    String virtualAccountNumber;

    @Schema(name = "branchName", example = "Bengaluru")
    String branchName;

    @Schema(name = "ifscCode", example = "SBI00457")
    String ifscCode;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;

    @Schema(name = "marketMasterName", example = "SBI00457")
    String marketMasterName;

    @Schema(name = "lotNumberNomenclature", example = "Test")
    String lotNumberNomenclature;

    @Schema(name = "error", example = "true")
    Boolean error;

    @Schema(name = "lock", example = "1")
    Boolean lock;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;



    private int serialNumber;

    private List<Long> externalUnitRegistrationIds;
    @Schema(name = "tsc_master_id", example = "1")
    Long tscMasterId;
    @Schema(name = "DISTRICT_ID",  example = "1")
    Long districtId;

    @Schema(name = "TALUK_ID", example = "1")
    Long talukId;
    @Schema(name = "name_kan", example = "Test")
    String nameKan;
    private List<ExternalUnitRegistrationDetailsRequest> externalUnitRegistrationDetailsRequests;
}
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
    @Schema(name = "arnNumber", example = "167676")
    String arnNumber;

    @Schema(name = "traderTypeMasterId", example = "1")
    Long traderTypeMasterId;

    @Schema(name = "firstName", example = "test")
    String firstName;

    @Schema(name = "middleName", example = "test")
    String middleName;

    @Schema(name = "lastName", example = "test")
    String lastName;

    @Schema(name = "fatherName", example = "Test")
    String fatherName;

    @Schema(name = "districtId", example = "1")
    Long districtId;

    @Schema(name = "stateId", example = "1")
    Long stateId;

    @Schema(name = "address", example = "Test")
    String address;

    @Schema(name = "premisesDescription", example = "Test")
    String premisesDescription;

    @Schema(name = "applicationDate", example = "2023-11-03 16:27:35.907")
    Date applicationDate;

    @Schema(name = "applicationNumber", example = "4561")
    String applicationNumber;

    @Schema(name = "traderLicenseNumber", example = "34541")
    String traderLicenseNumber;

    @Schema(name = "representativeDetails", example = "Test")
    String representativeDetails;

    @Schema(name = "licenseFee", example = "14.56")
    Double licenseFee;

    @Schema(name = "licenseChallanNumber", example = "Test")
    String licenseChallanNumber;

    @Schema(name = "godownDetails", example = "Test")
    String godownDetails;

    @Schema(name = "silkExchangeMahajar", example = "Test")
    String silkExchangeMahajar;

    @Schema(name = "licenseNumberSequence", example = "1")
    Long licenseNumberSequence;

    @Schema(name = "silkType", example = "Test")
    String silkType;

    @Schema(name = "marketMasterId", example = "1")
    Long marketMasterId;


    @Schema(name = "mobileNumber",  example = "1")
    String mobileNumber;
}
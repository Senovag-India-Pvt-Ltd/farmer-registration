package com.sericulture.registration.model.api.fruitsApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFruitsResponse {
    @Schema(name = "StatusCode", example = "1")
    Integer StatusCode;

    @Schema(name = "StatusText", example = ",")
    String StatusText;

    @Schema(name = "FarmerID", example = "FID2806000009439")
    String FarmerID;

    @Schema(name = "Name", example = "MUNIHANUMAPPA")
    String Name;

    @Schema(name = "NameKan", example = "munireddy")
    String NameKan;

    @Schema(name = "FatherName", example = "Munivenkatappa")
    String FatherName;

    @Schema(name = "FatherNameKan", example = "Munivenkatappa")
    String FatherNameKan;

    @Schema(name = "Gender", example = "Male")
    String Gender;

    @Schema(name = "GenderStatus", example = "Declared")
    String GenderStatus;

    @Schema(name = "Caste", example = "SC")
    String Caste;

    @Schema(name = "CasteStatus", example = "SC")
    String CasteStatus;

    @Schema(name = "RDNumber", example = "SC")
    String RDNumber;

    @Schema(name = "PhysicallyChallenged", example = "No")
    String PhysicallyChallenged;

    @Schema(name = "Minority", example = "No")
    String Minority;

    @Schema(name = "FarmerType", example = "No")
    String FarmerType;

    @Schema(name = "ResidentialAddress", example = "Kempadenahlli  Village ambajidurga Hobli Chintamani Taluk")
    String ResidentialAddress;

    @Schema(name = "Pincode", example = "")
    String Pincode;

    @Schema(name = "Landdata", example = "[{},{}]")
    List<GetLandDetailsResponse> Landdata;

    @Schema(name = "error", example = "Response unavailable")
    String error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}

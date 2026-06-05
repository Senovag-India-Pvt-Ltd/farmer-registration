package com.sericulture.registration.model.api.fruitsApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Single crop-survey record returned by the FRUITS GetCropSurveyDataByYear API.
 * Field names match the FRUITS API document exactly.
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CropSurveyData {
    @Schema(name = "FarmerID", example = "FID150*****23542")
    String FarmerID;

    @Schema(name = "DistrictCode", example = "15")
    Integer DistrictCode;

    @Schema(name = "TalukCode", example = "5")
    Integer TalukCode;

    @Schema(name = "HobliCode", example = "4")
    Integer HobliCode;

    @Schema(name = "VillageCode", example = "14")
    Integer VillageCode;

    @Schema(name = "LandCode", example = "20")
    Integer LandCode;

    @Schema(name = "Surveyno", example = "1")
    Integer Surveyno;

    @Schema(name = "Surnoc", example = "*")
    String Surnoc;

    @Schema(name = "Hissano", example = "1")
    String Hissano;

    @Schema(name = "OwnerNo", example = "6")
    Integer OwnerNo;

    @Schema(name = "MainOwnerNo", example = "6")
    Integer MainOwnerNo;

    @Schema(name = "OwnerName", example = "**************")
    String OwnerName;

    @Schema(name = "CropYear", example = "2023-24")
    String CropYear;

    @Schema(name = "CropSeasonCode", example = "1")
    String CropSeasonCode;

    @Schema(name = "CropSeasonName", example = "Kharif")
    String CropSeasonName;

    @Schema(name = "CropCode", example = "24")
    String CropCode;

    @Schema(name = "CropName", example = "Betel Nuts (Areca nuts)")
    String CropName;

    @Schema(name = "CropAcre", example = "2")
    Integer CropAcre;

    @Schema(name = "CropGunta", example = "11")
    Integer CropGunta;

    @Schema(name = "CropFgunta", example = "0.0")
    Float CropFgunta;
}

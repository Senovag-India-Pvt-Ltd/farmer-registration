package com.sericulture.registration.model.api.fruitsApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLandDetailsResponse {
    @Schema(name = "DistrictName", example = "Chikkaballapur")
    String DistrictName;

    @Schema(name = "TalukName", example = "Chinthamani")
    String TalukName;

    @Schema(name = "HobliName", example = "Ambajidurga")
    String HobliName;

    @Schema(name = "VillageName", example = "Kempadenahalli")
    String VillageName;

    @Schema(name = "OwnerName", example = "ಮುನಿರೆಡ್ಡಿ")
    String OwnerName;

    @Schema(name = "NameScore", example = "100")
    Integer NameScore;

    @Schema(name = "Surveyno", example = "16")
    Integer Surveyno;

    @Schema(name = "Surnoc", example = "*")
    String Surnoc;

    @Schema(name = "Hissano", example = "*")
    String Hissano;

    @Schema(name = "OwnerNo", example = "16")
    Integer OwnerNo;

    @Schema(name = "MainOwnerNo", example = "16")
    Integer MainOwnerNo;

    @Schema(name = "Acre", example = "16")
    Integer Acre;

    @Schema(name = "Gunta", example = "16")
    Integer Gunta;

    @Schema(name = "Fgunta", example = "16")
    Float Fgunta;

    @Schema(name = "error", example = "Response unavailable")
    String error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}

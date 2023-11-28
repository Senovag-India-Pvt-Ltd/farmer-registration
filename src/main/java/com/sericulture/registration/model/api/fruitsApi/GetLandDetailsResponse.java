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

    @Schema(name = "Surveyno", example = "123")
    String Surveyno;

    @Schema(name = "Hissno", example = "*")
    String Hissno;

    @Schema(name = "error", example = "Response unavailable")
    String error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}

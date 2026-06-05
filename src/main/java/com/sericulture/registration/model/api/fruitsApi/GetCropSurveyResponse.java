package com.sericulture.registration.model.api.fruitsApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Response returned by the FRUITS GetCropSurveyDataByYear API.
 * Field names match the FRUITS API document exactly.
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCropSurveyResponse {
    @Schema(name = "StatusCode", example = "1")
    Integer StatusCode;

    @Schema(name = "StatusText", example = "Success")
    String StatusText;

    @Schema(name = "CropSurveyData", example = "[{},{}]")
    List<CropSurveyData> CropSurveyData;

    @Schema(name = "error", example = "Response unavailable")
    String error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;
}

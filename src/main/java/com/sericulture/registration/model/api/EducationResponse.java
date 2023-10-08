package com.sericulture.registration.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EducationResponse extends BaseResponse {

    @Schema(name = "name", example = "Bachelor of Engineering", required = true)
    String name;
    @Schema(name = "code", example = "BE", required = true)
    String code;
}

package com.sericulture.registration.model.api.education;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class EducationResponse extends BaseResponse {

    @Schema(name="id", example = "1")
    int id;
    @Schema(name = "name", example = "Bachelor of Engineering")
    String name;
    @Schema(name = "code", example = "BE")
    String code;
}

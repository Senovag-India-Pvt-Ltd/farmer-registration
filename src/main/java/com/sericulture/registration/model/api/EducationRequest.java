package com.sericulture.registration.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class EducationRequest extends RequestBody {
    @Schema(name = "name", example = "Bachelor of Engineering", required = true)
    String name;
    @Schema(name = "code", example = "BE", required = true)
    String code;
}

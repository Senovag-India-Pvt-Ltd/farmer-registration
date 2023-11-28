package com.sericulture.registration.model.api.caste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CasteResponse {

    @Schema(name="id", example = "1")
    int id;

    @Schema(name = "title", example = "GM")
    String title;

    @Schema(name = "code", example = "123")
    String code;

    @Schema(name = "error", example = "0")
    String error;

    @Schema(name = "error_description", example = "Incorrect caste")
    String error_description;
}
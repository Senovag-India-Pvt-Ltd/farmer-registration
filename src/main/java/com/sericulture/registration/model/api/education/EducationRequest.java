package com.sericulture.registration.model.api.education;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EducationRequest extends RequestBody {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Education must contain only letters and numbers")
    @Schema(name = "name", example = "Bachelor of Engineering", required = true)
    String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\s\\u0C80-\\u0CFF]*$", message = "Education name in kannada must contain only letters and numbers")
    @Schema(name = "educationNameInKannada", example = "ಭಾಷೆ")
    String educationNameInKannada;
}

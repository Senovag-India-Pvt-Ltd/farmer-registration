package com.sericulture.registration.model.api.education;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EducationRequest extends RequestBody {

    @Schema(name = "name", example = "Bachelor of Engineering", required = true)
    String name;
}

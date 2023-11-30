package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetFruitsIdRequest extends RequestBody {
    @Schema(name = "fruitsId", example = "1")
    String fruitsId;
}
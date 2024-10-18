package com.sericulture.registration.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SearchRequest extends RequestBody {
    @Schema(name = "type", example = "mobileNumber")
     String type;
    @Schema(name = "text", example = "1")
     String text;
}

package com.sericulture.registration.model.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SearchByColumnRequest {
    @Schema(name = "searchText", example = "Kayappa")
    String searchText;

    @Schema(name = "searchColumn", example = "reeler.reelerName")
    String searchColumn;
}
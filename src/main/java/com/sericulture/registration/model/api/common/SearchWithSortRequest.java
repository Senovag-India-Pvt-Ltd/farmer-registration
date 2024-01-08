package com.sericulture.registration.model.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SearchWithSortRequest {
    @Schema(name = "searchText", example = "shimoga")
    String searchText;

    @Schema(name = "joinColumn", example = "district.districtName")
    String joinColumn;

    @Schema(name = "sortColumn", example = "districtName")
    String sortColumn;

    @Schema(name = "sortOrder", example = "asc")
    String sortOrder;

    @Schema(name = "pageNumber", example = "0")
    String pageNumber;

    @Schema(name = "pageSize", example = "10")
    String pageSize;
}

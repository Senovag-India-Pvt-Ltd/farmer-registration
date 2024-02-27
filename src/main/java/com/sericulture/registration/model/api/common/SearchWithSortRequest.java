package com.sericulture.registration.model.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SearchWithSortRequest {
    @Pattern(regexp = "^[a-zA-Z0-9\\s]*$", message = "Search text must contain only letters and numbers")
    @Schema(name = "searchText", example = "shimoga")
    String searchText;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "Join column must contain only letters and numbers")
    @Schema(name = "joinColumn", example = "district.districtName")
    String joinColumn;

    @Pattern(regexp = "^[a-zA-Z0-9.\\s]*$", message = "Sort Column must contain only letters and numbers")
    @Schema(name = "sortColumn", example = "districtName")
    String sortColumn;

    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Sort order must contain only letters")
    @Schema(name = "sortOrder", example = "asc")
    String sortOrder;

    @Pattern(regexp = "^[0-9]*$", message = "Page number text must contain only numbers")
    @Schema(name = "pageNumber", example = "0")
    String pageNumber;

    @Pattern(regexp = "^[0-9]*$", message = "Page size must contain only numbers")
    @Schema(name = "pageSize", example = "10")
    String pageSize;

    @Pattern(regexp = "^[0-9]*$", message = "Farmer type must contain only numbers")
    @Schema(name = "farmerType", example = "0")
    String farmerType;
}

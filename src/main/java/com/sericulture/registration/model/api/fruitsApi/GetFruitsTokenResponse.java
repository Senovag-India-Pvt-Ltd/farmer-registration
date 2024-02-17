package com.sericulture.registration.model.api.fruitsApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFruitsTokenResponse {
    @Schema(name = "access_token", example = "nGqF0mdEZJ9M5iKsT 53lnizvNqloKVfcFLBUHCtZDXaVANcUeOJsZk3rRsZHCtM9VRV05ASE7WDQmIrDYIJJBNE1n2WdT3_sGEmfo1 rXtRj4bPLJzOaY_gQf2r6-FwkqP0RhLIXaTIPphUXfYIXvEpqZJkVPr-SZd4PriBScX OCwbS800HhbvjBjeH68P")
    String access_token;

    @Schema(name = "token_type", example = "bearer")
    String token_type;

    @Schema(name = "expires_in", example = "599")
    Integer expires_in;

    @Schema(name = "error", example = "0")
    String error;

    @Schema(name = "error_description", example = "Username or password is incorrect")
    String error_description;

}

package com.sericulture.registration.model.api.farmer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sericulture.registration.model.entity.FarmerAddress;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = {JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES})
public class FruitsIdAllowedCounterRequest {
    @Schema(name = "allowedNoOfAttempts", example = "1")
    Long allowedNoOfAttempts;
}
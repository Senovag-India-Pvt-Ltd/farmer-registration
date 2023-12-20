package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ActivateReelerRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "isActivated", example = "0")
    int isActivated;

}
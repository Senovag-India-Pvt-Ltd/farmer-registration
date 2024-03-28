package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ReelerInitialAmountRequest extends RequestBody {
    @Schema(name = "reelerId", example = "1")
    Long reelerId;

    @Schema(name = "initialAmount", example = "15999.67")
    Double initialAmount;

    @Schema(name = "accountNumber", example = "375646767")
    private String accountNumber;

    @Schema(name = "virtualAccount", example = "4367574367")
    private String virtualAccount;

}
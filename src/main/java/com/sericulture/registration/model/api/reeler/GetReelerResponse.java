package com.sericulture.registration.model.api.reeler;

import com.sericulture.registration.model.api.RequestBody;
import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetReelerResponse extends RequestBody {
    @Schema(name = "reelerResponse")
    ReelerResponse reelerResponse;

    @Schema(name = "reelerVirtualBankAccountList")
    List<ReelerVirtualBankAccount> reelerVirtualBankAccountList;
}
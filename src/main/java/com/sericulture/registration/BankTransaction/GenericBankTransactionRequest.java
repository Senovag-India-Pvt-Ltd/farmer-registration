package com.sericulture.registration.BankTransaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sericulture.registration.model.api.RequestBody;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericBankTransactionRequest extends RequestBody {
    @JsonProperty("GenericCorporateAlertRequest")
    private List<GenericCorporateAlertRequest> genericCorporateAlertRequest = new ArrayList<>();
}
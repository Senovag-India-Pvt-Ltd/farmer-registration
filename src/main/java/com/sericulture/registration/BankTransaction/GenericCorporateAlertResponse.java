package com.sericulture.registration.BankTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class GenericCorporateAlertResponse {
    private String errorCode;
    private String errorMessage;
    private String domainReferenceNo;
}

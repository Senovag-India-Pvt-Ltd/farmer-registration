package com.sericulture.registration.BankTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BankResponse implements Serializable {
    GenericCorporateAlertResponse GenericCorporateAlertResponse;
}

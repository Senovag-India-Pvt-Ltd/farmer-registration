package com.sericulture.registration.BankTransaction;

import lombok.Getter;

@Getter
public enum BankResponseMessageEnum {
    SUCCESS("Success"),
    DUPLICATE("Duplicate"),
    REJECT("Technical Reject");

    public final String label;

    BankResponseMessageEnum(String label) {
        this.label = label;
    }
}
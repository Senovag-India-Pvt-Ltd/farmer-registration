package com.sericulture.registration.BankTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sericulture.registration.model.api.RequestBody;
import lombok.*;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericCorporateAlertRequest extends RequestBody {
    /*
    "Alert Sequence No" : "000303100109572403173",
    "Virtual Account": "ZERNSE",
    "Account number": "00321234560198",
    "Debit Credit": "Credit",
    "Amount": "5612.22",
    "Remitter Name": "PRAKASH KUMAR",
    "Remitter Account": "25781212184",
    "Remitter Bank": "Bank of Baroda",
    "Remitter IFSC": "CITI00000241"
    "Cheque No":"",
    "User Reference Number": "FT124542",
    "Mnemonic Code": "NEFT",
    "Value Date": "2017-03-24",
    "Transaction Description": "Test NEFT transaction",
    "Transaction Date": "2017-03-24 08:35"
     */
    @JsonProperty("Alert Sequence No")
    private String alertSequenceNo;
    @JsonProperty("Remitter Name")
    private String remitterName;
    @JsonProperty("Remitter Account")
    private String remitterAccount;
    @JsonProperty("Remitter Bank")
    private String remitterBank;
    @JsonProperty("User Reference Number")
    private String userReferenceNumber;
    @JsonProperty("Virtual Account")
    private String virtualAccount;
    @JsonProperty("Amount")
    private Double amount;
    @JsonProperty("Mnemonic Code")
    private String mnemonicCode;
    @JsonProperty("Transaction Date")
    private String transactionDate;
    @JsonProperty("Value Date")
    private String valueDate;
    @JsonProperty("Remitter IFSC")
    private String ifscCode;
    @JsonProperty("Cheque No")
    private Integer chequeNo;
    @JsonProperty("Transaction Description")
    private String transactionDescription;
    @JsonProperty("Account number")
    private String accountNumber;
    @JsonProperty("Debit Credit")
    private String debitCredit;


}

package com.sericulture.registration.BankTransaction;

public final class BTConstants {

    public static final String SPACE = " ";
    public static final String BANK_CREDIT_TXN_REQUEST ="""
    {"GenericCorporateAlertRequest":
    [
    {
      "Alert Sequence No": "000303100109572403173",
      "Virtual Account": "ZERNSE",
      "Account number": "00321234560198",
      "Debit Credit": "Credit",
      "Amount": "5612.22",
      "Remitter Name": "PRAKASH KUMAR",
      "Remitter Account": "25781212184",
      "Remitter Bank": "Bank of Baroda",
      "Remitter IFSC": "CITI00000241",
      "Cheque No": "",
      "User Reference Number": "FT124542",
      "Mnemonic Code": "NEFT",
      "Value Date": "2017-03-24",
      "Transaction Description": "Test NEFT transaction",
      "Transaction Date": "2017-03-24 08:35"
    }
    ]
    }
""";

    public static final String[] csvColumns = new String[]
            {       "transactionType",
                    "beneficiaryCode",
                    "beneficiaryAccountNumber",
                    "instrumentAmount",
                    "beneficiaryName",
                    "draweeLocation",
                    "printLocation",
                    "beneficiaryAddress1",
                    "beneficiaryAddress2",
                    "beneficiaryAddress3",
                    "beneficiaryAddress4",
                    "beneficiaryAddress5",
                    "instrumentReferenceNumber",
                    "customerReferenceNumber",
                    "paymentDetails1",
                    "paymentDetails2",
                    "paymentDetails3",
                    "paymentDetails4",
                    "paymentDetails5",
                    "paymentDetails6",
                    "paymentDetails7",
                    "chequeNumber",
                    "chequeDate",
                    "micrNumber",
                    "ifscCode",
                    "beneficiaryBankName",
                    "beneficiaryBankBranchName",
                    "beneficiaryEmailId"};


    public static final String[] csvColumnsResponse = new String[]
            {"transactionType",
                    "beneficiaryCode",
                    "beneficiaryName",
                    "instrumentAmount",
                    "chequeNumber",
                    "chequeDate",
                    "customerReferenceNumber",
                    "paymentDetails1",
                    "paymentDetails2",
                    "beneficiaryAccountNumber",
                    "bankRefNumber",
                    "transactionStatus",
                    "rejectReason",
                    "ifscCode",
                    "micrNumber",
                    "utrNumber"
            };
}

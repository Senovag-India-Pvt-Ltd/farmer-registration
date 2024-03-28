package com.sericulture.registration.BankTransaction;

import com.sericulture.registration.model.mapper.Mapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Component
@Slf4j
public class BankTransactionService {
    @Autowired
    AuditTransactionRepository auditTransactionRepository;

    @Autowired
    ReelerVidCreditTxnRepository reelerVidCreditTxnRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    EntityManager entityManager;

    @Transactional
    public long saveAudit(String header, String body) {
        AuditTransaction at = new AuditTransaction();
        at.setRequestHeaders(header);
        at.setRequestBody(body);
        auditTransactionRepository.save(at);
        return at.getId().longValue();
    }
    @Transactional
    public long updateAudit(String body, Long id) {
        AuditTransaction at = auditTransactionRepository.findById(id);
        at.setResponseBody(body);
        auditTransactionRepository.save(at);
        return at.getId().longValue();
    }
    @Transactional
    public BankResponse saveTransaction(GenericBankTransactionRequest genericBankTransactionRequest) {
        List<GenericCorporateAlertRequest> genericCorporateAlertRequests =
                genericBankTransactionRequest.getGenericCorporateAlertRequest();
        List<ReelerVidCreditTxn> reelerVidCreditTxns = new ArrayList<>();
        for (GenericCorporateAlertRequest genericCorporateAlertRequest : genericCorporateAlertRequests) {
            ReelerVidCreditTxn reelerVidCreditTxn = mapper.bankCreditRequestToEntity(genericCorporateAlertRequest);
            reelerVidCreditTxns.add(reelerVidCreditTxn);
        }
        try {
            reelerVidCreditTxnRepository.saveAll(reelerVidCreditTxns);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("UC_ALERT_SEQUENCE_NO") ){
                GenericCorporateAlertResponse gen = new GenericCorporateAlertResponse("0",
                        BankResponseMessageEnum.DUPLICATE.getLabel(),
                        getValidAlertSequenceNumber(genericCorporateAlertRequests));
                return new BankResponse(gen);
            } else {
                throw e;
            }
        }
        GenericCorporateAlertResponse gen = new GenericCorporateAlertResponse("0",
                BankResponseMessageEnum.SUCCESS.getLabel(),
                getValidAlertSequenceNumber(genericCorporateAlertRequests));


        return  new BankResponse(gen);
    }
    @Transactional
    private static String getValidAlertSequenceNumber(List<GenericCorporateAlertRequest> genericCorporateAlertRequests) {
        if (genericCorporateAlertRequests.size() > 0) {
            return genericCorporateAlertRequests.get(0).getAlertSequenceNo().toString();
        }
        throw new IllegalStateException("No valid alert sequence found");
    }
}

package com.sericulture.registration.BankTransaction;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ReelerVidCreditTxnRepository extends CrudRepository<ReelerVidCreditTxn, Integer> {


    public ReelerVidCreditTxn save(ReelerVidCreditTxn at);

}
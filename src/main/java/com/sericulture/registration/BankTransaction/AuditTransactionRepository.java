package com.sericulture.registration.BankTransaction;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditTransactionRepository extends PagingAndSortingRepository<AuditTransaction, Integer> {


    public AuditTransaction save(AuditTransaction at);

    public AuditTransaction findById(Long id);

}
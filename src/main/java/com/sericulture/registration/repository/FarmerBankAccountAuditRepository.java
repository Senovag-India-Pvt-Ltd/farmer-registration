package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerBankAccount;
import com.sericulture.registration.model.entity.FarmerBankAccountAudit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FarmerBankAccountAuditRepository extends PagingAndSortingRepository<FarmerBankAccountAudit, Long> {

    public FarmerBankAccountAudit save(FarmerBankAccountAudit farmerBankAccountAudit);
}

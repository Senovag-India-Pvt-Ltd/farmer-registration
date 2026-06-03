package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerVirtualBankAccountAudit;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerVirtualBankAccountAuditRepository extends PagingAndSortingRepository<FarmerVirtualBankAccountAudit, Long> {

    FarmerVirtualBankAccountAudit save(FarmerVirtualBankAccountAudit farmerVirtualBankAccountAudit);

    List<FarmerVirtualBankAccountAudit> findByFarmerVirtualBankAccountId(Long farmerVirtualBankAccountId);
}
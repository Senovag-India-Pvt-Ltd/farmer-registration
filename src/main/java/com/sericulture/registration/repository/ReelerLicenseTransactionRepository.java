package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.ReelerLicenseTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReelerLicenseTransactionRepository extends PagingAndSortingRepository<ReelerLicenseTransaction, Long> {

    public Page<ReelerLicenseTransaction> findByActiveOrderByReelerLicenseTransactionIdAsc(boolean isActive, final Pageable pageable);

    public ReelerLicenseTransaction save(ReelerLicenseTransaction farmerBankAccount);

    public ReelerLicenseTransaction findByReelerLicenseTransactionIdAndActive(long id, boolean isActive);

    public ReelerLicenseTransaction findByReelerLicenseTransactionIdAndActiveIn(@Param("farmerBankAccountId") long farmerBankAccountId, @Param("active") Set<Boolean> active);
}
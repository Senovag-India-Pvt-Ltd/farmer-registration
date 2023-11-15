package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface FarmerBankAccountRepository extends PagingAndSortingRepository<FarmerBankAccount, Long> {
    public List<FarmerBankAccount> findByFarmerBankAccountNumber(String farmerBankAccountNumber);

    public FarmerBankAccount findByFarmerBankAccountNumberAndActive(String farmerBankAccountNumber,boolean isActive);

    public Page<FarmerBankAccount> findByActiveOrderByFarmerBankAccountIdAsc(boolean isActive, final Pageable pageable);

    public FarmerBankAccount save(FarmerBankAccount farmerBankAccount);

    public FarmerBankAccount findByFarmerBankAccountIdAndActive(long id, boolean isActive);

    public FarmerBankAccount findByFarmerBankAccountIdAndActiveIn(@Param("farmerBankAccountId") long farmerBankAccountId, @Param("active") Set<Boolean> active);

    public FarmerBankAccount findByFarmerIdAndActive(long farmerId, boolean isActive);
}
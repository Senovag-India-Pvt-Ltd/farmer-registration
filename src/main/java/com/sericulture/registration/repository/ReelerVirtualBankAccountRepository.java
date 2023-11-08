package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReelerVirtualBankAccountRepository extends PagingAndSortingRepository<ReelerVirtualBankAccount, Long> {
    public Page<ReelerVirtualBankAccount> findByActiveOrderByReelerVirtualBankAccountIdAsc(boolean isActive, final Pageable pageable);

    public ReelerVirtualBankAccount save(ReelerVirtualBankAccount ReelerVirtualBankAccount);

    public ReelerVirtualBankAccount findByReelerVirtualBankAccountIdAndActive(long id, boolean isActive);

    public ReelerVirtualBankAccount findByReelerVirtualBankAccountIdAndActiveIn(@Param("reelerVirtualBankAccountId") long ReelerVirtualBankAccountId, @Param("active") Set<Boolean> active);
}
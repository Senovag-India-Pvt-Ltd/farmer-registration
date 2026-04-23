package com.sericulture.registration.repository;


import com.sericulture.registration.model.entity.EuVirtualBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface EuVirtualBankAccountRepository
        extends JpaRepository<EuVirtualBankAccount, Long> {

   public List<EuVirtualBankAccount> findByVirtualAccountNumber(String virtualAccountNumber);


   public List<EuVirtualBankAccount> findByVirtualAccountNumberAndEuIdNot(String virtualAccountNumber, Long euId);

    public List<EuVirtualBankAccount> findByEuId(Long euId);
     public boolean existsByEuIdAndLockTrue(Long euId);

    List<EuVirtualBankAccount> findByEuIdAndActiveTrue(Long euId);



}
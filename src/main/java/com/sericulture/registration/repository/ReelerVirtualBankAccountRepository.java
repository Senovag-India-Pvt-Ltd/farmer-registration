package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReelerVirtualBankAccountRepository extends PagingAndSortingRepository<ReelerVirtualBankAccount, Long> {
    public Page<ReelerVirtualBankAccount> findByActiveOrderByReelerVirtualBankAccountIdAsc(boolean isActive, final Pageable pageable);

    public ReelerVirtualBankAccount save(ReelerVirtualBankAccount ReelerVirtualBankAccount);

    public List<ReelerVirtualBankAccount> findByVirtualAccountNumber(String virtualAccountNumber);

    public List<ReelerVirtualBankAccount> findByVirtualAccountNumberAndActiveAndReelerVirtualBankAccountIdIsNot(String virtualAccountNumber, boolean isActive, long id);

    public ReelerVirtualBankAccount findByReelerVirtualBankAccountIdAndActive(long id, boolean isActive);

    public ReelerVirtualBankAccount findByReelerVirtualBankAccountIdAndActiveIn(@Param("reelerVirtualBankAccountId") long ReelerVirtualBankAccountId, @Param("active") Set<Boolean> active);

    public List<ReelerVirtualBankAccount> findByReelerIdAndActive(long reelerId, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.reelerVirtualBankAccountId = :id")
    public ReelerVirtualBankAccountDTO getByReelerVirtualBankAccountIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.reelerId = :id")
    public List <ReelerVirtualBankAccountDTO> getByReelerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.marketMasterId = :marketId order by reeler.reelerName ASC")
    public List<ReelerVirtualBankAccountDTO> getByReelersByMarketId(@Param("marketId") long marketId, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.marketMasterId = :marketId AND reeler.reelerNumber = :reelerNumber")
    public ReelerVirtualBankAccountDTO getByReelerByMarketIdAndReelerNumber(@Param("marketId") long marketId, @Param("reelerNumber") String reelerNumber, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.marketMasterId = :marketId AND reeler.reelingLicenseNumber = :reelingLicenseNumber")
    public ReelerVirtualBankAccountDTO getByReelerByMarketIdAndReelingLicenseNumber(@Param("marketId") long marketId, @Param("reelingLicenseNumber") String reelingLicenseNumber, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO(" +
            " reelerVirtualBankAccount.reelerVirtualBankAccountId, " +
            " reelerVirtualBankAccount.reelerId, " +
            " reelerVirtualBankAccount.virtualAccountNumber, " +
            " reelerVirtualBankAccount.branchName, " +
            " reelerVirtualBankAccount.ifscCode, " +
            " reelerVirtualBankAccount.marketMasterId, " +
            " reeler.reelerName, " +
            " marketMaster.marketMasterName" +
            ") " +
            "from ReelerVirtualBankAccount reelerVirtualBankAccount " +
            "left join market_master marketMaster " +
            "on reelerVirtualBankAccount.marketMasterId = marketMaster.marketMasterId " +
            "left join Reeler reeler " +
            "on reelerVirtualBankAccount.reelerId = reeler.reelerId " +
            "where reelerVirtualBankAccount.active = :isActive AND reelerVirtualBankAccount.marketMasterId = :marketId AND reeler.mobileNumber = :mobileNumber")
    public ReelerVirtualBankAccountDTO getByReelerByMarketIdAndMobileNumber(@Param("marketId") long marketId, @Param("mobileNumber") String mobileNumber, @Param("isActive") boolean isActive);
}
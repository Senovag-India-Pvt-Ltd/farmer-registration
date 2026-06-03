package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerVirtualBankAccountView;
import com.sericulture.registration.model.entity.FarmerVirtualBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerVirtualBankAccountRepository extends PagingAndSortingRepository<FarmerVirtualBankAccount, Long> {

    Page<FarmerVirtualBankAccount> findByActiveOrderByFarmerVirtualBankAccountIdAsc(boolean isActive, Pageable pageable);

    FarmerVirtualBankAccount save(FarmerVirtualBankAccount farmerVirtualBankAccount);

    List<FarmerVirtualBankAccount> findByVirtualAccountNumber(String virtualAccountNumber);

    List<FarmerVirtualBankAccount> findByVirtualAccountNumberAndActiveAndFarmerVirtualBankAccountIdIsNot(String virtualAccountNumber, boolean isActive, long id);

    FarmerVirtualBankAccount findByFarmerVirtualBankAccountIdAndActive(long id, boolean isActive);

    FarmerVirtualBankAccount findByFarmerVirtualBankAccountIdAndActiveIn(@Param("farmerVirtualBankAccountId") long farmerVirtualBankAccountId, @Param("active") Set<Boolean> active);

    List<FarmerVirtualBankAccount> findByFarmerIdAndActive(long farmerId, boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.farmer_virtual_bank_account_id = :id",
            nativeQuery = true)
    FarmerVirtualBankAccountView getByFarmerVirtualBankAccountIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.farmer_id = :id",
            nativeQuery = true)
    List<FarmerVirtualBankAccountView> getByFarmerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.market_master_id = :marketId " +
            "ORDER BY f.first_name ASC",
            nativeQuery = true)
    List<FarmerVirtualBankAccountView> getByFarmersByMarketId(@Param("marketId") long marketId, @Param("isActive") boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.market_master_id = :marketId AND f.farmer_number = :farmerNumber",
            nativeQuery = true)
    FarmerVirtualBankAccountView getByFarmerByMarketIdAndFarmerNumber(@Param("marketId") long marketId, @Param("farmerNumber") String farmerNumber, @Param("isActive") boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.market_master_id = :marketId AND f.fruits_id = :fruitsId",
            nativeQuery = true)
    FarmerVirtualBankAccountView getByFarmerByMarketIdAndFruitsId(@Param("marketId") long marketId, @Param("fruitsId") String fruitsId, @Param("isActive") boolean isActive);

    @Query(value = "SELECT fvba.farmer_virtual_bank_account_id, fvba.farmer_id, " +
            "fvba.virtual_account_number, fvba.branch_name, fvba.ifsc_code, " +
            "fvba.market_master_id, fvba.is_locked, f.first_name, f.fruits_id, " +
            "f.farmer_number, f.mobile_number, mm.market_master_name " +
            "FROM farmer_virtual_bank_account fvba " +
            "LEFT JOIN farmer f ON fvba.farmer_id = f.farmer_id " +
            "LEFT JOIN market_master mm ON fvba.market_master_id = mm.market_master_id " +
            "WHERE fvba.active = :isActive AND fvba.market_master_id = :marketId AND f.mobile_number = :mobileNumber",
            nativeQuery = true)
    FarmerVirtualBankAccountView getByFarmerByMarketIdAndMobileNumber(@Param("marketId") long marketId, @Param("mobileNumber") String mobileNumber, @Param("isActive") boolean isActive);
}
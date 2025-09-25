package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.Taluk;
import com.sericulture.registration.model.entity.TraderLicense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface TraderLicenseRepository extends PagingAndSortingRepository<TraderLicense, Long> {
    public Page<TraderLicense> findByActiveOrderByTraderLicenseIdAsc(boolean isActive, final Pageable pageable);

    public TraderLicense save(TraderLicense farmerAddress);

    public TraderLicense findByTraderLicenseIdAndActive(long id, boolean isActive);

    public List<TraderLicense> findByTraderTypeMasterIdAndTraderLicenseNumberAndLicenseChallanNumberAndActive(long traderTypeMasterId, String traderLicenseNumber, String licenseChallanNumber, boolean isActive);

    public TraderLicense findByTraderLicenseIdAndActiveIn(@Param("traderLicenseId") long traderLicenseId, @Param("active") Set<Boolean> active);

    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive " +
            "ORDER BY traderLicense.firstName ASC"
    )
    Page<TraderLicenseDTO> getByActiveOrderByTraderLicenseIdAsc(@Param("isActive") boolean isActive, final Pageable pageable);


    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") " +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state " +
            "   on traderLicense.stateId = state.stateId " +
            "left join District district " +
            "   on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive " +
            "  and (:districtId is null or traderLicense.districtId = :districtId) " +
            "  and (:silkType is null or traderLicense.silkType = :silkType) " +
            "  and (:traderTypeMasterId is null or traderLicense.traderTypeMasterId = :traderTypeMasterId) " +
            "order by traderLicense.firstName asc"
    )
    Page<TraderLicenseDTO> getByActiveAndFilters(
            @Param("isActive") boolean isActive,
            @Param("districtId") Long districtId,
            @Param("silkType") String silkType,
            @Param("traderTypeMasterId") Long traderTypeMasterId,
            Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.traderLicenseId = :id "
    )
    public TraderLicenseDTO getByTraderLicenseIdAndActive(long id, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.traderLicenseNumber = :id "
    )
    public TraderLicenseDTO getByTraderLicenseNumberAndActive(@Param("id") String id, @Param("isActive") boolean isActive);


    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND " +
            "(:joinColumn = 'traderTypeMaster.traderTypeMasterName' AND traderTypeMaster.traderTypeMasterName LIKE :searchText) OR " +
            "(:joinColumn = 'traderLicense.arnNumber' AND traderLicense.arnNumber LIKE :searchText) OR " +
            "(:joinColumn = 'traderLicense.firstName' AND traderLicense.firstName LIKE :searchText)"
    )
    public Page<TraderLicenseDTO> getSortedTraderLicenses(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.marketMasterId = :marketId AND traderLicense.traderLicenseNumber = :traderLicenseNumber")
    public TraderLicenseDTO getByTraderLicenseByMarketIdAndTraderLicenseNumber(@Param("marketId") long marketId, @Param("traderLicenseNumber") String reelingLicenseNumber, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.marketMasterId = :marketId AND traderLicense.mobileNumber = :mobileNumber")
    public TraderLicenseDTO getByTraderLicenseByMarketIdAndMobileNumber(@Param("marketId") long marketId, @Param("mobileNumber") String mobileNumber, @Param("isActive") boolean isActive);


    @Query("select new com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO(" +
            " traderLicense.traderLicenseId," +
            " traderLicense.arnNumber," +
            " traderLicense.traderTypeMasterId," +
            " traderLicense.firstName," +
            " traderLicense.middleName," +
            " traderLicense.lastName," +
            " traderLicense.fatherName," +
            " traderLicense.stateId," +
            " traderLicense.districtId," +
            " traderLicense.address," +
            " traderLicense.premisesDescription," +
            " traderLicense.applicationDate," +
            " traderLicense.applicationNumber," +
            " traderLicense.traderLicenseNumber," +
            " traderLicense.representativeDetails," +
            " traderLicense.licenseFee," +
            " traderLicense.silkType," +
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " marketMaster.marketMasterName," +
            " traderLicense.marketMasterId," +
            " traderLicense.walletAmount," +
            " traderLicense.mobileNumber," +
            " traderLicense.virtualAccountNumber," +
            " traderLicense.ifscCode," +
            " traderLicense.branchName," +
            " traderLicense.gstNumber," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join market_master marketMaster\n" +
            "on traderLicense.marketMasterId = marketMaster.marketMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.marketMasterId = :marketId order by traderLicense.firstName ASC")
    public List<TraderLicenseDTO> getByTradersByMarketId(@Param("marketId") long marketId, @Param("isActive") boolean isActive);

    @Query(value = """
            SELECT
                tl.trader_license_id,
                tl.arn_number,
                tl.trader_type_id,
                tt.trader_type_name,
                tt.trader_type_name_in_kannada,
                tt.no_of_device_allowed,
                tl.first_name,
                tl.middle_name,
                tl.last_name,
                tl.father_name,
                tl.state_id,
                tl.district_id,
                d.district_name,
                d.district_name_in_kannada,
                d.region,
                d.code,
                tl.address,
                tl.premises_description,
                tl.application_date,
                tl.application_number,
                tl.trader_license_number,
                tl.representative_details,
                tl.license_fee,
                tl.license_challan_number,
                tl.godown_details,
                tl.silk_exchange_mahajar,
                tl.license_number_sequence,
                tl.silk_type,
                tl.market_master_id,
                tl.wallet_amount,
                tl.mobile_number,
                tl.is_activated,
                tl.virtual_account_number,
                tl.branch_name,
                tl.ifsc_code,
                tl.active,
                tl.created_by,
                tl.created_date,
                tl.modified_by,
                tl.modified_date
            FROM trader_license tl
            LEFT JOIN trader_type_master tt ON tl.trader_type_id = tt.trader_type_id
            LEFT JOIN district d ON tl.district_id = d.district_id
            """, nativeQuery = true)
    List<Map<String, Object>> getFullTraderLicenseDetails();
}

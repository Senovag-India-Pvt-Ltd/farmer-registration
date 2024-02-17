package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.TraderLicense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TraderLicenseRepository extends PagingAndSortingRepository<TraderLicense, Long> {
    public Page<TraderLicense> findByActiveOrderByTraderLicenseIdAsc(boolean isActive, final Pageable pageable);

    public TraderLicense save(TraderLicense farmerAddress);

    public TraderLicense findByTraderLicenseIdAndActive(long id, boolean isActive);

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
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
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
            " traderLicense.licenseChallanNumber," +
            " traderLicense.godownDetails," +
            " traderLicense.silkExchangeMahajar," +
            " traderLicense.licenseNumberSequence," +
            " traderTypeMaster.traderTypeMasterName," +
            " state.stateName," +
            " district.districtName" +
            ") \n" +
            "from TraderLicense traderLicense\n" +
            "left join trader_type_master traderTypeMaster\n" +
            "on traderLicense.traderTypeMasterId = traderTypeMaster.traderTypeMasterId " +
            "left join State state\n" +
            "on traderLicense.stateId = state.stateId " +
            "left join District district\n" +
            "on traderLicense.districtId = district.districtId " +
            "where traderLicense.active = :isActive AND traderLicense.traderLicenseId = :id "
    )
    public TraderLicenseDTO getByTraderLicenseIdAndActive(long id, boolean isActive);
}

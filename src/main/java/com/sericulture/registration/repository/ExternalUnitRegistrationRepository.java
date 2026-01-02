package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.ExternalUnitRegistration;
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
public interface ExternalUnitRegistrationRepository extends PagingAndSortingRepository<ExternalUnitRegistration, Long> {
    public Page<ExternalUnitRegistration> findByActiveOrderByExternalUnitRegistrationIdAsc(boolean isActive, final Pageable pageable);

    public List<ExternalUnitRegistration> findByActiveOrderByNameAsc(boolean isActive);

    public ExternalUnitRegistration save(ExternalUnitRegistration farmerAddress);

    public ExternalUnitRegistration findByExternalUnitRegistrationIdAndActive(long id, boolean isActive);

    List<ExternalUnitRegistration> findByExternalUnitTypeIdAndActive(long externalUnitTypeId, boolean active);

    public ExternalUnitRegistration findByExternalUnitRegistrationIdAndActiveIn(@Param("externalUnitRegistrationId") long externalUnitRegistrationId, @Param("active") Set<Boolean> active);

    @Query("select new com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO(" +
            " externalUnitRegistration.externalUnitRegistrationId," +
            " externalUnitRegistration.externalUnitTypeId," +
            " externalUnitRegistration.name," +
            " externalUnitRegistration.address," +
            " externalUnitRegistration.licenseNumber," +
            " externalUnitRegistration.externalUnitNumber," +
            " externalUnitRegistration.organisationName," +
            " externalUnitRegistration.raceMasterId," +
            " externalUnitRegistration.capacity," +
            " externalUnitType.externalUnitTypeName," +
            " externalUnitRegistration.virtualAccountNumber," +
            " externalUnitRegistration.ifscCode," +
            " externalUnitRegistration.branchName," +
            " marketMaster.marketMasterName," +
            " externalUnitRegistration.lotNumberNomenclature," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
            "left join market_master marketMaster\n" +
            "on externalUnitRegistration.marketMasterId = marketMaster.marketMasterId " +
            "where externalUnitRegistration.active = :isActive " +
            "ORDER BY externalUnitRegistration.name ASC"
    )
    Page<ExternalUnitRegistrationDTO> getByActiveOrderByExternalUnitRegistrationIdAsc(@Param("isActive") boolean isActive, final Pageable pageable);


    @Query("select new com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO(" +
            " externalUnitRegistration.externalUnitRegistrationId," +
            " externalUnitRegistration.externalUnitTypeId," +
            " externalUnitRegistration.name," +
            " externalUnitRegistration.address," +
            " externalUnitRegistration.licenseNumber," +
            " externalUnitRegistration.externalUnitNumber," +
            " externalUnitRegistration.organisationName," +
            " externalUnitRegistration.raceMasterId," +
            " externalUnitRegistration.capacity," +
            " externalUnitType.externalUnitTypeName," +
            " externalUnitRegistration.virtualAccountNumber," +
            " externalUnitRegistration.ifscCode," +
            " externalUnitRegistration.branchName," +
            " marketMaster.marketMasterName," +
            " externalUnitRegistration.lotNumberNomenclature," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
            "left join market_master marketMaster\n" +
            "on externalUnitRegistration.marketMasterId = marketMaster.marketMasterId " +
            "where externalUnitRegistration.active = :isActive " +
            "  and (:raceMasterId is null or externalUnitRegistration.raceMasterId = :raceMasterId) " +
            "  and (:externalUnitTypeId is null or externalUnitRegistration.externalUnitTypeId = :externalUnitTypeId) " +
            "ORDER BY externalUnitRegistration.name ASC"
    )
    Page<ExternalUnitRegistrationDTO> getByActiveAndFilters(
            @Param("isActive") boolean isActive,
            @Param("raceMasterId") Long raceMasterId,
            @Param("externalUnitTypeId") Long externalUnitTypeId,
            Pageable pageable);


    @Query("select new com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO(" +
            " externalUnitRegistration.externalUnitRegistrationId," +
            " externalUnitRegistration.externalUnitTypeId," +
            " externalUnitRegistration.name," +
            " externalUnitRegistration.address," +
            " externalUnitRegistration.licenseNumber," +
            " externalUnitRegistration.externalUnitNumber," +
            " externalUnitRegistration.organisationName," +
            " externalUnitRegistration.raceMasterId," +
            " externalUnitRegistration.capacity," +
            " externalUnitType.externalUnitTypeName," +
            " externalUnitRegistration.virtualAccountNumber," +
            " externalUnitRegistration.ifscCode," +
            " externalUnitRegistration.branchName," +
            " marketMaster.marketMasterName," +
            " externalUnitRegistration.lotNumberNomenclature," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
            "left join market_master marketMaster\n" +
            "on externalUnitRegistration.marketMasterId = marketMaster.marketMasterId " +
            "where externalUnitRegistration.active = :isActive AND externalUnitRegistration.externalUnitRegistrationId = :id "
    )
    public ExternalUnitRegistrationDTO getByExternalUnitRegistrationIdAndActive(long id, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO(" +
            " externalUnitRegistration.externalUnitRegistrationId," +
            " externalUnitRegistration.externalUnitTypeId," +
            " externalUnitRegistration.name," +
            " externalUnitRegistration.address," +
            " externalUnitRegistration.licenseNumber," +
            " externalUnitRegistration.externalUnitNumber," +
            " externalUnitRegistration.organisationName," +
            " externalUnitRegistration.raceMasterId," +
            " externalUnitRegistration.capacity," +
            " externalUnitType.externalUnitTypeName," +
            " externalUnitRegistration.virtualAccountNumber," +
            " externalUnitRegistration.ifscCode," +
            " externalUnitRegistration.branchName," +
            " marketMaster.marketMasterName," +
            " externalUnitRegistration.lotNumberNomenclature," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
            "left join market_master marketMaster\n" +
            "on externalUnitRegistration.marketMasterId = marketMaster.marketMasterId " +
            "where externalUnitRegistration.active = :isActive AND " +
            "(:joinColumn = 'externalUnitType.externalUnitTypeName' AND externalUnitType.externalUnitTypeName LIKE :searchText) OR " +
            "(:joinColumn = 'externalUnitRegistration.licenseNumber' AND externalUnitRegistration.licenseNumber LIKE :searchText) OR " +
            "(:joinColumn = 'externalUnitRegistration.organisationName' AND externalUnitRegistration.organisationName LIKE :searchText)"
    )
    public Page<ExternalUnitRegistrationDTO> getSortedExternalUnitRegistration(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

    @Query(value = """
            SELECT
                    eur.external_unit_registration_id,
                    eur.external_unit_type_id,
                    eut.external_unit_type_name,
                    eut.external_unit_type_name_in_kannada,
            		eur.name,
                    eur.address,
                    eur.license_number,
                    eur.external_unit_number,
                    eur.organisation_name,
                    eur.race_id,
                    rm.race_name,
                    rm.race_name_in_kannada,
                    eur.capacity,
                    eur.user_master_id,
                    eur.virtual_account_number,
                    eur.branch_name,
                    eur.ifsc_code,
                    eur.lot_number_nomenclature,
                    eur.market_master_id,
            		mm.market_name,
            		mm.market_name_in_kannada,
                    eur.active,
                    eur.created_by,
                    eur.created_date,
                    eur.modified_by,
                    eur.modified_date
                FROM external_unit_registration eur
                LEFT JOIN external_unit_type_master eut
                    ON eur.external_unit_type_id = eut.external_unit_type_id
                LEFT JOIN race_master rm ON rm.race_id = eur.race_id
                LEFT JOIN market_master mm ON mm.market_master_id = eur.market_master_id
            """, nativeQuery = true)
    List<Map<String, Object>> getFullExternalUnitDetails();

}
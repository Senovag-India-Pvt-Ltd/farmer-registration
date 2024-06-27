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
import java.util.Set;

@Repository
public interface ExternalUnitRegistrationRepository extends PagingAndSortingRepository<ExternalUnitRegistration, Long> {
    public Page<ExternalUnitRegistration> findByActiveOrderByExternalUnitRegistrationIdAsc(boolean isActive, final Pageable pageable);

    public List<ExternalUnitRegistration> findByActiveOrderByExternalUnitRegistrationIdAsc(boolean isActive);

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
            " externalUnitType.externalUnitTypeName," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
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
            " externalUnitType.externalUnitTypeName," +
            " raceMaster.raceMasterName" +
            ") \n" +
            "from ExternalUnitRegistration externalUnitRegistration\n" +
            "left join external_unit_type_master externalUnitType\n" +
            "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
            "left join RaceMaster raceMaster\n" +
            "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
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
        " externalUnitType.externalUnitTypeName," +
        " raceMaster.raceMasterName" +
        ") \n" +
        "from ExternalUnitRegistration externalUnitRegistration\n" +
        "left join external_unit_type_master externalUnitType\n" +
        "on externalUnitRegistration.externalUnitTypeId = externalUnitType.externalUnitTypeId " +
        "left join RaceMaster raceMaster\n" +
        "on externalUnitRegistration.raceMasterId = raceMaster.raceMasterId " +
        "where externalUnitRegistration.active = :isActive AND " +
        "(:joinColumn = 'externalUnitType.externalUnitTypeName' AND externalUnitType.externalUnitTypeName LIKE :searchText) OR " +
        "(:joinColumn = 'externalUnitRegistration.licenseNumber' AND externalUnitRegistration.licenseNumber LIKE :searchText) OR " +
        "(:joinColumn = 'externalUnitRegistration.organisationName' AND externalUnitRegistration.organisationName LIKE :searchText)"
)
public Page<ExternalUnitRegistrationDTO> getSortedExternalUnitRegistration(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

}
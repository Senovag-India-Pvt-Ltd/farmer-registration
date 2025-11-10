package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.entity.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FarmerRepository extends PagingAndSortingRepository<Farmer, Long> {
    public List<Farmer> findByFarmerNumber(String farmerNumber);

    public List<Farmer> findByMobileNumber(String mobileNumber);

    public Farmer findByFarmerNumberAndActive(String farmerNumber, boolean isActive);

    public Page<Farmer> findByActiveOrderByFarmerIdAsc(boolean isActive, final Pageable pageable);

    public Farmer save(Farmer farmer);

    Optional<Farmer> findByFarmerId(long farmerId);

    public Farmer findByFarmerIdAndActive(long id, boolean isActive);

    public Farmer findByFarmerIdAndActiveIn(@Param("farmerId") long farmerId, @Param("active") Set<Boolean> active);

    public Farmer findByFruitsIdAndActive(String fruitsid, boolean isActive);

    public Farmer findByMobileNumberAndActive(String mobileNumber, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive AND farmer.farmerId = :id")
    public FarmerDTO getByFarmerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAsc(@Param("isActive") boolean isActive, final Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) OR" +
            "(:joinColumn = 'farmerBankAccount.farmerBankAccountNumber' AND farmerBankAccount.farmerBankAccountNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAsc(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive and farmer.isOtherStateFarmer = true and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) OR" +
            "(:joinColumn = 'farmerBankAccount.farmerBankAccountNumber' AND farmerBankAccount.farmerBankAccountNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForNonKAFarmers(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive and (farmer.isOtherStateFarmer = false or farmer.isOtherStateFarmer is null) and (farmer.fruitsId != '' and farmer.fruitsId is not null) and  " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) OR" +
            "(:joinColumn = 'farmerBankAccount.farmerBankAccountNumber' AND farmerBankAccount.farmerBankAccountNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive and (farmer.isOtherStateFarmer = false or farmer.isOtherStateFarmer is NULL) and (farmer.fruitsId = '' or farmer.fruitsId is null) and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) OR" +
            "(:joinColumn = 'farmerBankAccount.farmerBankAccountNumber' AND farmerBankAccount.farmerBankAccountNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsId(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);


//    @Query("SELECT new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
//            " farmer.farmerId, " +
//            " farmer.farmerNumber, " +
//            " farmer.fruitsId, " +
//            " farmer.firstName, " +
//            " farmer.middleName, " +
//            " farmer.lastName, " +
//            " farmer.dob, " +
//            " farmer.genderId, " +
//            " farmer.casteId, " +
//            " farmer.tscMasterId, " +
//            " farmer.assignToInspectId, " +
//            " farmer.differentlyAbled, " +
//            " farmer.email, " +
//            " farmer.mobileNumber, " +
//            " farmer.epicNumber, " +
//            " farmer.rationCardNumber, " +
//            " farmer.totalLandHolding, " +
//            " farmer.passbookNumber, " +
//            " farmer.landCategoryId, " +
//            " farmer.educationId, " +
//            " farmer.representativeId, " +
//            " farmer.khazaneRecipientId, " +
//            " farmer.photoPath, " +
//            " farmer.farmerTypeId, " +
//            " farmer.minority, " +
//            " farmer.rdNumber, " +
//            " farmer.casteStatus, " +
//            " farmer.genderStatus, " +
//            " farmer.fatherNameKan, " +
//            " farmer.fatherName, " +
//            " farmer.nameKan, " +
//            " caste.title, " +
//            " landCategory.landCategoryName, " +
//            " farmerType.farmerTypeName, " +
//            " tscMaster.name, " +
//            " education.name, " +
//            " farmer.isOtherStateFarmer, " +
//            " farmerBankAccount.farmerBankAccountNumber, " +
//            " userMaster.username, " +
//            " farmer.aadhaarNumber" +
//            ") " +
//            "FROM Farmer farmer " +
//            "LEFT JOIN Caste caste ON farmer.casteId = caste.casteId " +
//            "LEFT JOIN LandCategory landCategory ON farmer.landCategoryId = landCategory.landCategoryId " +
//            "LEFT JOIN FarmerType farmerType ON farmer.farmerTypeId = farmerType.farmerTypeId " +
//            "LEFT JOIN TscMaster tscMaster ON farmer.tscMasterId = tscMaster.tscMasterId " +
//            "LEFT JOIN Education education ON farmer.educationId = education.educationId " +
//            "LEFT JOIN FarmerBankAccount farmerBankAccount ON farmer.farmerId = farmerBankAccount.farmerId " +
//            "LEFT JOIN UserMaster userMaster ON farmer.assignToInspectId = userMaster.userMasterId " +
//            "LEFT JOIN FarmerAddress fa ON fa.farmerId = farmer.farmerId " +
//            "WHERE farmer.active = :isActive " +
//            "AND (farmer.isOtherStateFarmer = false OR farmer.isOtherStateFarmer IS NULL) " +
//            "AND (farmer.fruitsId = '' OR farmer.fruitsId IS NULL) " +
//            "AND (:districtId IS NULL OR fa.districtId = :districtId) " +
//            "AND (:talukId IS NULL OR fa.talukId = :talukId) " +
//            "AND (:hobliId IS NULL OR fa.hobliId = :hobliId) " +
//            "ORDER BY farmer.farmerNumber ASC")
//    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsIds(
//            @Param("isActive") boolean isActive,
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("hobliId") Long hobliId,
//            Pageable pageable);
//
//
//    @Query("SELECT new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
//            " farmer.farmerId, " +
//            " farmer.farmerNumber, " +
//            " farmer.fruitsId, " +
//            " farmer.firstName, " +
//            " farmer.middleName, " +
//            " farmer.lastName, " +
//            " farmer.dob, " +
//            " farmer.genderId, " +
//            " farmer.casteId, " +
//            " farmer.tscMasterId, " +
//            " farmer.assignToInspectId, " +
//            " farmer.differentlyAbled, " +
//            " farmer.email, " +
//            " farmer.mobileNumber, " +
//            " farmer.epicNumber, " +
//            " farmer.rationCardNumber, " +
//            " farmer.totalLandHolding, " +
//            " farmer.passbookNumber, " +
//            " farmer.landCategoryId, " +
//            " farmer.educationId, " +
//            " farmer.representativeId, " +
//            " farmer.khazaneRecipientId, " +
//            " farmer.photoPath, " +
//            " farmer.farmerTypeId, " +
//            " farmer.minority, " +
//            " farmer.rdNumber, " +
//            " farmer.casteStatus, " +
//            " farmer.genderStatus, " +
//            " farmer.fatherNameKan, " +
//            " farmer.fatherName, " +
//            " farmer.nameKan, " +
//            " caste.title, " +
//            " landCategory.landCategoryName, " +
//            " farmerType.farmerTypeName, " +
//            " tscMaster.name, " +
//            " education.name, " +
//            " farmer.isOtherStateFarmer, " +
//            " farmerBankAccount.farmerBankAccountNumber, " +
//            " userMaster.username, " +
//            " farmer.aadhaarNumber" +
//            ") " +
//            "FROM Farmer farmer " +
//            "LEFT JOIN Caste caste ON farmer.casteId = caste.casteId " +
//            "LEFT JOIN LandCategory landCategory ON farmer.landCategoryId = landCategory.landCategoryId " +
//            "LEFT JOIN FarmerType farmerType ON farmer.farmerTypeId = farmerType.farmerTypeId " +
//            "LEFT JOIN TscMaster tscMaster ON farmer.tscMasterId = tscMaster.tscMasterId " +
//            "LEFT JOIN Education education ON farmer.educationId = education.educationId " +
//            "LEFT JOIN FarmerBankAccount farmerBankAccount ON farmer.farmerId = farmerBankAccount.farmerId " +
//            "LEFT JOIN UserMaster userMaster ON farmer.assignToInspectId = userMaster.userMasterId " +
//            "LEFT JOIN FarmerAddress fa ON fa.farmerId = farmer.farmerId " +
//            "WHERE farmer.active = :isActive " +
//            "AND farmer.isOtherStateFarmer = true " +
//            "AND (:districtId IS NULL OR fa.districtId = :districtId) " +
//            "AND (:talukId IS NULL OR fa.talukId = :talukId) " +
//            "AND (:hobliId IS NULL OR fa.hobliId = :hobliId) " +
//            "ORDER BY farmer.farmerNumber ASC")
//    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForNonKAFarmersList(
//            @Param("isActive") boolean isActive,
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("hobliId") Long hobliId,
//            Pageable pageable);
//

    @Query("SELECT new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name, " +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber, " +
            " userMaster.username, " +
            " farmer.aadhaarNumber " +
            ") " +
            "FROM Farmer farmer " +
            "LEFT JOIN Caste caste ON farmer.casteId = caste.casteId " +
            "LEFT JOIN LandCategory landCategory ON farmer.landCategoryId = landCategory.landCategoryId " +
            "LEFT JOIN FarmerType farmerType ON farmer.farmerTypeId = farmerType.farmerTypeId " +
            "LEFT JOIN TscMaster tscMaster ON farmer.tscMasterId = tscMaster.tscMasterId " +
            "LEFT JOIN Education education ON farmer.educationId = education.educationId " +
            "LEFT JOIN FarmerBankAccount farmerBankAccount ON farmer.farmerId = farmerBankAccount.farmerId " +
            "LEFT JOIN UserMaster userMaster ON farmer.assignToInspectId = userMaster.userMasterId " +
            "LEFT JOIN FarmerAddress fa ON fa.farmerId = farmer.farmerId " +
            "LEFT JOIN District district ON fa.districtId = district.districtId " +
            "LEFT JOIN Taluk taluk ON fa.talukId = taluk.talukId " +
            "LEFT JOIN Hobli hobli ON fa.hobliId = hobli.hobliId " +
            "LEFT JOIN State state ON fa.stateId = state.stateId " +
            "WHERE farmer.active = :isActive " +
            "AND (farmer.isOtherStateFarmer = false OR farmer.isOtherStateFarmer IS NULL) " +
            "AND (farmer.fruitsId = '' OR farmer.fruitsId IS NULL) " +
            "AND (:stateId IS NULL OR fa.stateId = :stateId) " +   // ✅ filter only
            "AND (:districtId IS NULL OR fa.districtId = :districtId) " +
            "AND (:talukId IS NULL OR fa.talukId = :talukId) " +
            "AND (:hobliId IS NULL OR fa.hobliId = :hobliId) " +
            "AND (:casteId IS NULL OR farmer.casteId = :casteId) " +
            "ORDER BY farmer.farmerNumber ASC")
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsIds(
            @Param("isActive") boolean isActive,
            @Param("stateId") Long stateId,
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("hobliId") Long hobliId,
            @Param("casteId") Long casteId,

            Pageable pageable);


    @Query("SELECT new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name, " +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber, " +
            " userMaster.username, " +
            " farmer.aadhaarNumber " +
            ") " +
            "FROM Farmer farmer " +
            "LEFT JOIN Caste caste ON farmer.casteId = caste.casteId " +
            "LEFT JOIN LandCategory landCategory ON farmer.landCategoryId = landCategory.landCategoryId " +
            "LEFT JOIN FarmerType farmerType ON farmer.farmerTypeId = farmerType.farmerTypeId " +
            "LEFT JOIN TscMaster tscMaster ON farmer.tscMasterId = tscMaster.tscMasterId " +
            "LEFT JOIN Education education ON farmer.educationId = education.educationId " +
            "LEFT JOIN FarmerBankAccount farmerBankAccount ON farmer.farmerId = farmerBankAccount.farmerId " +
            "LEFT JOIN UserMaster userMaster ON farmer.assignToInspectId = userMaster.userMasterId " +
            "LEFT JOIN FarmerAddress fa ON fa.farmerId = farmer.farmerId " +
            "LEFT JOIN District district ON fa.districtId = district.districtId " +
            "LEFT JOIN Taluk taluk ON fa.talukId = taluk.talukId " +
            "LEFT JOIN Hobli hobli ON fa.hobliId = hobli.hobliId " +
            "LEFT JOIN State state ON fa.stateId = state.stateId " +
            "WHERE farmer.active = :isActive " +
            "AND farmer.isOtherStateFarmer = true " +
            "AND (:stateId IS NULL OR fa.stateId = :stateId) " +   // ✅ filter only
            "AND (:districtId IS NULL OR fa.districtId = :districtId) " +
            "AND (:talukId IS NULL OR fa.talukId = :talukId) " +
            "AND (:hobliId IS NULL OR fa.hobliId = :hobliId) " +
            "AND (:casteId IS NULL OR farmer.casteId = :casteId) " +
            "ORDER BY farmer.farmerNumber ASC")
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForNonKAFarmersList(
            @Param("isActive") boolean isActive,
            @Param("stateId") Long stateId,
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("hobliId") Long hobliId,
            @Param("casteId") Long casteId,
            Pageable pageable);


    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerDTO(" +
            " farmer.farmerId, " +
            " farmer.farmerNumber, " +
            " farmer.fruitsId, " +
            " farmer.firstName, " +
            " farmer.middleName, " +
            " farmer.lastName, " +
            " farmer.dob, " +
            " farmer.genderId, " +
            " farmer.casteId, " +
            " farmer.tscMasterId, " +
            " farmer.assignToInspectId, " +
            " farmer.differentlyAbled, " +
            " farmer.email, " +
            " farmer.mobileNumber, " +
            " farmer.epicNumber, " +
            " farmer.rationCardNumber, " +
            " farmer.totalLandHolding, " +
            " farmer.passbookNumber, " +
            " farmer.landCategoryId, " +
            " farmer.educationId, " +
            " farmer.representativeId, " +
            " farmer.khazaneRecipientId, " +
            " farmer.photoPath, " +
            " farmer.farmerTypeId, " +
            " farmer.minority, " +
            " farmer.rdNumber, " +
            " farmer.casteStatus, " +
            " farmer.genderStatus, " +
            " farmer.fatherNameKan, " +
            " farmer.fatherName, " +
            " farmer.nameKan, " +
            " caste.title, " +
            " landCategory.landCategoryName, " +
            " farmerType.farmerTypeName, " +
            " tscMaster.name, " +
            " education.name," +
            " farmer.isOtherStateFarmer, " +
            " farmerBankAccount.farmerBankAccountNumber," +
            " userMaster.username," +
            " farmer.aadhaarNumber" +
            ") " +
            "from Farmer farmer " +
            "left join Caste caste " +
            "on farmer.casteId = caste.casteId " +
            "left join LandCategory landCategory " +
            "on farmer.landCategoryId = landCategory.landCategoryId " +
            "left join FarmerType farmerType " +
            "on farmer.farmerTypeId = farmerType.farmerTypeId " +
            "left join TscMaster tscMaster " +
            "on farmer.tscMasterId = tscMaster.tscMasterId " +
            "left join Education education " +
            "on farmer.educationId = education.educationId " +
            "left join FarmerBankAccount farmerBankAccount " +
            "on farmer.farmerId = farmerBankAccount.farmerId " +
            "left join UserMaster userMaster\n" +
            "on farmer.assignToInspectId = userMaster.userMasterId " +
            "where farmer.active = :isActive AND " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText)OR" +
            "(:joinColumn = 'farmerBankAccount.farmerBankAccountNumber' AND farmerBankAccount.farmerBankAccountNumber LIKE :searchText) "
    )
    public Page<FarmerDTO> getSortedFarmers(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

    @Query(nativeQuery = true, value = "select COUNT(farmer_id) as total_farmer_count\n" +
            "from farmer;\n")
    public List<Object[]> getFarmerCountDetails();

//    @Query(nativeQuery = true,value = "select d.district_name, COUNT(f.farmer_id) as farmer_count\n" +
//            "from farmer f\n" +
//            "left join farmer_address fa on fa.farmer_id=f.farmer_id \n" +
//            "left join district d on d.DISTRICT_ID = fa.DISTRICT_ID GROUP BY d.district_name;\n" )
//    public List<Object[]> getDistrictWiseCount();
//
//    @Query(nativeQuery = true,value = "select t.taluk_name, COUNT(f.farmer_id) AS farmer_count \n" +
//            "from farmer f\n" +
//            "left join farmer_address fa on fa.farmer_id = f.farmer_id\n"+
//            "left join district d on d.district_id = fa.district_id\n" +
//            "left join taluk t on t.taluk_id = fa.taluk_id\n" +
//            "where d.district_id = :districtId \n" +
//            "GROUP BY t.taluk_name;\n")
//    public List<Object[]> getTalukWise(@Param("districtId") int districtId);

    //    @Query(nativeQuery = true, value = """
//    SELECT d.district_name, COUNT(f.farmer_id) AS farmer_count
//    FROM farmer f
//    LEFT JOIN farmer_address fa ON fa.farmer_id = f.farmer_id AND fa.active = 1
//    LEFT JOIN district d ON d.DISTRICT_ID = fa.DISTRICT_ID AND d.active = 1
//    WHERE f.active = 1
//    GROUP BY d.district_name;
//""")
//    public List<Object[]> getDistrictWiseCount();
    @Query(nativeQuery = true, value = """
                WITH PrimaryAddress AS (
                    SELECT
                        fa.farmer_id,
                        fa.DISTRICT_ID,
                        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
                    FROM
                        farmer_address fa
                    WHERE
                        fa.active = 1
                )
                SELECT 
                    d.district_name, 
                    COUNT(f.farmer_id) AS farmer_count
                FROM 
                    farmer f
                LEFT JOIN 
                    PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
                LEFT JOIN 
                    district d ON d.DISTRICT_ID = pa.DISTRICT_ID AND d.active = 1
                WHERE 
                    f.active = 1
                GROUP BY 
                    d.district_name;
            """)
    public List<Object[]> getDistrictWiseCount();


    //    @Query(nativeQuery = true, value = """
//    SELECT t.taluk_name, COUNT(f.farmer_id) AS farmer_count
//    FROM farmer f
//    LEFT JOIN farmer_address fa ON fa.farmer_id = f.farmer_id AND fa.active = 1
//    LEFT JOIN district d ON d.DISTRICT_ID = fa.DISTRICT_ID AND d.active = 1
//    LEFT JOIN taluk t ON t.TALUK_ID = fa.TALUK_ID AND t.active = 1
//    WHERE f.active = 1 AND d.DISTRICT_ID = :districtId
//    GROUP BY t.taluk_name;
//""")
//    public List<Object[]> getTalukWise(@Param("districtId") int districtId);
    @Query(nativeQuery = true, value = """
                WITH PrimaryAddress AS (
                    SELECT
                        fa.farmer_id,
                        fa.DISTRICT_ID,
                        fa.TALUK_ID,
                        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
                    FROM
                        farmer_address fa
                    WHERE
                        fa.active = 1
                )
                SELECT 
                    t.taluk_name, 
                    COUNT(f.farmer_id) AS farmer_count
                FROM 
                    farmer f
                LEFT JOIN 
                    PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
                LEFT JOIN 
                    district d ON d.DISTRICT_ID = pa.DISTRICT_ID AND d.active = 1
                LEFT JOIN 
                    taluk t ON t.TALUK_ID = pa.TALUK_ID AND t.active = 1
                WHERE 
                    f.active = 1 AND d.DISTRICT_ID = :districtId
                GROUP BY 
                    t.taluk_name;
            """)
    public List<Object[]> getTalukWise(@Param("districtId") int districtId);



//    @Query(nativeQuery = true, value = """
//                WITH PrimaryAddress AS (
//                    SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
//                        fa.farmer_id,
//                        fa.DISTRICT_ID,
//                        fa.TALUK_ID,
//                        fa.HOBLI_ID,
//                        fa.VILLAGE_ID,
//                        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
//                    FROM
//                        farmer_address fa
//                    WHERE fa.active = 1
//                )
//                SELECT
//                    f.farmer_id,
//                    f.first_name,
//                    f.middle_name,
//                    f.last_name,
//                    f.fruits_id,
//                    f.farmer_number,
//                    f.father_name,
//                    f.passbook_number,
//                    f.epic_number,
//                    f.ration_card_number,
//                    f.dob,
//                    d.DISTRICT_NAME,
//                    t.TALUK_NAME,
//                    h.hobli_name,
//                    v.village_name,
//                    fba.farmer_bank_name,
//                    fba.farmer_bank_account_number,
//                    fba.farmer_bank_branch_name,
//                    fba.farmer_bank_ifsc_code,
//                    c.caste_title
//                FROM
//                    farmer f
//                LEFT JOIN
//                    PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
//                LEFT JOIN
//                    farmer_bank_account fba ON fba.farmer_id = f.farmer_id AND fba.active = 1
//                LEFT JOIN
//                    caste c ON f.caste_id = c.caste_id
//                LEFT JOIN
//                    district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
//                LEFT JOIN
//                    taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
//                LEFT JOIN
//                    hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
//                LEFT JOIN
//                    village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
//                WHERE
//                    f.active = 1 AND
//                    (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
//                    (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
//                    (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
//                    (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId) AND
//                    (:casteId IS NULL OR f.caste_id = :casteId)
//            """, countQuery = """
//                WITH PrimaryAddress AS (
//                    SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
//                        fa.farmer_id,
//                        fa.DISTRICT_ID,
//                        fa.TALUK_ID,
//                        fa.HOBLI_ID,
//                        fa.VILLAGE_ID,
//                        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
//                    FROM
//                        farmer_address fa
//                    WHERE fa.active = 1
//                )
//                SELECT COUNT(*)
//                FROM
//                    farmer f
//                LEFT JOIN
//                    PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
//                WHERE
//                    f.active = 1 AND
//                    (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
//                    (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
//                    (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
//                    (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId) AND
//                    (:casteId IS NULL OR f.caste_id = :casteId)
//            """)
//    Page<Object[]> getPrimaryFarmerDetails(
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("villageId") Long villageId,
//            @Param("tscMasterId") Long tscMasterId,
//            @Param("casteId") Long casteId,
//            Pageable pageable);



    @Query(nativeQuery = true, value = """
    WITH PrimaryAddress AS (
        SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
               fa.farmer_id,
               fa.DISTRICT_ID,
               fa.TALUK_ID,
               fa.HOBLI_ID,
               fa.VILLAGE_ID,
               ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
        FROM farmer_address fa
        WHERE fa.active = 1
    ),
    LatestLand AS (
        SELECT fld.*,
               ROW_NUMBER() OVER (PARTITION BY fld.farmer_id ORDER BY fld.created_date DESC) AS rn
        FROM farmer_land_details fld
        WHERE fld.active = 1
    )
    SELECT
        f.farmer_id,
        f.first_name,
        f.middle_name,
        f.last_name,
        f.fruits_id,
        f.farmer_number,
        f.father_name,
        f.passbook_number,
        f.epic_number,
        f.ration_card_number,
        f.dob,
        d.DISTRICT_NAME,
        t.TALUK_NAME,
        h.hobli_name,
        v.village_name,
        fba.farmer_bank_name,
        fba.farmer_bank_account_number,
        fba.farmer_bank_branch_name,
        fba.farmer_bank_ifsc_code,
        c.caste_title,
        fld.mulberry_area,
        fld.owner_name,
        fld.survey_number,
        fld.spacing,
        fld.hissa,
        fld.rearing_house_details,
        fld.address,
        mv.mulberry_variety_name
    FROM farmer f
    LEFT JOIN PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
    LEFT JOIN farmer_bank_account fba ON fba.farmer_id = f.farmer_id AND fba.active = 1
    LEFT JOIN caste c ON f.caste_id = c.caste_id
    LEFT JOIN district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
    LEFT JOIN taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
    LEFT JOIN hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
    LEFT JOIN village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
    LEFT JOIN LatestLand fld ON fld.farmer_id = f.farmer_id AND fld.rn = 1 -- ✅ only 1 land row per farmer
    LEFT JOIN mulberry_variety mv ON mv.mulberry_variety_id = fld.mulberry_variety_id
    WHERE f.active = 1
      AND (:districtId IS NULL OR pa.DISTRICT_ID = :districtId)
      AND (:talukId IS NULL OR pa.TALUK_ID = :talukId)
      AND (:villageId IS NULL OR pa.VILLAGE_ID = :villageId)
      AND (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId)
      AND (:casteId IS NULL OR f.caste_id = :casteId)
      AND (
             :landFilter IS NULL
          OR (:landFilter = 'WITH' AND EXISTS (
                 SELECT 1 FROM farmer_land_details fld2 WHERE fld2.farmer_id = f.farmer_id
             ))
          OR (:landFilter = 'WITHOUT' AND NOT EXISTS (
                 SELECT 1 FROM farmer_land_details fld3 WHERE fld3.farmer_id = f.farmer_id
             ))
         )
    """,
            countQuery = """
    WITH PrimaryAddress AS (
        SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
               fa.farmer_id,
               fa.DISTRICT_ID,
               fa.TALUK_ID,
               fa.HOBLI_ID,
               fa.VILLAGE_ID,
               ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
        FROM farmer_address fa
        WHERE fa.active = 1
    )
    SELECT COUNT(DISTINCT f.farmer_id)
    FROM farmer f
    LEFT JOIN PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
    WHERE f.active = 1
      AND (:districtId IS NULL OR pa.DISTRICT_ID = :districtId)
      AND (:talukId IS NULL OR pa.TALUK_ID = :talukId)
      AND (:villageId IS NULL OR pa.VILLAGE_ID = :villageId)
      AND (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId)
      AND (:casteId IS NULL OR f.caste_id = :casteId)
      AND (
             :landFilter IS NULL
          OR (:landFilter = 'WITH' AND EXISTS (
                 SELECT 1 FROM farmer_land_details fld WHERE fld.farmer_id = f.farmer_id
             ))
          OR (:landFilter = 'WITHOUT' AND NOT EXISTS (
                 SELECT 1 FROM farmer_land_details fld WHERE fld.farmer_id = f.farmer_id
             ))
         )
    """)
    Page<Object[]> getPrimaryFarmerDetails(
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("villageId") Long villageId,
            @Param("tscMasterId") Long tscMasterId,
            @Param("casteId") Long casteId,
            @Param("landFilter") String landFilter,
            Pageable pageable);



    @Query(nativeQuery = true, value = """
            SELECT CONCAT(f.first_name, ' ', f.middle_name) AS full_name,
                            f.father_name,
                           fa.address_text,
                           f.FARMER_ID,
                           f.fruits_id,
                           v.VILLAGE_NAME
                    FROM FARMER f
                    Left JOIN farmer_address fa ON f.FARMER_ID = fa.FARMER_ID
                    Left JOIN VILLAGE v  ON v.VILLAGE_ID = fa.VILLAGE_ID
                WHERE fa.default_address = 1
                AND f.fruits_id = :fruitsId
                AND f.active = 1;
            """)
    public List<Object[]> getFruitsDetails(String fruitsId);

    @Query(nativeQuery = true, value = """
            SELECT
            fld.farmer_land_details_id,
            f.farmer_id,
            fld.survey_number,
            v.VILLAGE_NAME
                    FROM
            farmer_land_details fld
            LEFT JOIN
            farmer f ON fld.farmer_id = f.farmer_id
            LEFT JOIN
            village v ON fld.village_id = v.VILLAGE_ID
            WHERE
            f.fruits_id = :fruitsId
            AND f.active = 1;
            """)

    public List<Object[]> getFarmerLandDetails(String fruitsId);

    @Query(nativeQuery = true, value = """
                WITH PrimaryAddress AS (
                    SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
                           fa.farmer_id,
                           fa.STATE_ID,
                           fa.DISTRICT_ID,
                           fa.TALUK_ID,
                           fa.HOBLI_ID,
                           fa.VILLAGE_ID,
                           ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
                    FROM farmer_address fa
                    WHERE fa.active = 1
                )
                SELECT
                    f.farmer_id,
                    f.first_name,
                    f.middle_name,
                    f.last_name,
                    f.fruits_id,
                    f.farmer_number,
                    f.father_name,
                    f.dob,
                    f.mobile_number,
                    d.DISTRICT_NAME,
                    t.TALUK_NAME,
                    h.hobli_name,
                    v.village_name,
                    sadod.rate_per100dfls_price,
                    sadod.number_of_dfls_disposed,
                    sadod.lot_number,
                    s.state_name,
                    sadod.race_id,
                    rm.race_name,
                    fc.fitness_certificate_path
                FROM farmer f
                LEFT JOIN PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
                LEFT JOIN state s ON pa.state_id = s.state_id AND s.active = 1
                LEFT JOIN district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
                LEFT JOIN taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
                LEFT JOIN hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
                LEFT JOIN village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
                Inner JOIN
                sale_and_disposal_of_dfls sadod ON sadod.fruits_id = f.fruits_id AND (sadod.is_disposed = 0 OR sadod.is_disposed IS NULL) AND  sadod.active = 1
                INNER JOIN fitness_certificate fc
                   ON fc.farmer_id = f.FARMER_ID
                   AND fc.active = 1
                   AND fc.is_fc_issued = 1
                LEFT JOIN
                race_master rm ON rm.race_id = sadod.race_id AND rm.active = 1
                WHERE
                    (:type = 'mobileNumber' AND f.mobile_number = :text) OR
                    (:type = 'farmerNumber' AND f.farmer_number = :text) OR
                    (:type = 'fruitsId' AND f.fruits_id = :text)
            """)
    List<Object[]> getFarmerDetailsForSeedCocoonMarket(String text, String type);

    @Query(value = """
            WITH PrimaryAddress AS (
                SELECT
                    fa.farmer_id,
                    fa.district_id,
                    fa.taluk_id,
                    fa.hobli_id,
                    fa.village_id,
                    ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
                FROM farmer_address fa
                WHERE fa.active = 1
            )
            SELECT
                f.FARMER_ID,
                f.farmer_number,
                f.fruits_id,
                f.first_name,
                f.middle_name,
                f.last_name,
                f.dob,
            
                CASE
                    WHEN f.gender_id = 1 THEN 'Male'
                    WHEN f.gender_id = 2 THEN 'Female'
                    WHEN f.gender_id = 3 THEN 'Others'
                    ELSE 'Unknown'
                END AS gender,
            
                caste.caste_title,
                f.differently_abled,
                f.email,
                f.mobile_number,
                f.aadhaar_number,
                f.epic_number,
                f.ration_card_number,
                f.total_land_holding,
                f.passbook_number,
                f.representative_id,
                f.khazane_recipient_id,
                f.photo_path,
            
                ft.name AS farmer_type,
                f.minority,
                f.father_name,
                f.father_name_kan,
                f.name_kan,
            
                tsc.name AS tsc_name,
                tsc.name_in_kannada AS tsc_name_kan,
            
                edu.education_name,
                edu.education_name_in_kannada,
            
                addr.address_text,
                addr.pincode,
                dist.district_name,
                dist.district_name_in_kannada,
                tal.taluk_name,
                tal.taluk_name_in_kannada,
                vill.village_name,
                vill.village_name_in_kannada
            
            FROM farmer f
            
            LEFT JOIN PrimaryAddress pa
                ON pa.farmer_id = f.farmer_id AND pa.rn = 1
            
            OUTER APPLY (
                SELECT TOP 1 *
                FROM farmer_address
                WHERE FARMER_ID = f.FARMER_ID AND active = 1
                ORDER BY default_address DESC, created_date ASC
            ) addr
            
            LEFT JOIN education edu
                ON f.education_id = edu.education_id AND edu.active = 1
            
            LEFT JOIN farmer_type ft
                ON f.farmer_type_id = ft.farmer_type_id AND ft.active = 1
            
            LEFT JOIN caste caste
                ON f.caste_id = caste.caste_id AND caste.active = 1
            
            LEFT JOIN tsc_master tsc
                ON f.tsc_master_id = tsc.tsc_master_id AND tsc.active = 1
            
            LEFT JOIN village vill
                ON pa.village_id = vill.village_id AND vill.active = 1
            
            LEFT JOIN taluk tal
                ON pa.taluk_id = tal.taluk_id AND tal.active = 1
            
            LEFT JOIN district dist
                ON pa.district_id = dist.district_id AND dist.active = 1
            WHERE f.active = 1;
            """, nativeQuery = true)
    List<Map<String, Object>> getFullFarmerDetails();
}
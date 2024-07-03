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
import java.util.Optional;
import java.util.Set;

@Repository
public interface FarmerRepository extends PagingAndSortingRepository<Farmer, Long> {
    public List<Farmer> findByFarmerNumber(String farmerNumber);

    public List<Farmer> findByMobileNumber(String mobileNumber);

    public Farmer findByFarmerNumberAndActive(String farmerNumber,boolean isActive);

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
            " farmer.isOtherStateFarmer " +
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
            " farmer.isOtherStateFarmer " +
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
            " farmer.isOtherStateFarmer " +
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
            "where farmer.active = :isActive and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAsc(@Param("isActive") boolean isActive,@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

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
            " farmer.isOtherStateFarmer " +
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
            "where farmer.active = :isActive and farmer.isOtherStateFarmer = true and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForNonKAFarmers(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn,@Param("searchText") String searchText, final Pageable pageable);

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
            " farmer.isOtherStateFarmer " +
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
            "where farmer.active = :isActive and (farmer.isOtherStateFarmer = false or farmer.isOtherStateFarmer is null) and (farmer.fruitsId != '' and farmer.fruitsId is not null) and  " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(@Param("isActive") boolean isActive,@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

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
            " farmer.isOtherStateFarmer " +
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
            "where farmer.active = :isActive and (farmer.isOtherStateFarmer = false or farmer.isOtherStateFarmer is NULL) and (farmer.fruitsId = '' or farmer.fruitsId is null) and " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText) " +
            "ORDER BY farmer.farmerNumber ASC"
    )
    Page<FarmerDTO> getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsId(@Param("isActive") boolean isActive, @Param("joinColumn") String joinColumn, @Param("searchText") String searchText, final Pageable pageable);

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
            " farmer.isOtherStateFarmer " +
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
            "where farmer.active = :isActive AND " +
            "(:joinColumn = 'farmer.fruitsId' AND farmer.fruitsId LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.farmerNumber' AND farmer.farmerNumber LIKE :searchText) OR " +
            "(:joinColumn = 'farmer.mobileNumber' AND farmer.mobileNumber LIKE :searchText)"
    )
    public Page<FarmerDTO> getSortedFarmers(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

    @Query(nativeQuery = true,value = "select COUNT(farmer_id) as total_farmer_count\n" +
            "from farmer;\n")
    public List<Object[]> getFarmerCountDetails( );

    @Query(nativeQuery = true,value = "select d.district_name, COUNT(f.farmer_id) as farmer_count\n" +
            "from farmer f\n" +
            "left join user_master um on um.username=f.CREATED_BY \n" +
            "left join district d on d.DISTRICT_ID = um.DISTRICT_ID GROUP BY d.district_name;\n" )
    public List<Object[]> getDistrictWiseCount();

    @Query(nativeQuery = true,value = "select t.taluk_name, COUNT(f.farmer_id) AS farmer_count \n" +
            "from farmer f\n" +
            "left join user_master um on um.username = f.CREATED_BY\n"+
            "left join district d on d.district_id = um.district_id\n" +
            "left join taluk t on t.taluk_id = um.taluk_id\n" +
            "where d.district_id = :districtId \n" +
            "GROUP BY t.taluk_name;\n")
    public List<Object[]> getTalukWise(@Param("districtId") int districtId);

//    @Query(nativeQuery = true,value = "WITH PrimaryAddress AS (\n" +
//            "    SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,\n" +
//            "        fa.farmer_id,\n" +
//            "        fa.DISTRICT_ID,\n" +
//            "        fa.TALUK_ID,\n" +
//            "        fa.HOBLI_ID,\n" +
//            "        fa.VILLAGE_ID,\n" +
//            "        ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn\n" +
//            "    FROM \n" +
//            "        farmer_address fa\n" +
//            ")\n" +
//            "SELECT\n" +
//            "    f.farmer_id,\n" +
//            "    f.first_name, \n" +
//            "    f.middle_name,\n" +
//            "    f.last_name,\n" +
//            "    f.fruits_id,\n" +
//            "    f.farmer_number,\n" +
//            "    f.father_name,\n" +
//            "    f.passbook_number,\n" +
//            "    f.epic_number,\n" +
//            "    f.ration_card_number,\n" +
//            "    f.dob,\n" +
//            "    d.DISTRICT_NAME, \n" +
//            "    t.TALUK_NAME,\n" +
//            "    h.hobli_name,\n" +
//            "    v.village_name,\n" +
//            "    fba.farmer_bank_name,\n" +
//            "    fba.farmer_bank_account_number,\n" +
//            "    fba.farmer_bank_branch_name,\n" +
//            "    fba.farmer_bank_ifsc_code\n" +
//            "FROM\n" +
//            "    farmer f\n" +
//            "LEFT JOIN\n" +
//            "    PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1\n" +
//            "LEFT JOIN\n" +
//            "    farmer_bank_account fba ON fba.farmer_id = f.farmer_id\n" +
//            "LEFT JOIN\n" +
//            "    district d ON pa.DISTRICT_ID = d.DISTRICT_ID\n" +
//            "LEFT JOIN\n" +
//            "    taluk t ON pa.TALUK_ID = t.TALUK_ID\n" +
//            "LEFT JOIN\n" +
//            "    hobli h ON pa.HOBLI_ID = h.HOBLI_ID\n" +
//            "LEFT JOIN\n" +
//            "    village v ON pa.VILLAGE_ID = v.VILLAGE_ID\n" +
//            "WHERE\n" +
//            "    (pa.DISTRICT_ID = NULL OR NULL IS NULL) AND\n" +
//            "    (pa.TALUK_ID = NULL OR NULL IS NULL) AND\n" +
//            "    (pa.VILLAGE_ID = NULL OR NULL IS NULL) AND\n" +
//            "    (f.tsc_master_id = NULL OR NULL IS NULL)\n")
//    public List<Object[]> getPrimaryFarmerReportDetails( @Param("districtId") Long districtId,
//                                                   @Param("talukId") Long talukId,
//                                                   @Param("villageId") Long villageId,
//                                                   @Param("tscMasterId") Long tscMasterId,Pageable pageable);

//    @Query(nativeQuery = true, value = """
//    WITH PrimaryAddress AS (
//        SELECT ROW_NUMBER() OVER (ORDER BY fa.farmer_id ASC) AS row_id,
//            fa.farmer_id,
//            fa.DISTRICT_ID,
//            fa.TALUK_ID,
//            fa.HOBLI_ID,
//            fa.VILLAGE_ID,
//            ROW_NUMBER() OVER (PARTITION BY fa.farmer_id ORDER BY fa.district_id DESC) AS rn
//        FROM
//            farmer_address fa
//    )
//    SELECT
//        f.farmer_id,
//        f.first_name,
//        f.middle_name,
//        f.last_name,
//        f.fruits_id,
//        f.farmer_number,
//        f.father_name,
//        f.passbook_number,
//        f.epic_number,
//        f.ration_card_number,
//        f.dob,
//        d.DISTRICT_NAME,
//        t.TALUK_NAME,
//        h.hobli_name,
//        v.village_name,
//        fba.farmer_bank_name,
//        fba.farmer_bank_account_number,
//        fba.farmer_bank_branch_name,
//        fba.farmer_bank_ifsc_code
//    FROM
//        farmer f
//    LEFT JOIN
//        PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
//    LEFT JOIN
//        farmer_bank_account fba ON fba.farmer_id = f.farmer_id
//    LEFT JOIN
//        district d ON pa.DISTRICT_ID = d.DISTRICT_ID
//    LEFT JOIN
//        taluk t ON pa.TALUK_ID = t.TALUK_ID
//    LEFT JOIN
//        hobli h ON pa.HOBLI_ID = h.HOBLI_ID
//    LEFT JOIN
//        village v ON pa.VILLAGE_ID = v.VILLAGE_ID
//    WHERE
//        (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
//        (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
//        (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
//        (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId)
//""")
//    public List<Object[]> getPrimaryFarmerDetails(
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("villageId") Long villageId,
//            @Param("tscMasterId") Long tscMasterId,
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
        FROM
            farmer_address fa
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
        fba.farmer_bank_ifsc_code
    FROM
        farmer f
    LEFT JOIN
        PrimaryAddress pa ON pa.farmer_id = f.farmer_id AND pa.rn = 1
    LEFT JOIN
        farmer_bank_account fba ON fba.farmer_id = f.farmer_id
    LEFT JOIN
        district d ON pa.DISTRICT_ID = d.DISTRICT_ID
    LEFT JOIN
        taluk t ON pa.TALUK_ID = t.TALUK_ID
    LEFT JOIN
        hobli h ON pa.HOBLI_ID = h.HOBLI_ID
    LEFT JOIN
        village v ON pa.VILLAGE_ID = v.VILLAGE_ID
    WHERE
        (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
        (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
        (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
        (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId)
""", countQuery = """
    SELECT COUNT(*)
    FROM
        farmer f
    LEFT JOIN
        farmer_address fa ON fa.farmer_id = f.farmer_id
    WHERE
        (:districtId IS NULL OR fa.DISTRICT_ID = :districtId) AND
        (:talukId IS NULL OR fa.TALUK_ID = :talukId) AND
        (:villageId IS NULL OR fa.VILLAGE_ID = :villageId) AND
        (:tscMasterId IS NULL OR f.tsc_master_id = :tscMasterId)
""")
    Page<Object[]> getPrimaryFarmerDetails(
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("villageId") Long villageId,
            @Param("tscMasterId") Long tscMasterId,
            Pageable pageable);



}
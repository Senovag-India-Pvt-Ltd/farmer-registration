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
import java.util.Set;

@Repository
public interface FarmerRepository extends PagingAndSortingRepository<Farmer, Long> {
    public List<Farmer> findByFarmerNumber(String farmerNumber);

    public List<Farmer> findByMobileNumber(String mobileNumber);

    public Farmer findByFarmerNumberAndActive(String farmerNumber,boolean isActive);

    public Page<Farmer> findByActiveOrderByFarmerIdAsc(boolean isActive, final Pageable pageable);

    public Farmer save(Farmer farmer);

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
}
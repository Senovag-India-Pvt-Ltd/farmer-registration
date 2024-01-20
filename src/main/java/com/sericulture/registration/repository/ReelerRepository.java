package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerSearchDTO;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.model.entity.Reeler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReelerRepository extends PagingAndSortingRepository<Reeler, Long> {

    public Page<Reeler> findByActiveOrderByReelerIdAsc(boolean isActive, final Pageable pageable);

    public List<Reeler> findByActiveOrderByReelerIdAsc(boolean isActive);

    public List<Reeler> findByReelingLicenseNumber(String reelingLicenseNumber);

   public List<Reeler> findByReelerNumber(String reelerNumber);

    public List<Reeler> findByActive(boolean isActive);

    public Reeler save(Reeler Reeler);

    public Reeler findByReelerIdAndActive(long id, boolean isActive);

    public Reeler findByReelerIdAndActiveIn(@Param("ReelerId") long ReelerId, @Param("active") Set<Boolean> active);
    public Reeler findByFruitsIdAndActive(String fruitsId, boolean active);
    public Reeler findByReelingLicenseNumberAndActive(String reelingLicenseNumber, boolean isActive);

    List<Reeler> findByActiveAndIsActivatedOrderByReelerNameAsc(boolean isActive, int isActivated);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.reelingUnitBoundary, " +
            " reeler.dob, " +
            " reeler.rationCard, " +
            " reeler.machineTypeId, " +
            " reeler.gender, " +
            " reeler.dateOfMachineInstallation, " +
            " reeler.electricityRrNumber, " +
            " reeler.casteId, " +
            " reeler.revenueDocument, " +
            " reeler.numberOfBasins, " +
            " reeler.mobileNumber, " +
            " reeler.recipientId, " +
            " reeler.mahajarDetails, " +
            " reeler.emailId, " +
            " reeler.representativeNameAddress, " +
            " reeler.loanDetails, " +
            " reeler.assignToInspectId, " +
            " reeler.gpsLat, " +
            " reeler.gpsLng, " +
            " reeler.inspectionDate, " +
            " reeler.arnNumber, " +
            " reeler.chakbandiLat, " +
            " reeler.chakbandiLng, " +
            " reeler.address, " +
            " reeler.pincode, " +
            " reeler.stateId, " +
            " reeler.districtId, " +
            " reeler.talukId, " +
            " reeler.hobliId, " +
            " reeler.villageId, " +
            " reeler.licenseReceiptNumber, " +
            " reeler.licenseExpiryDate, " +
            " reeler.receiptDate, " +
            " reeler.functionOfUnit, " +
            " reeler.reelingLicenseNumber, " +
            " reeler.feeAmount, " +
            " reeler.memberLoanDetails, " +
            " reeler.mahajarEast, " +
            " reeler.mahajarWest, " +
            " reeler.mahajarNorth, " +
            " reeler.mahajarSouth, " +
            " reeler.mahajarNorthEast, " +
            " reeler.mahajarNorthWest, " +
            " reeler.mahajarSouthEast, " +
            " reeler.mahajarSouthWest, " +
            " reeler.bankName, " +
            " reeler.bankAccountNumber, " +
            " reeler.branchName, " +
            " reeler.ifscCode, " +
            " reeler.status, " +
            " reeler.licenseRenewalDate, " +
            " reeler.fruitsId, " +
            " caste.title, " +
            " education.name, " +
            " machineType.machineTypeName, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName," +
            " reeler.isActivated," +
            " reeler.walletAmount," +
            " reeler.reelerNumber," +
            " reeler.reelerTypeMasterId," +
            " reelerTypeMaster.reelerTypeMasterName " +
            ") " +
            "from Reeler reeler " +
            "left join Caste caste " +
            "on reeler.casteId = caste.casteId " +
            "left join Education education " +
            "on reeler.educationId = education.educationId " +
            "left join machine_type_master machineType " +
            "on reeler.machineTypeId = machineType.machineTypeId " +
            "left join State state " +
            "on reeler.stateId = state.stateId " +
            "left join District district " +
            "on reeler.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on reeler.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on reeler.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on reeler.villageId = village.villageId " +
            "left join ReelerTypeMaster reelerTypeMaster " +
            "on reeler.reelerTypeMasterId = reelerTypeMaster.reelerTypeMasterId " +
            "where reeler.active = :isActive AND reeler.reelerId = :id")
    public ReelerDTO getByReelerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);


    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.reelingUnitBoundary, " +
            " reeler.dob, " +
            " reeler.rationCard, " +
            " reeler.machineTypeId, " +
            " reeler.gender, " +
            " reeler.dateOfMachineInstallation, " +
            " reeler.electricityRrNumber, " +
            " reeler.casteId, " +
            " reeler.revenueDocument, " +
            " reeler.numberOfBasins, " +
            " reeler.mobileNumber, " +
            " reeler.recipientId, " +
            " reeler.mahajarDetails, " +
            " reeler.emailId, " +
            " reeler.representativeNameAddress, " +
            " reeler.loanDetails, " +
            " reeler.assignToInspectId, " +
            " reeler.gpsLat, " +
            " reeler.gpsLng, " +
            " reeler.inspectionDate, " +
            " reeler.arnNumber, " +
            " reeler.chakbandiLat, " +
            " reeler.chakbandiLng, " +
            " reeler.address, " +
            " reeler.pincode, " +
            " reeler.stateId, " +
            " reeler.districtId, " +
            " reeler.talukId, " +
            " reeler.hobliId, " +
            " reeler.villageId, " +
            " reeler.licenseReceiptNumber, " +
            " reeler.licenseExpiryDate, " +
            " reeler.receiptDate, " +
            " reeler.functionOfUnit, " +
            " reeler.reelingLicenseNumber, " +
            " reeler.feeAmount, " +
            " reeler.memberLoanDetails, " +
            " reeler.mahajarEast, " +
            " reeler.mahajarWest, " +
            " reeler.mahajarNorth, " +
            " reeler.mahajarSouth, " +
            " reeler.mahajarNorthEast, " +
            " reeler.mahajarNorthWest, " +
            " reeler.mahajarSouthEast, " +
            " reeler.mahajarSouthWest, " +
            " reeler.bankName, " +
            " reeler.bankAccountNumber, " +
            " reeler.branchName, " +
            " reeler.ifscCode, " +
            " reeler.status, " +
            " reeler.licenseRenewalDate, " +
            " reeler.fruitsId, " +
            " caste.title, " +
            " education.name, " +
            " machineType.machineTypeName, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName," +
            " reeler.isActivated," +
            " reeler.walletAmount," +
            " reeler.reelerNumber," +
            " reeler.reelerTypeMasterId," +
            " reelerTypeMaster.reelerTypeMasterName " +
            ") " +
            "from Reeler reeler " +
            "left join Caste caste " +
            "on reeler.casteId = caste.casteId " +
            "left join Education education " +
            "on reeler.educationId = education.educationId " +
            "left join machine_type_master machineType " +
            "on reeler.machineTypeId = machineType.machineTypeId " +
            "left join State state " +
            "on reeler.stateId = state.stateId " +
            "left join District district " +
            "on reeler.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on reeler.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on reeler.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on reeler.villageId = village.villageId " +
            "left join ReelerTypeMaster reelerTypeMaster " +
            "on reeler.reelerTypeMasterId = reelerTypeMaster.reelerTypeMasterId " +
            "where reeler.active = :isActive " +
            "ORDER BY reeler.reelerName ASC"
    )
    Page<ReelerDTO> getByActiveOrderByReelerIdAsc(@Param("isActive") boolean isActive, final Pageable pageable);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.reelingUnitBoundary, " +
            " reeler.dob, " +
            " reeler.rationCard, " +
            " reeler.machineTypeId, " +
            " reeler.gender, " +
            " reeler.dateOfMachineInstallation, " +
            " reeler.electricityRrNumber, " +
            " reeler.casteId, " +
            " reeler.revenueDocument, " +
            " reeler.numberOfBasins, " +
            " reeler.mobileNumber, " +
            " reeler.recipientId, " +
            " reeler.mahajarDetails, " +
            " reeler.emailId, " +
            " reeler.representativeNameAddress, " +
            " reeler.loanDetails, " +
            " reeler.assignToInspectId, " +
            " reeler.gpsLat, " +
            " reeler.gpsLng, " +
            " reeler.inspectionDate, " +
            " reeler.arnNumber, " +
            " reeler.chakbandiLat, " +
            " reeler.chakbandiLng, " +
            " reeler.address, " +
            " reeler.pincode, " +
            " reeler.stateId, " +
            " reeler.districtId, " +
            " reeler.talukId, " +
            " reeler.hobliId, " +
            " reeler.villageId, " +
            " reeler.licenseReceiptNumber, " +
            " reeler.licenseExpiryDate, " +
            " reeler.receiptDate, " +
            " reeler.functionOfUnit, " +
            " reeler.reelingLicenseNumber, " +
            " reeler.feeAmount, " +
            " reeler.memberLoanDetails, " +
            " reeler.mahajarEast, " +
            " reeler.mahajarWest, " +
            " reeler.mahajarNorth, " +
            " reeler.mahajarSouth, " +
            " reeler.mahajarNorthEast, " +
            " reeler.mahajarNorthWest, " +
            " reeler.mahajarSouthEast, " +
            " reeler.mahajarSouthWest, " +
            " reeler.bankName, " +
            " reeler.bankAccountNumber, " +
            " reeler.branchName, " +
            " reeler.ifscCode, " +
            " reeler.status, " +
            " reeler.licenseRenewalDate, " +
            " reeler.fruitsId, " +
            " caste.title, " +
            " education.name, " +
            " machineType.machineTypeName, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName," +
            " reeler.isActivated," +
            " reeler.walletAmount," +
            " reeler.reelerNumber," +
            " reeler.reelerTypeMasterId," +
            " reelerTypeMaster.reelerTypeMasterName " +
            ") " +
            "from Reeler reeler " +
            "left join Caste caste " +
            "on reeler.casteId = caste.casteId " +
            "left join Education education " +
            "on reeler.educationId = education.educationId " +
            "left join machine_type_master machineType " +
            "on reeler.machineTypeId = machineType.machineTypeId " +
            "left join State state " +
            "on reeler.stateId = state.stateId " +
            "left join District district " +
            "on reeler.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on reeler.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on reeler.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on reeler.villageId = village.villageId " +
            "left join ReelerTypeMaster reelerTypeMaster " +
            "on reeler.reelerTypeMasterId = reelerTypeMaster.reelerTypeMasterId " +
            "where reeler.active = :isActive AND " +
            "(:joinColumn = 'reeler.mobileNumber' AND reeler.mobileNumber LIKE :searchText) OR " +
            "(:joinColumn = 'reeler.reelingLicenseNumber' AND reeler.reelingLicenseNumber LIKE :searchText)"
    )
    public Page<ReelerDTO> getSortedReelers(@Param("joinColumn") String joinColumn, @Param("searchText") String searchText, @Param("isActive") boolean isActive, Pageable pageable);

 @Query("select new com.sericulture.registration.model.dto.reeler.ReelerSearchDTO(" +
         " reeler.reelerId, " +
         " reeler.reelerName, " +
         " CASE WHEN :joinColumn = 'reeler.mobileNumber' THEN reeler.mobileNumber " +
         "      WHEN :joinColumn = 'reeler.reelerName' THEN reeler.reelerName " +
         "      WHEN :joinColumn = 'reeler.reelingLicenseNumber' THEN reeler.reelingLicenseNumber " +
         "      ELSE null " +
         " END " +
         ") " +
         "from Reeler reeler " +
         "where reeler.active = true AND " +
         "(:joinColumn = 'reeler.mobileNumber' AND reeler.mobileNumber LIKE :searchText) OR " +
         "(:joinColumn = 'reeler.reelerName' AND reeler.reelerName LIKE :searchText) OR " +
         "(:joinColumn = 'reeler.reelingLicenseNumber' AND reeler.reelingLicenseNumber LIKE :searchText)"
 )
 public List<ReelerSearchDTO> getReelerBySearchText(@Param("searchText") String searchText, @Param("joinColumn") String joinColumn);

}
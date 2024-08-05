package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerSearchDTO;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.model.entity.Reeler;
import com.sericulture.registration.model.entity.ReelerTypeMaster;
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

//    public ReelerTypeMaster findByReelerNumberAndActive(String reelerNumber, boolean isActive);


    public List<Reeler> findByReelingLicenseNumberAndActive(String reelingLicenseNumber, boolean isActive);

   public List<Reeler> findByReelerNumberAndActive(String reelerNumber,boolean isActive);

    public List<Reeler> findByActiveOrderByReelerNameAsc(boolean isActive);

    public Reeler save(Reeler Reeler);

    public Reeler findByReelerIdAndActive(long id, boolean isActive);

    public Reeler findByReelerIdAndActiveIn(@Param("ReelerId") long ReelerId, @Param("active") Set<Boolean> active);

    public Reeler findByFruitsIdAndActive(String fruitsId, boolean active);

//    public Reeler findByReelingLicenseNumberAndActive(String reelingLicenseNumber, boolean isActive);

    List<Reeler> findByActiveAndIsActivatedOrderByReelerNameAsc(boolean isActive, int isActivated);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.tscMasterId, " +
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
            " tscMaster.name, " +
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
            "left join TscMaster tscMaster " +
            "on reeler.tscMasterId = tscMaster.tscMasterId " +
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
         " reeler.tscMasterId, " +
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
         " tscMaster.name, " +
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
         "left join TscMaster tscMaster " +
         "on reeler.tscMasterId = tscMaster.tscMasterId " +
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
         "where reeler.active = :isActive AND reeler.reelingLicenseNumber = :id")
 public ReelerDTO getByReelerLicenseNumberAndActive(@Param("id") String id, @Param("isActive") boolean isActive);


    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.tscMasterId, " +
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
            " tscMaster.name, " +
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
            "left join TscMaster tscMaster " +
            "on reeler.tscMasterId = tscMaster.tscMasterId " +
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
            "where reeler.active = :isActive AND reeler.mobileNumber = :mobileNumber")
    public ReelerDTO getByMobileNumberAndActive(@Param("mobileNumber") String mobileNumber, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.tscMasterId, " +
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
            " tscMaster.name, " +
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
            "left join TscMaster tscMaster " +
            "on reeler.tscMasterId = tscMaster.tscMasterId " +
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
            "where reeler.active = :isActive AND reeler.reelerNumber = :reelerNumber")
    public ReelerDTO getByReelerNumberAndActive(@Param("reelerNumber") String reelerNumber,@Param("isActive") boolean isActive);


    @Query("select new com.sericulture.registration.model.dto.reeler.ReelerDTO(" +
            " reeler.reelerId, " +
            " reeler.reelerName, " +
            " reeler.wardNumber, " +
            " reeler.passbookNumber, " +
            " reeler.fatherName, " +
            " reeler.educationId, " +
            " reeler.tscMasterId, " +
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
            " tscMaster.name, " +
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
            "left join TscMaster tscMaster " +
            "on reeler.tscMasterId = tscMaster.tscMasterId " +
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
            " reeler.tscMasterId, " +
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
            " tscMaster.name, " +
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
            "left join TscMaster tscMaster " +
            "on reeler.tscMasterId = tscMaster.tscMasterId " +
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

    @Query(nativeQuery = true,value = "select COUNT(reeler_id) as total_reeler_count\n" +
            "from reeler;\n")
    public List<Object[]> getReelerCountDetails( );


    @Query(nativeQuery = true, value = """
    SELECT 
        d.district_name, 
        COUNT(r.reeler_id) AS reeler_count
    FROM 
        reeler r
    LEFT JOIN 
        district d ON d.DISTRICT_ID = r.DISTRICT_ID AND d.active = 1
    WHERE 
        r.active = 1
    GROUP BY 
        d.district_name;
""")
    public List<Object[]> getDistrictWiseReelerCount();

    @Query(nativeQuery = true, value = """
    SELECT
        t.taluk_name,
        COUNT(r.reeler_id) AS reeler_count
    FROM
        reeler r
    LEFT JOIN
        taluk t ON t.taluk_id = r.taluk_id AND t.active = 1
    LEFT JOIN
        district d ON t.district_id = d.district_id
    WHERE
        r.active = 1
        AND d.district_id = :districtId
    GROUP BY
        t.taluk_name;
""")
    public List<Object[]> getTalukWiseReelerCount(@Param("districtId") int districtId);



    @Query(nativeQuery = true, value = """
    WITH PrimaryAddress AS (
        SELECT
            rvba.reeler_id,
            rvba.market_master_id,
            ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
        FROM
            reeler_virtual_bank_account rvba
        WHERE
            rvba.active = 1
    )
    SELECT 
        mm.market_name, 
        COUNT(r.reeler_id) AS reeler_count
    FROM 
        reeler r
    LEFT JOIN 
        PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
    LEFT JOIN 
        market_master mm ON mm.market_master_id = pa.market_master_id AND mm.active = 1
    WHERE 
        r.active = 1
    GROUP BY 
        mm.market_name;
""")
    public List<Object[]> getMarketWiseReelerCount();

    @Query(nativeQuery = true, value = """
    WITH PrimaryAddress AS (
            SELECT
                    rvba.reeler_id,
            rvba.market_master_id,
            ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
    FROM
    reeler_virtual_bank_account rvba
    WHERE
    rvba.active = 1
            )
    SELECT
    mm.market_name,
    COUNT(r.reeler_id) AS reeler_count
    FROM
    reeler r
    LEFT JOIN
    PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
    LEFT JOIN
    market_master mm ON mm.market_master_id = pa.market_master_id AND mm.active = 1
    WHERE
    r.active = 1
    AND pa.market_master_id = :marketId
    GROUP BY
    mm.market_name;
    """)
    public List<Object[]> getMarketWiseReelerCountByMarketId(@Param("marketId") int marketId);

//    @Query(nativeQuery = true, value = """
//    WITH PrimaryMarket AS (
//        SELECT ROW_NUMBER() OVER (ORDER BY r.reeler_id ASC) AS row_id,
//            r.reeler_id,
//            r.DISTRICT_ID,
//            r.TALUK_ID,
//            r.HOBLI_ID,
//            r.VILLAGE_ID,
//            ROW_NUMBER() OVER (PARTITION BY r.reeler_id ORDER BY r.district_id DESC) AS rn
//        FROM
//            reeler r
//        WHERE r.active = 1
//    )
//    SELECT
//        r.reeler_id,
//        r.name,
//        r.fruits_id,
//        r.reeling_license_number,
//        r.father_name,
//        r.passbook_number,
//        r.reeler_number,
//        r.ration_card,
//        r.dob,
//        d.DISTRICT_NAME,
//        t.TALUK_NAME,
//        h.hobli_name,
//        v.village_name,
//        r.bank_name,
//        r.bank_account_number,
//        r.branch_name,
//        r.ifsc_code
//    FROM
//        reeler r
//    LEFT JOIN
//        PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
//    LEFT JOIN
//        reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
//    LEFT JOIN
//        district d ON pa.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
//    LEFT JOIN
//        taluk t ON pa.TALUK_ID = t.TALUK_ID AND t.active = 1
//    LEFT JOIN
//        hobli h ON pa.HOBLI_ID = h.HOBLI_ID AND h.active = 1
//    LEFT JOIN
//        village v ON pa.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
//    WHERE
//        r.active = 1 AND
//        (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
//        (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
//        (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
//        (:marketId IS NULL OR rvba.market_master_id = :marketId)
//""", countQuery = """
//    WITH PrimaryAddress AS (
//        SELECT ROW_NUMBER() OVER (ORDER BY r.reeler_id ASC) AS row_id,
//            r.reeler_id,
//            r.DISTRICT_ID,
//            r.TALUK_ID,
//            r.HOBLI_ID,
//            r.VILLAGE_ID,
//            ROW_NUMBER() OVER (PARTITION BY r.reeler_id ORDER BY r.district_id DESC) AS rn
//        FROM
//            reeler r
//        WHERE r.active = 1
//    )
//    SELECT COUNT(*)
//    FROM
//        reeler r
//    LEFT JOIN
//        PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
//    LEFT JOIN
//        reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
//    WHERE
//        r.active = 1 AND
//        (:districtId IS NULL OR pa.DISTRICT_ID = :districtId) AND
//        (:talukId IS NULL OR pa.TALUK_ID = :talukId) AND
//        (:villageId IS NULL OR pa.VILLAGE_ID = :villageId) AND
//        (:marketId IS NULL OR rvba.market_master_id = :marketId)
//""")
//    Page<Object[]> getPrimaryReelerDetails(
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("villageId") Long villageId,
//            @Param("marketId") Long marketId,
//            Pageable pageable);

//    @Query(nativeQuery = true, value = """
//    WITH PrimaryAddress AS (
//        SELECT ROW_NUMBER() OVER (ORDER BY rvba.reeler_id ASC) AS row_id,
//               rvba.reeler_id,
//               rvba.market_master_Id,
//               ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
//        FROM reeler_virtual_bank_account rvba
//        WHERE rvba.active = 1
//    )
//    SELECT
//        r.reeler_id,
//        r.name,
//        r.fruits_id,
//        r.reeling_license_number,
//        r.father_name,
//        r.passbook_number,
//        r.reeler_number,
//        r.ration_card,
//        r.dob,
//        d.DISTRICT_NAME,
//        t.TALUK_NAME,
//        h.hobli_name,
//        v.village_name,
//        r.bank_name,
//        r.bank_account_number,
//        r.branch_name,
//        r.ifsc_code
//    FROM reeler r
//    LEFT JOIN PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
//    LEFT JOIN reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
//    LEFT JOIN district d ON r.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
//    LEFT JOIN taluk t ON r.TALUK_ID = t.TALUK_ID AND t.active = 1
//    LEFT JOIN hobli h ON r.HOBLI_ID = h.HOBLI_ID AND h.active = 1
//    LEFT JOIN village v ON r.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
//    WHERE r.active = 1 AND
//          (:districtId IS NULL OR r.DISTRICT_ID = :districtId) AND
//          (:talukId IS NULL OR r.TALUK_ID = :talukId) AND
//          (:villageId IS NULL OR r.VILLAGE_ID = :villageId) AND
//          (:marketId IS NULL OR rvba.market_master_id = :marketId)
//""", countQuery = """
//    WITH PrimaryAddress AS (
//        SELECT ROW_NUMBER() OVER (ORDER BY rvba.reeler_id ASC) AS row_id,
//               rvba.reeler_id,
//               rvba.market_master_Id,
//               ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
//        FROM reeler_virtual_bank_account rvba
//        WHERE rvba.active = 1
//    )
//    SELECT COUNT(*)
//    FROM reeler r
//    LEFT JOIN PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
//    LEFT JOIN reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
//    WHERE r.active = 1 AND
//          (:districtId IS NULL OR r.DISTRICT_ID = :districtId) AND
//          (:talukId IS NULL OR r.TALUK_ID = :talukId) AND
//          (:villageId IS NULL OR r.VILLAGE_ID = :villageId) AND
//          (:marketId IS NULL OR rvba.market_master_id = :marketId)
//""")
//    Page<Object[]> getPrimaryReelerDetails(
//            @Param("districtId") Long districtId,
//            @Param("talukId") Long talukId,
//            @Param("villageId") Long villageId,
//            @Param("marketId") Long marketId,
//            Pageable pageable);

    @Query(nativeQuery = true, value = """
    WITH PrimaryAddress AS (
        SELECT rvba.reeler_id,
               ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
        FROM reeler_virtual_bank_account rvba
        WHERE rvba.active = 1
    ),
    FilteredReeler AS (
        SELECT r.reeler_id
        FROM reeler r
        LEFT JOIN PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
        LEFT JOIN reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
        WHERE r.active = 1
          AND (:districtId IS NULL OR r.DISTRICT_ID = :districtId)
          AND (:talukId IS NULL OR r.TALUK_ID = :talukId)
          AND (:villageId IS NULL OR r.VILLAGE_ID = :villageId)
          AND (:marketId IS NULL OR rvba.market_master_id = :marketId)
    )
    SELECT
        r.reeler_id,
        r.name,
        r.fruits_id,
        r.reeling_license_number,
        r.father_name,
        r.passbook_number,
        r.reeler_number,
        r.ration_card,
        r.dob,
        d.DISTRICT_NAME,
        t.TALUK_NAME,
        h.hobli_name,
        v.village_name,
        r.bank_name,
        r.bank_account_number,
        r.branch_name,
        r.ifsc_code
    FROM reeler r
    LEFT JOIN FilteredReeler fr ON fr.reeler_id = r.reeler_id
    LEFT JOIN district d ON r.DISTRICT_ID = d.DISTRICT_ID AND d.active = 1
    LEFT JOIN taluk t ON r.TALUK_ID = t.TALUK_ID AND t.active = 1
    LEFT JOIN hobli h ON r.HOBLI_ID = h.HOBLI_ID AND h.active = 1
    LEFT JOIN village v ON r.VILLAGE_ID = v.VILLAGE_ID AND v.active = 1
    WHERE r.active = 1
    AND fr.reeler_id IS NOT NULL
""", countQuery = """
    WITH PrimaryAddress AS (
        SELECT rvba.reeler_id,
               ROW_NUMBER() OVER (PARTITION BY rvba.reeler_id ORDER BY rvba.market_master_id DESC) AS rn
        FROM reeler_virtual_bank_account rvba
        WHERE rvba.active = 1
    ),
    FilteredReeler AS (
        SELECT r.reeler_id
        FROM reeler r
        LEFT JOIN PrimaryAddress pa ON pa.reeler_id = r.reeler_id AND pa.rn = 1
        LEFT JOIN reeler_virtual_bank_account rvba ON rvba.reeler_id = r.reeler_id
        WHERE r.active = 1
          AND (:districtId IS NULL OR r.DISTRICT_ID = :districtId)
          AND (:talukId IS NULL OR r.TALUK_ID = :talukId)
          AND (:villageId IS NULL OR r.VILLAGE_ID = :villageId)
          AND (:marketId IS NULL OR rvba.market_master_id = :marketId)
    )
    SELECT COUNT(DISTINCT fr.reeler_id)
    FROM FilteredReeler fr
""")
    Page<Object[]> getPrimaryReelerDetails(
            @Param("districtId") Long districtId,
            @Param("talukId") Long talukId,
            @Param("villageId") Long villageId,
            @Param("marketId") Long marketId,
            Pageable pageable);

}
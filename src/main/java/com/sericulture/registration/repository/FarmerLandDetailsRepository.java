package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.entity.FarmerLandDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerLandDetailsRepository extends PagingAndSortingRepository<FarmerLandDetails, Long> {

    public Page<FarmerLandDetails> findByActiveOrderByFarmerLandDetailsIdAsc(boolean isActive, final Pageable pageable);

    public FarmerLandDetails save(FarmerLandDetails farmerLandDetails);

    public FarmerLandDetails findByFarmerLandDetailsIdAndActive(long id, boolean isActive);

    public FarmerLandDetails findByFarmerLandDetailsIdAndActiveIn(@Param("farmerLandDetailsId") long farmerLandDetailsId, @Param("active") Set<Boolean> active);

    public List<FarmerLandDetails> findByFarmerIdAndActive(long farmerId, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO(" +
            " farmerLandDetails.farmerLandDetailsId, " +
            " farmerLandDetails.farmerId, " +
            " farmerLandDetails.categoryNumber, " +
            " farmerLandDetails.landOwnershipId, " +
            " farmerLandDetails.soilTypeId, " +
            " farmerLandDetails.hissa, " +
            " farmerLandDetails.mulberrySourceId, " +
            " farmerLandDetails.mulberryArea, " +
            " farmerLandDetails.mulberryVarietyId, " +
            " farmerLandDetails.plantationDate, " +
            " farmerLandDetails.spacing, " +
            " farmerLandDetails.plantationTypeId, " +
            " farmerLandDetails.irrigationSourceId, " +
            " farmerLandDetails.irrigationTypeId, " +
            " farmerLandDetails.rearingHouseDetails, " +
            " farmerLandDetails.roofTypeId, " +
            " farmerLandDetails.silkWormVarietyId, " +
            " farmerLandDetails.rearingCapacityCrops, " +
            " farmerLandDetails.rearingCapacityDlf, " +
            " farmerLandDetails.subsidyAvailed, " +
            " farmerLandDetails.subsidyId, " +
            " farmerLandDetails.loanDetails, " +
            " farmerLandDetails.equipmentDetails, " +
            " farmerLandDetails.gpsLat, " +
            " farmerLandDetails.gpsLng, " +
            " farmerLandDetails.surveyNumber, " +
            " farmerLandDetails.stateId, " +
            " farmerLandDetails.districtId, " +
            " farmerLandDetails.talukId, " +
            " farmerLandDetails.hobliId, " +
            " farmerLandDetails.villageId, " +
            " farmerLandDetails.address, " +
            " farmerLandDetails.pincode, " +
            " farmerLandDetails.ownerName, " +
            " farmerLandDetails.surNoc, " +
            " farmerLandDetails.nameScore, " +
            " farmerLandDetails.ownerNo, " +
            " farmerLandDetails.mainOwnerNo, " +
            " farmerLandDetails.acre, " +
            " farmerLandDetails.gunta, " +
            " farmerLandDetails.fGunta, " +
            " landOwnership.landOwnershipName, " +
            " soilType.soilTypeName, " +
            " mulberrySource.mulberrySourceName, " +
            " mulberryVariety.mulberryVarietyName, " +
            " plantationType.plantationTypeName, " +
            " irrigationSource.irrigationSourceName, " +
            " irrigationType.irrigationTypeName, " +
            " roofType.roofTypeName, " +
            " silkWormVariety.silkWormVarietyName, " +
            " subsidy.subsidyName, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName" +
            ") " +
            "from FarmerLandDetails farmerLandDetails " +
            "left join LandOwnership landOwnership " +
            "on farmerLandDetails.landOwnershipId = landOwnership.landOwnershipId " +
            "left join SoilType soilType " +
            "on farmerLandDetails.soilTypeId = soilType.soilTypeId " +
            "left join MulberrySource mulberrySource " +
            "on farmerLandDetails.mulberrySourceId = mulberrySource.mulberrySourceId " +
            "left join MulberryVariety mulberryVariety " +
            "on farmerLandDetails.mulberryVarietyId = mulberryVariety.mulberryVarietyId " +
            "left join PlantationType plantationType " +
            "on farmerLandDetails.plantationTypeId = plantationType.plantationTypeId " +
            "left join IrrigationSource irrigationSource " +
            "on farmerLandDetails.irrigationSourceId = irrigationSource.irrigationSourceId " +
            "left join IrrigationType irrigationType " +
            "on farmerLandDetails.irrigationTypeId = irrigationType.irrigationTypeId " +
            "left join RoofType roofType " +
            "on farmerLandDetails.roofTypeId = roofType.roofTypeId " +
            "left join SilkWormVariety silkWormVariety " +
            "on farmerLandDetails.silkWormVarietyId = silkWormVariety.silkWormVarietyId " +
            "left join subsidy_master subsidy " +
            "on farmerLandDetails.subsidyId = subsidy.subsidyId " +
            "left join State state " +
            "on farmerLandDetails.stateId = state.stateId " +
            "left join District district " +
            "on farmerLandDetails.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on farmerLandDetails.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on farmerLandDetails.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on farmerLandDetails.villageId = village.villageId " +
            "where farmerLandDetails.active = :isActive AND farmerLandDetails.farmerLandDetailsId = :id")
    public FarmerLandDetailsDTO getByFarmerLandDetailsIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO(" +
            " farmerLandDetails.farmerLandDetailsId, " +
            " farmerLandDetails.farmerId, " +
            " farmerLandDetails.categoryNumber, " +
            " farmerLandDetails.landOwnershipId, " +
            " farmerLandDetails.soilTypeId, " +
            " farmerLandDetails.hissa, " +
            " farmerLandDetails.mulberrySourceId, " +
            " farmerLandDetails.mulberryArea, " +
            " farmerLandDetails.mulberryVarietyId, " +
            " farmerLandDetails.plantationDate, " +
            " farmerLandDetails.spacing, " +
            " farmerLandDetails.plantationTypeId, " +
            " farmerLandDetails.irrigationSourceId, " +
            " farmerLandDetails.irrigationTypeId, " +
            " farmerLandDetails.rearingHouseDetails, " +
            " farmerLandDetails.roofTypeId, " +
            " farmerLandDetails.silkWormVarietyId, " +
            " farmerLandDetails.rearingCapacityCrops, " +
            " farmerLandDetails.rearingCapacityDlf, " +
            " farmerLandDetails.subsidyAvailed, " +
            " farmerLandDetails.subsidyId, " +
            " farmerLandDetails.loanDetails, " +
            " farmerLandDetails.equipmentDetails, " +
            " farmerLandDetails.gpsLat, " +
            " farmerLandDetails.gpsLng, " +
            " farmerLandDetails.surveyNumber, " +
            " farmerLandDetails.stateId, " +
            " farmerLandDetails.districtId, " +
            " farmerLandDetails.talukId, " +
            " farmerLandDetails.hobliId, " +
            " farmerLandDetails.villageId, " +
            " farmerLandDetails.address, " +
            " farmerLandDetails.pincode, " +
            " farmerLandDetails.ownerName, " +
            " farmerLandDetails.surNoc, " +
            " farmerLandDetails.nameScore, " +
            " farmerLandDetails.ownerNo, " +
            " farmerLandDetails.mainOwnerNo, " +
            " farmerLandDetails.acre, " +
            " farmerLandDetails.gunta, " +
            " farmerLandDetails.fGunta, " +
            " landOwnership.landOwnershipName, " +
            " soilType.soilTypeName, " +
            " mulberrySource.mulberrySourceName, " +
            " mulberryVariety.mulberryVarietyName, " +
            " plantationType.plantationTypeName, " +
            " irrigationSource.irrigationSourceName, " +
            " irrigationType.irrigationTypeName, " +
            " roofType.roofTypeName, " +
            " silkWormVariety.silkWormVarietyName, " +
            " subsidy.subsidyName, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName" +
            ") " +
            "from FarmerLandDetails farmerLandDetails " +
            "left join LandOwnership landOwnership " +
            "on farmerLandDetails.landOwnershipId = landOwnership.landOwnershipId " +
            "left join SoilType soilType " +
            "on farmerLandDetails.soilTypeId = soilType.soilTypeId " +
            "left join MulberrySource mulberrySource " +
            "on farmerLandDetails.mulberrySourceId = mulberrySource.mulberrySourceId " +
            "left join MulberryVariety mulberryVariety " +
            "on farmerLandDetails.mulberryVarietyId = mulberryVariety.mulberryVarietyId " +
            "left join PlantationType plantationType " +
            "on farmerLandDetails.plantationTypeId = plantationType.plantationTypeId " +
            "left join IrrigationSource irrigationSource " +
            "on farmerLandDetails.irrigationSourceId = irrigationSource.irrigationSourceId " +
            "left join IrrigationType irrigationType " +
            "on farmerLandDetails.irrigationTypeId = irrigationType.irrigationTypeId " +
            "left join RoofType roofType " +
            "on farmerLandDetails.roofTypeId = roofType.roofTypeId " +
            "left join SilkWormVariety silkWormVariety " +
            "on farmerLandDetails.silkWormVarietyId = silkWormVariety.silkWormVarietyId " +
            "left join subsidy_master subsidy " +
            "on farmerLandDetails.subsidyId = subsidy.subsidyId " +
            "left join State state " +
            "on farmerLandDetails.stateId = state.stateId " +
            "left join District district " +
            "on farmerLandDetails.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on farmerLandDetails.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on farmerLandDetails.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on farmerLandDetails.villageId = village.villageId " +
            "where farmerLandDetails.active = :isActive AND farmerLandDetails.farmerId = :id")
    public List<FarmerLandDetailsDTO> getByFarmerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);


//    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO(" +
//            " farmerLandDetails.farmerLandDetailsId, " +
//            " farmerLandDetails.farmerId, " +
//            " farmerLandDetails.categoryNumber, " +
//            " farmerLandDetails.landOwnershipId, " +
//            " farmerLandDetails.soilTypeId, " +
//            " farmerLandDetails.hissa, " +
//            " farmerLandDetails.mulberrySourceId, " +
//            " farmerLandDetails.mulberryArea, " +
//            " farmerLandDetails.mulberryVarietyId, " +
//            " farmerLandDetails.plantationDate, " +
//            " farmerLandDetails.spacing, " +
//            " farmerLandDetails.plantationTypeId, " +
//            " farmerLandDetails.irrigationSourceId, " +
//            " farmerLandDetails.irrigationTypeId, " +
//            " farmerLandDetails.rearingHouseDetails, " +
//            " farmerLandDetails.roofTypeId, " +
//            " farmerLandDetails.silkWormVarietyId, " +
//            " farmerLandDetails.rearingCapacityCrops, " +
//            " farmerLandDetails.rearingCapacityDlf, " +
//            " farmerLandDetails.subsidyAvailed, " +
//            " farmerLandDetails.subsidyId, " +
//            " farmerLandDetails.loanDetails, " +
//            " farmerLandDetails.equipmentDetails, " +
//            " farmerLandDetails.gpsLat, " +
//            " farmerLandDetails.gpsLng, " +
//            " farmerLandDetails.surveyNumber, " +
//            " farmerLandDetails.stateId, " +
//            " farmerLandDetails.districtId, " +
//            " farmerLandDetails.talukId, " +
//            " farmerLandDetails.hobliId, " +
//            " farmerLandDetails.villageId, " +
//            " farmerLandDetails.address, " +
//            " farmerLandDetails.pincode, " +
//            " farmerLandDetails.ownerName, " +
//            " farmerLandDetails.surNoc, " +
//            " farmerLandDetails.nameScore, " +
//            " farmerLandDetails.ownerNo, " +
//            " farmerLandDetails.mainOwnerNo, " +
//            " farmerLandDetails.acre, " +
//            " farmerLandDetails.gunta, " +
//            " farmerLandDetails.fGunta, " +
//            " state.stateName, " +
//            " district.districtName, " +
//            " taluk.talukName, " +
//            " hobli.hobliName, " +
//            " village.villageName" +
//            ") " +
//            "from FarmerLandDetails farmerLandDetails " +
//            "left join State state " +
//            "on farmerLandDetails.stateId = state.stateId " +
//            "left join District district " +
//            "on farmerLandDetails.districtId = district.districtId " +
//            "left join Taluk taluk " +
//            "on farmerLandDetails.talukId = taluk.talukId " +
//            "left join Hobli hobli " +
//            "on farmerLandDetails.hobliId = hobli.hobliId " +
//            "left join Village village " +
//            "on farmerLandDetails.villageId = village.villageId " +
//            "where farmerLandDetails.active = :isActive AND farmerLandDetails.farmerId = :farmerId")
//    public List<FarmerLandDetailsDTO> getByFarmerIdAndActive(@Param("farmerId") long farmerId, @Param("isActive") boolean isActive);

}
package com.sericulture.registration.repository;

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
            " farmerLandDetails.landOwnerShipId, " +
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
            " farmerLandDetails.subsidyMasterId, " +
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
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName" +
            ") " +
            "from FarmerLandDetails farmerLandDetails " +
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
            "where farmerLandDetails.active = :isActive AND farmerLandDetails.farmerId = :farmerId")
    public List<FarmerLandDetailsDTO> getByFarmerIdAndActive(@Param("farmerId") long farmerId, @Param("isActive") boolean isActive);

}
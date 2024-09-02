package com.sericulture.registration.repository;

import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.entity.FarmerAddress;
import com.sericulture.registration.model.entity.FarmerFamily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FarmerAddressRepository extends PagingAndSortingRepository<FarmerAddress, Long> {
    public Page<FarmerAddress> findByActiveOrderByFarmerAddressIdAsc(boolean isActive, final Pageable pageable);

    public FarmerAddress save(FarmerAddress farmerAddress);

    public FarmerAddress findByFarmerAddressIdAndActive(long id, boolean isActive);

    public FarmerAddress findByFarmerAddressIdAndActiveIn(@Param("farmerAddressId") long farmerAddressId, @Param("active") Set<Boolean> active);

    List<FarmerAddress> findByFarmerIdAndActive(long id, boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerAddressDTO(" +
            " farmerAddress.farmerAddressId, " +
            " farmerAddress.farmerId, " +
            " farmerAddress.addressText, " +
            " farmerAddress.pincode, " +
            " farmerAddress.defaultAddress, " +
            " farmerAddress.stateId, " +
            " farmerAddress.districtId, " +
            " farmerAddress.talukId, " +
            " farmerAddress.hobliId, " +
            " farmerAddress.villageId, " +
            " farmerAddress.taluk, " +
            " farmerAddress.district, " +
            " farmerAddress.village, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName" +
            ") " +
            "from FarmerAddress farmerAddress " +
            "left join State state " +
            "on farmerAddress.stateId = state.stateId " +
            "left join District district " +
            "on farmerAddress.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on farmerAddress.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on farmerAddress.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on farmerAddress.villageId = village.villageId " +
            "where farmerAddress.active = :isActive AND farmerAddress.farmerAddressId = :id")
    public FarmerAddressDTO getByFarmerAddressIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

    @Query("select new com.sericulture.registration.model.dto.farmer.FarmerAddressDTO(" +
            " farmerAddress.farmerAddressId, " +
            " farmerAddress.farmerId, " +
            " farmerAddress.addressText, " +
            " farmerAddress.pincode, " +
            " farmerAddress.defaultAddress, " +
            " farmerAddress.stateId, " +
            " farmerAddress.districtId, " +
            " farmerAddress.talukId, " +
            " farmerAddress.hobliId, " +
            " farmerAddress.villageId, " +
            " farmerAddress.taluk, " +
            " farmerAddress.district, " +
            " farmerAddress.village, " +
            " state.stateName, " +
            " district.districtName, " +
            " taluk.talukName, " +
            " hobli.hobliName, " +
            " village.villageName" +
            ") " +
            "from FarmerAddress farmerAddress " +
            "left join State state " +
            "on farmerAddress.stateId = state.stateId " +
            "left join District district " +
            "on farmerAddress.districtId = district.districtId " +
            "left join Taluk taluk " +
            "on farmerAddress.talukId = taluk.talukId " +
            "left join Hobli hobli " +
            "on farmerAddress.hobliId = hobli.hobliId " +
            "left join Village village " +
            "on farmerAddress.villageId = village.villageId " +
            "where farmerAddress.active = :isActive AND farmerAddress.farmerId = :id")
    public List <FarmerAddressDTO> getByFarmerIdAndActive(@Param("id") long id, @Param("isActive") boolean isActive);

}
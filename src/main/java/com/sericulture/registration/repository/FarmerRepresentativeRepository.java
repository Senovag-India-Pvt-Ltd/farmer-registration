package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerRepresentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepresentativeRepository extends JpaRepository<FarmerRepresentative, Long> {

    List<FarmerRepresentative> findByFarmerIdAndActiveOrderByFarmerRepresentativeIdAsc(Long farmerId, Boolean active);

    long countByFarmerIdAndActive(Long farmerId, Boolean active);

    Optional<FarmerRepresentative> findByFarmerRepresentativeIdAndActive(Long farmerRepresentativeId, Boolean active);

    /**
     * Locks the farmer row until the transaction ends, so two parallel "add" calls for the same farmer cannot both
     * pass the maximum check. Empty when the farmer does not exist or is inactive.
     */
    @Query(nativeQuery = true, value = "select f.FARMER_ID from farmer f with (updlock, rowlock) where f.FARMER_ID = :farmerId and f.ACTIVE = 1")
    List<Object> lockActiveFarmer(@Param("farmerId") Long farmerId);

    /** Relationship names for display (relationship master). 0 relationship_id, 1 relationship_name. */
    @Query(nativeQuery = true, value = "select RELATIONSHIP_ID, RELATIONSHIP_NAME from relationship where RELATIONSHIP_ID in (:ids)")
    List<Object[]> relationshipNames(@Param("ids") List<Long> ids);

    /**
     * Agent lookup by mobile number (exact) or name (contains), with the farmer each agent is linked to.
     * 0 farmer_representative_id, 1 name, 2 mobile_number, 3 address, 4 relationship_id, 5 relationship name,
     * 6 authorised_from, 7 authorised_to, 8 FARMER_ID, 9 farmer name, 10 fruits_id, 11 farmer_number, 12 farmer mobile
     */
    @Query(nativeQuery = true, value = """
            select top 100 r.farmer_representative_id, r.name, r.mobile_number, r.address, r.relationship_id,
                   rel.RELATIONSHIP_NAME, r.authorised_from, r.authorised_to, f.FARMER_ID,
                   ltrim(rtrim(concat(f.first_name, ' ', f.middle_name, ' ', f.last_name))), f.fruits_id, f.farmer_number,
                   f.mobile_number
            from farmer_representative r
            join farmer f on f.FARMER_ID = r.farmer_id and f.ACTIVE = 1
            left join relationship rel on rel.RELATIONSHIP_ID = r.relationship_id
            where r.ACTIVE = 1
              and ((:mobile <> '' and r.mobile_number = :mobile) or (:name <> '' and r.name like concat('%', :name, '%')))
            order by r.name, r.farmer_representative_id""")
    List<Object[]> search(@Param("mobile") String mobile, @Param("name") String name);
}

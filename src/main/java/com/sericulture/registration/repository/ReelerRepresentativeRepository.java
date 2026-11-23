package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.ReelerRepresentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReelerRepresentativeRepository extends JpaRepository<ReelerRepresentative, Long> {

    List<ReelerRepresentative> findByReelerIdAndActiveOrderByReelerRepresentativeIdAsc(Long reelerId, Boolean active);

    long countByReelerIdAndActive(Long reelerId, Boolean active);

    Optional<ReelerRepresentative> findByReelerRepresentativeIdAndActive(Long reelerRepresentativeId, Boolean active);

    /**
     * Locks the reeler row until the transaction ends, so two parallel "add" calls for the same reeler cannot both
     * pass the maximum check. Empty when the reeler does not exist or is inactive.
     */
    @Query(nativeQuery = true, value = "select r.REELER_ID from reeler r with (updlock, rowlock) where r.REELER_ID = :reelerId and r.ACTIVE = 1")
    List<Object> lockActiveReeler(@Param("reelerId") Long reelerId);

    /** Relationship names for display (relationship master). 0 relationship_id, 1 relationship_name. */
    @Query(nativeQuery = true, value = "select RELATIONSHIP_ID, RELATIONSHIP_NAME from relationship where RELATIONSHIP_ID in (:ids)")
    List<Object[]> relationshipNames(@Param("ids") List<Long> ids);
}

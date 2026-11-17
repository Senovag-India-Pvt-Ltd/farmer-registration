package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.FarmerChangeHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FarmerChangeHistoryRepository extends CrudRepository<FarmerChangeHistory, Long> {

    List<FarmerChangeHistory> findByFarmerIdOrderByChangedDateDescIdDesc(Long farmerId);
}

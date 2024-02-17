package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.SerialCounter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerialCounterRepository extends PagingAndSortingRepository<SerialCounter, Long> {
    public SerialCounter save(SerialCounter serialCounter);

    public List<SerialCounter> findByActive(boolean isActive);
}
package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.RequestInspectionMapping;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestInspectionMappingRepository extends PagingAndSortingRepository<RequestInspectionMapping, Long> {

    RequestInspectionMapping findByRequestTypeNameAndActive(String name, boolean active);

    public RequestInspectionMapping save(RequestInspectionMapping RequestInspectionMapping);


    public List<RequestInspectionMapping> findByActive(boolean isActive);
}

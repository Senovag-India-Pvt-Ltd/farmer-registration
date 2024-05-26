package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.InspectionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface InspectionTaskRepository extends PagingAndSortingRepository<InspectionTask,Long> {

    public InspectionTask save(InspectionTask inspectionTask);

    public List<InspectionTask> findByActive(boolean isActive);

}

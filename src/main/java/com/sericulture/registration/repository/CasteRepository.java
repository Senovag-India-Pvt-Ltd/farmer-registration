package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Caste;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CasteRepository extends PagingAndSortingRepository<Caste, Long> {
    public Caste findByTitleAndActive(String title,boolean isActive);

    public Caste save(Caste caste);
}

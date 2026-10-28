package com.sericulture.registration.sevasindhu;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevaSindhuIntegrationLogRepository extends PagingAndSortingRepository<SevaSindhuIntegrationLog, Long> {

    public SevaSindhuIntegrationLog save(SevaSindhuIntegrationLog log);
}

package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StateRepository extends PagingAndSortingRepository<State,Long> {
    public List<State> findByStateNameAndStateNameInKannada(String stateName, String stateNameInKannada);

    public List<State> findByActiveAndStateNameAndStateNameInKannada(boolean a,String stateName,String stateNameInKannada);

    public State findByStateNameAndActive(String stateName,boolean isActive);

    public Page<State> findByActiveOrderByStateNameAsc(boolean isActive, final Pageable pageable);

    public State save(State state);

    public State findByStateIdAndActive(long id, boolean isActive);

    public State findByStateIdAndActiveIn(@Param("stateId") long stateId, @Param("active") Set<Boolean> active);

    public List<State> findByActiveOrderByStateNameAsc(boolean isActive);
}

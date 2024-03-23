package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.MulberryVariety;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
@Repository
public interface MulberryVarietyRepository extends PagingAndSortingRepository <MulberryVariety, Long> {
    public List<MulberryVariety> findByMulberryVarietyNameAndMulberryVarietyNameInKannada(String mulberryVarietyName, String mulberryVarietyNameInKannada);

    public List<MulberryVariety> findByActiveAndMulberryVarietyNameAndMulberryVarietyNameInKannada(boolean a,String mulberryVarietyName,String mulberryVarietyNameInKannada);

    public MulberryVariety findByMulberryVarietyNameAndActive(String mulberryVarietyName,boolean isActive);

    public Page<MulberryVariety> findByActiveOrderByMulberryVarietyNameAsc(boolean isActive, final Pageable pageable);

    public MulberryVariety save(MulberryVariety mulberryVariety);

    public MulberryVariety findByMulberryVarietyIdAndActive(long id, boolean isActive);

    public MulberryVariety findByMulberryVarietyIdAndActiveIn(@Param("mulberryVarietyId") long mulberryVarietyId, @Param("active") Set<Boolean> active);

    public List<MulberryVariety> findByActive(boolean isActive);
}

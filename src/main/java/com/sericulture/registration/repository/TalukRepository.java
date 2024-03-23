package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Taluk;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TalukRepository extends PagingAndSortingRepository<Taluk, Long> {

    public List<Taluk> findByTalukNameAndTalukNameInKannada(String talukName, String talukNameInKannada);

    public List<Taluk> findByActiveAndTalukNameAndTalukNameInKannada(boolean a,String talukName,String talukNameInKannada);

    Taluk findByTalukNameAndDistrictIdAndActive(String talukName, long districtId, boolean a);
    public List<Taluk> findByTalukNameAndDistrictId(String talukName, long districtId);

    public Taluk findByTalukNameAndActive(String talukName,boolean isActive);
}

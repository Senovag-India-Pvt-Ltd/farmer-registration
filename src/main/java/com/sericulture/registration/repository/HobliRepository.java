package com.sericulture.registration.repository;

import com.sericulture.registration.model.entity.Hobli;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HobliRepository extends PagingAndSortingRepository<Hobli,Long> {

    public List<Hobli> findByHobliNameAndHobliNameInKannada(String hobliName, String hobliNameInKannada);

    public List<Hobli> findByActiveAndHobliNameAndHobliNameInKannada(boolean active,String hobliName,String hobliNameInKannada);

    Hobli findByHobliNameAndTalukIdAndActive(String hobliName, long talukId, boolean a);
    public List<Hobli> findByHobliName(String hobliName);

    public List<Hobli> findByHobliNameAndTalukId(String hobliName, long talukId);

    public Hobli findByHobliNameAndActive(String hobliName,boolean isActive);

    public Page<Hobli> findByActiveOrderByHobliIdAsc(boolean isActive, final Pageable pageable);

    public Hobli save(Hobli hobli);

    public Hobli findByHobliIdAndActive(long id, boolean isActive);

    public Hobli findByTalukIdAndHobliCodeAndActive(long id, String hobliCode, boolean active);

}

package com.sericulture.registration.service;

import com.sericulture.registration.model.api.reelerVirtualBankAccount.EditReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.entity.ReelerVirtualBankAccount;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ReelerVirtualBankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReelerVirtualBankAccountService {

    @Autowired
    ReelerVirtualBankAccountRepository reelerVirtualBankAccountRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public ReelerVirtualBankAccountResponse insertReelerVirtualBankAccountDetails(ReelerVirtualBankAccountRequest reelerVirtualBankAccountRequest){
        ReelerVirtualBankAccount reelerVirtualBankAccount = mapper.reelerVirtualBankAccountObjectToEntity(reelerVirtualBankAccountRequest,ReelerVirtualBankAccount.class);
        validator.validate(reelerVirtualBankAccount);
       /* List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountName(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountName());
        if(!reelerVirtualBankAccountList.isEmpty() && reelerVirtualBankAccountList.stream().filter(ReelerVirtualBankAccount::getActive).findAny().isPresent()){
            throw new ValidationException("ReelerVirtualBankAccount name already exist");
        }
        if(!reelerVirtualBankAccountList.isEmpty() && reelerVirtualBankAccountList.stream().filter(Predicate.not(ReelerVirtualBankAccount::getActive)).findAny().isPresent()){
            throw new ValidationException("ReelerVirtualBankAccount name already exist with inactive state");
        }*/
        return mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount),ReelerVirtualBankAccountResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedReelerVirtualBankAccountDetails(final Pageable pageable){
        return convertToMapResponse(reelerVirtualBankAccountRepository.findByActiveOrderByReelerVirtualBankAccountIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<ReelerVirtualBankAccount> activeReelerVirtualBankAccounts) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerVirtualBankAccountResponse> reelerVirtualBankAccountResponses = activeReelerVirtualBankAccounts.getContent().stream()
                .map(reelerVirtualBankAccount -> mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount,ReelerVirtualBankAccountResponse.class)).collect(Collectors.toList());
        response.put("reelerVirtualBankAccount",reelerVirtualBankAccountResponses);
        response.put("currentPage", activeReelerVirtualBankAccounts.getNumber());
        response.put("totalItems", activeReelerVirtualBankAccounts.getTotalElements());
        response.put("totalPages", activeReelerVirtualBankAccounts.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteReelerVirtualBankAccountDetails(long id) {
        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActive(id, true);
        if (Objects.nonNull(reelerVirtualBankAccount)) {
            reelerVirtualBankAccount.setActive(false);
            reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public ReelerVirtualBankAccountResponse getById(int id){
        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActive(id,true);
        if(reelerVirtualBankAccount == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",reelerVirtualBankAccount);
        return mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount,ReelerVirtualBankAccountResponse.class);
    }

    @Transactional
    public ReelerVirtualBankAccountResponse getByReelerId(int id){
        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerId(id);
        if(reelerVirtualBankAccount == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",reelerVirtualBankAccount);
        return mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount,ReelerVirtualBankAccountResponse.class);
    }

    @Transactional
    public ReelerVirtualBankAccountResponse updateReelerVirtualBankAccountDetails(EditReelerVirtualBankAccountRequest reelerVirtualBankAccountRequest){
       /* List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountName(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountName());
        if(reelerVirtualBankAccountList.size()>0){
            throw new ValidationException("ReelerVirtualBankAccount already exists with this name, duplicates are not allowed.");
        }*/

        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActiveIn(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountId(), Set.of(true,false));
        if(Objects.nonNull(reelerVirtualBankAccount)){
            reelerVirtualBankAccount.setReelerVirtualBankAccountId(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountId());
            reelerVirtualBankAccount.setReelerId(reelerVirtualBankAccountRequest.getReelerId());
            reelerVirtualBankAccount.setVirtualAccountNumber(reelerVirtualBankAccountRequest.getVirtualAccountNumber());
            reelerVirtualBankAccount.setBranchName(reelerVirtualBankAccountRequest.getBranchName());
            reelerVirtualBankAccount.setIfscCode(reelerVirtualBankAccountRequest.getIfscCode());
            reelerVirtualBankAccount.setMarketMasterId(reelerVirtualBankAccountRequest.getMarketMasterId());
            reelerVirtualBankAccount.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching reelerVirtualBankAccount");
        }
        return mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount),ReelerVirtualBankAccountResponse.class);
    }

}
package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerBankAccount.EditFarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerBankAccountRepository;
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
public class FarmerBankAccountService {

    @Autowired
    FarmerBankAccountRepository farmerBankAccountRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FarmerBankAccountResponse getFarmerBankAccountDetails(String farmerBankAccountNumber) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = null;
        if (farmerBankAccount == null) {
            farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountNumberAndActive(farmerBankAccountNumber, true);
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        } else {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("FarmerBankAccount not found");
        }
        log.info("Entity is ", farmerBankAccount);
        return farmerBankAccountResponse;
    }

    @Transactional
    public FarmerBankAccountResponse insertFarmerBankAccountDetails(FarmerBankAccountRequest farmerBankAccountRequest) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = mapper.farmerBankAccountObjectToEntity(farmerBankAccountRequest, FarmerBankAccount.class);
        validator.validate(farmerBankAccount);
        List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerBankAccountRequest.getFarmerBankAccountNumber());
        if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("FarmerBankAccount name already exist");
        } else if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(Predicate.not(FarmerBankAccount::getActive)).findAny().isPresent()) {
            //throw new ValidationException("Village name already exist with inactive state");
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("FarmerBankAccount name already exist with inactive state");
        } else {
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccountRepository.save(farmerBankAccount), FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        }
        return farmerBankAccountResponse;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getPaginatedFarmerBankAccountDetails(final Pageable pageable) {
        return convertToMapResponse(farmerBankAccountRepository.findByActiveOrderByFarmerBankAccountIdAsc(true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerBankAccount> activeFarmerBankAccounts) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerBankAccountResponse> farmerBankAccountResponses = activeFarmerBankAccounts.getContent().stream()
                .map(farmerBankAccount -> mapper.farmerBankAccountEntityToObject(farmerBankAccount, FarmerBankAccountResponse.class)).collect(Collectors.toList());
        response.put("farmerBankAccount", farmerBankAccountResponses);
        response.put("currentPage", activeFarmerBankAccounts.getNumber());
        response.put("totalItems", activeFarmerBankAccounts.getTotalElements());
        response.put("totalPages", activeFarmerBankAccounts.getTotalPages());

        return response;
    }

    @Transactional
    public FarmerBankAccountResponse deleteFarmerBankAccountDetails(long id) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActive(id, true);
        if (Objects.nonNull(farmerBankAccount)) {
            farmerBankAccount.setActive(false);
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccountRepository.save(farmerBankAccount), FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        } else {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerBankAccountResponse;
    }

    @Transactional
    public FarmerBankAccountResponse getById(int id) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActive(id, true);
        if (farmerBankAccount == null) {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Invalid id");
        } else {
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        }
        log.info("Entity is ", farmerBankAccount);
        return farmerBankAccountResponse;
    }

    @Transactional
    public FarmerBankAccountResponse getByFarmerId(int id) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(id, true);
        if (farmerBankAccount == null) {
//            throw new ValidationException("Invalid Id");
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Invalid id");
        } else {
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        }
        log.info("Entity is ", farmerBankAccount);
        return farmerBankAccountResponse;
    }

    @Transactional
    public FarmerBankAccountResponse updateFarmerBankAccountDetails(EditFarmerBankAccountRequest farmerBankAccountRequest) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
//        List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerBankAccountRequest.getFarmerBankAccountNumber());
//        if(farmerBankAccountList.size()>0){
//            throw new ValidationException("FarmerBankAccount already exists with this name, duplicates are not allowed.");
//        }

        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActiveIn(farmerBankAccountRequest.getFarmerBankAccountId(), Set.of(true, false));
        if (Objects.nonNull(farmerBankAccount)) {
            farmerBankAccount.setFarmerBankBranchName(farmerBankAccountRequest.getFarmerBankBranchName());
            farmerBankAccount.setFarmerBankIfscCode(farmerBankAccountRequest.getFarmerBankIfscCode());
            farmerBankAccount.setFarmerBankName(farmerBankAccountRequest.getFarmerBankName());
            farmerBankAccount.setFarmerId(farmerBankAccountRequest.getFarmerId());
            farmerBankAccount.setFarmerBankAccountId(farmerBankAccountRequest.getFarmerBankAccountId());
            farmerBankAccount.setActive(true);
            FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerBankAccount);
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount1, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        } else {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Error occurred while fetching farmerBankAccount");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerBankAccountResponse;
    }
}
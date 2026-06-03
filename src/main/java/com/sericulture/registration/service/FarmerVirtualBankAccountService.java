package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.EditFarmerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.FarmerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.FarmerVirtualBankAccountResponse;
import com.sericulture.registration.model.dto.farmer.FarmerVirtualBankAccountView;
import com.sericulture.registration.model.entity.FarmerVirtualBankAccount;
import com.sericulture.registration.model.entity.FarmerVirtualBankAccountAudit;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerVirtualBankAccountAuditRepository;
import com.sericulture.registration.repository.FarmerVirtualBankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerVirtualBankAccountService {

    @Autowired
    FarmerVirtualBankAccountRepository farmerVirtualBankAccountRepository;

    @Autowired
    FarmerVirtualBankAccountAuditRepository farmerVirtualBankAccountAuditRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public FarmerVirtualBankAccountResponse insertFarmerVirtualBankAccountDetails(FarmerVirtualBankAccountRequest farmerVirtualBankAccountRequest) {
        FarmerVirtualBankAccountResponse farmerVirtualBankAccountResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccount farmerVirtualBankAccount = mapper.farmerVirtualBankAccountObjectToEntity(farmerVirtualBankAccountRequest, FarmerVirtualBankAccount.class);
        validator.validate(farmerVirtualBankAccount);
        List<FarmerVirtualBankAccount> farmerVirtualBankAccountList = farmerVirtualBankAccountRepository.findByVirtualAccountNumber(farmerVirtualBankAccount.getVirtualAccountNumber());
        if (!farmerVirtualBankAccountList.isEmpty() && farmerVirtualBankAccountList.stream().filter(FarmerVirtualBankAccount::getActive).findAny().isPresent()) {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Farmer virtual bank account number already exist");
        } else if (!farmerVirtualBankAccountList.isEmpty() && farmerVirtualBankAccountList.stream().filter(Predicate.not(FarmerVirtualBankAccount::getActive)).findAny().isPresent()) {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Farmer virtual bank account number already exist with inactive state");
        } else {
            farmerVirtualBankAccountResponse = mapper.farmerVirtualBankAccountEntityToObject(farmerVirtualBankAccountRepository.save(farmerVirtualBankAccount), FarmerVirtualBankAccountResponse.class);
            farmerVirtualBankAccountResponse.setError(false);
        }
        return farmerVirtualBankAccountResponse;
    }

    public Map<String, Object> getPaginatedFarmerVirtualBankAccountDetails(final Pageable pageable) {
        return convertToMapResponse(farmerVirtualBankAccountRepository.findByActiveOrderByFarmerVirtualBankAccountIdAsc(true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<FarmerVirtualBankAccount> activeFarmerVirtualBankAccounts) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerVirtualBankAccountResponse> farmerVirtualBankAccountResponses = activeFarmerVirtualBankAccounts.getContent().stream()
                .map(farmerVirtualBankAccount -> mapper.farmerVirtualBankAccountEntityToObject(farmerVirtualBankAccount, FarmerVirtualBankAccountResponse.class))
                .collect(Collectors.toList());
        response.put("farmerVirtualBankAccount", farmerVirtualBankAccountResponses);
        response.put("currentPage", activeFarmerVirtualBankAccounts.getNumber());
        response.put("totalItems", activeFarmerVirtualBankAccounts.getTotalElements());
        response.put("totalPages", activeFarmerVirtualBankAccounts.getTotalPages());
        return response;
    }

    public Map<String, Object> getFarmersByMarketId(long marketId) {
        return convertViewListToMapResponse(farmerVirtualBankAccountRepository.getByFarmersByMarketId(marketId, true));
    }

    @Transactional
    public FarmerVirtualBankAccountResponse deleteFarmerVirtualBankAccountDetails(long id) {
        FarmerVirtualBankAccountResponse farmerVirtualBankAccountResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccount farmerVirtualBankAccount = farmerVirtualBankAccountRepository.findByFarmerVirtualBankAccountIdAndActive(id, true);
        if (Objects.nonNull(farmerVirtualBankAccount)) {
            farmerVirtualBankAccount.setActive(false);
            FarmerVirtualBankAccount savedAccount = farmerVirtualBankAccountRepository.save(farmerVirtualBankAccount);
            FarmerVirtualBankAccountAudit audit = new FarmerVirtualBankAccountAudit();
            audit.setFarmerVirtualBankAccountId(savedAccount.getFarmerVirtualBankAccountId());
            audit.setAction("DELETED");
            farmerVirtualBankAccountAuditRepository.save(audit);
            farmerVirtualBankAccountResponse = mapper.farmerVirtualBankAccountEntityToObject(savedAccount, FarmerVirtualBankAccountResponse.class);
            farmerVirtualBankAccountResponse.setError(false);
        } else {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Invalid Id");
        }
        return farmerVirtualBankAccountResponse;
    }

    public FarmerVirtualBankAccountResponse getById(int id) {
        FarmerVirtualBankAccountResponse farmerVirtualBankAccountResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccount farmerVirtualBankAccount = farmerVirtualBankAccountRepository.findByFarmerVirtualBankAccountIdAndActive(id, true);
        if (farmerVirtualBankAccount == null) {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Invalid id");
        } else {
            farmerVirtualBankAccountResponse = mapper.farmerVirtualBankAccountEntityToObject(farmerVirtualBankAccount, FarmerVirtualBankAccountResponse.class);
            farmerVirtualBankAccountResponse.setError(false);
        }
        log.info("Entity is ", farmerVirtualBankAccount);
        return farmerVirtualBankAccountResponse;
    }

    public Map<String, Object> getByFarmerId(int farmerId) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerVirtualBankAccount> farmerList = farmerVirtualBankAccountRepository.findByFarmerIdAndActive(farmerId, true);
        if (farmerList.isEmpty()) {
            response.put("error", "Error");
            response.put("error_description", "Invalid id");
            return response;
        } else {
            return convertListToMapResponse(farmerList);
        }
    }

    public FarmerVirtualBankAccountResponse getFarmerDetailsByFarmerNumberOrMobileNumber(GetFarmerRequest getFarmerRequest) throws Exception {
        FarmerVirtualBankAccountResponse farmerResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccountView farmerVirtualBankAccount;
        if (getFarmerRequest.getFruitsId() != null && !getFarmerRequest.getFruitsId().equals("")) {
            farmerVirtualBankAccount = farmerVirtualBankAccountRepository.getByFarmerByMarketIdAndFruitsId(getFarmerRequest.getMarketId(), getFarmerRequest.getFruitsId(), true);
        } else if (getFarmerRequest.getFarmerNumber() != null && !getFarmerRequest.getFarmerNumber().equals("")) {
            farmerVirtualBankAccount = farmerVirtualBankAccountRepository.getByFarmerByMarketIdAndFarmerNumber(getFarmerRequest.getMarketId(), getFarmerRequest.getFarmerNumber(), true);
        } else {
            farmerVirtualBankAccount = farmerVirtualBankAccountRepository.getByFarmerByMarketIdAndMobileNumber(getFarmerRequest.getMarketId(), getFarmerRequest.getMobileNumber(), true);
        }
        if (farmerVirtualBankAccount == null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid id");
        } else {
            farmerResponse = mapper.farmerVirtualBankAccountViewToObject(farmerVirtualBankAccount, FarmerVirtualBankAccountResponse.class);
            farmerResponse.setError(false);
        }
        log.info("Entity is ", farmerVirtualBankAccount);
        return farmerResponse;
    }

    public FarmerVirtualBankAccountResponse getByIdJoin(int id) {
        FarmerVirtualBankAccountResponse farmerVirtualBankAccountResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccountView farmerVirtualBankAccountView = farmerVirtualBankAccountRepository.getByFarmerVirtualBankAccountIdAndActive(id, true);
        if (farmerVirtualBankAccountView == null) {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Invalid id");
        } else {
            farmerVirtualBankAccountResponse = mapper.farmerVirtualBankAccountViewToObject(farmerVirtualBankAccountView, FarmerVirtualBankAccountResponse.class);
            farmerVirtualBankAccountResponse.setError(false);
        }
        return farmerVirtualBankAccountResponse;
    }

    @Transactional
    public FarmerVirtualBankAccountResponse updateFarmerVirtualBankAccountDetails(EditFarmerVirtualBankAccountRequest farmerVirtualBankAccountRequest) {
        FarmerVirtualBankAccountResponse farmerVirtualBankAccountResponse = new FarmerVirtualBankAccountResponse();
        FarmerVirtualBankAccount farmerVirtualBankAccount = farmerVirtualBankAccountRepository.findByFarmerVirtualBankAccountIdAndActiveIn(farmerVirtualBankAccountRequest.getFarmerVirtualBankAccountId(), Set.of(true, false));
        if (Objects.nonNull(farmerVirtualBankAccount)) {
            List<FarmerVirtualBankAccount> farmerVirtualBankAccountList = farmerVirtualBankAccountRepository.findByVirtualAccountNumberAndActiveAndFarmerVirtualBankAccountIdIsNot(farmerVirtualBankAccountRequest.getVirtualAccountNumber(), true, farmerVirtualBankAccountRequest.getFarmerVirtualBankAccountId());
            if (farmerVirtualBankAccountList.size() > 0) {
                farmerVirtualBankAccountResponse.setError(true);
                farmerVirtualBankAccountResponse.setError_description("Please check virtual account number");
            } else {
                farmerVirtualBankAccount.setFarmerVirtualBankAccountId(farmerVirtualBankAccountRequest.getFarmerVirtualBankAccountId());
                farmerVirtualBankAccount.setFarmerId(farmerVirtualBankAccountRequest.getFarmerId());
                farmerVirtualBankAccount.setVirtualAccountNumber(farmerVirtualBankAccountRequest.getVirtualAccountNumber());
                farmerVirtualBankAccount.setBranchName(farmerVirtualBankAccountRequest.getBranchName());
                farmerVirtualBankAccount.setIfscCode(farmerVirtualBankAccountRequest.getIfscCode());
                farmerVirtualBankAccount.setMarketMasterId(farmerVirtualBankAccountRequest.getMarketMasterId());
                boolean previousLockState = Boolean.TRUE.equals(farmerVirtualBankAccount.getIsLocked());
                boolean newLockState = Boolean.TRUE.equals(farmerVirtualBankAccountRequest.getIsLocked());
                farmerVirtualBankAccount.setIsLocked(farmerVirtualBankAccountRequest.getIsLocked());
                farmerVirtualBankAccount.setActive(true);
                FarmerVirtualBankAccount savedAccount = farmerVirtualBankAccountRepository.save(farmerVirtualBankAccount);
                if (previousLockState != newLockState) {
                    FarmerVirtualBankAccountAudit audit = new FarmerVirtualBankAccountAudit();
                    audit.setFarmerVirtualBankAccountId(savedAccount.getFarmerVirtualBankAccountId());
                    audit.setAction(newLockState ? "LOCKED" : "UNLOCKED");
                    farmerVirtualBankAccountAuditRepository.save(audit);
                }
                farmerVirtualBankAccountResponse = mapper.farmerVirtualBankAccountEntityToObject(savedAccount, FarmerVirtualBankAccountResponse.class);
                farmerVirtualBankAccountResponse.setError(false);
            }
        } else {
            farmerVirtualBankAccountResponse.setError(true);
            farmerVirtualBankAccountResponse.setError_description("Error occurred while fetching farmerVirtualBankAccount");
        }
        return farmerVirtualBankAccountResponse;
    }

    private Map<String, Object> convertListToMapResponse(List<FarmerVirtualBankAccount> farmerVirtualBankAccountList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerVirtualBankAccountResponse> farmerVBAccountResponse = farmerVirtualBankAccountList.stream()
                .map(farmerVirtualBankAccount -> mapper.farmerVirtualBankAccountEntityToObject(farmerVirtualBankAccount, FarmerVirtualBankAccountResponse.class))
                .collect(Collectors.toList());
        response.put("farmerVirtualBankAccounts", farmerVBAccountResponse);
        response.put("totalItems", farmerVirtualBankAccountList.size());
        return response;
    }

    public Map<String, Object> getByFarmerIdJoin(int farmerId) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerVirtualBankAccountView> farmerVirtualBankAccountViewList = farmerVirtualBankAccountRepository.getByFarmerIdAndActive(farmerId, true);
        if (farmerVirtualBankAccountViewList.isEmpty()) {
            response.put("error", "Error");
            response.put("error_description", "Invalid id");
            return response;
        } else {
            return convertViewListToMapResponse(farmerVirtualBankAccountViewList);
        }
    }

    private Map<String, Object> convertViewListToMapResponse(List<FarmerVirtualBankAccountView> farmerVirtualBankAccountViewList) {
        Map<String, Object> response = new HashMap<>();
        List<FarmerVirtualBankAccountResponse> farmerVirtualBankAccountResponses = farmerVirtualBankAccountViewList.stream()
                .map(view -> mapper.farmerVirtualBankAccountViewToObject(view, FarmerVirtualBankAccountResponse.class))
                .collect(Collectors.toList());
        response.put("farmerVirtualBankAccount", farmerVirtualBankAccountResponses);
        response.put("totalItems", farmerVirtualBankAccountViewList.size());
        return response;
    }
}
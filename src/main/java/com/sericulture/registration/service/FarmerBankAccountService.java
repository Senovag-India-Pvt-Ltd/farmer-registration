package com.sericulture.registration.service;

import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.model.api.farmerBankAccount.EditFarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.entity.FarmerBankAccount;
import com.sericulture.registration.model.entity.FarmerBankAccountAudit;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.FarmerBankAccountAuditRepository;
import com.sericulture.registration.repository.FarmerBankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerBankAccountService {

    @Autowired
    FarmerBankAccountRepository farmerBankAccountRepository;

    @Autowired
    FarmerBankAccountAuditRepository farmerBankAccountAuditRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    S3Controller s3Controller;

    public FarmerBankAccountResponse getFarmerBankAccountDetails(String farmerBankAccountNumber) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountNumberAndActive(farmerBankAccountNumber, true);
        if (farmerBankAccount == null) {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Farmer Bank Account not found");
        } else {
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        }
        log.info("Entity is ", farmerBankAccount);
        return farmerBankAccountResponse;
    }

//    @Transactional
//    public FarmerBankAccountResponse insertFarmerBankAccountDetails(FarmerBankAccountRequest farmerBankAccountRequest) {
//        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
//        FarmerBankAccount farmerBankAccount = mapper.farmerBankAccountObjectToEntity(farmerBankAccountRequest, FarmerBankAccount.class);
//        validator.validate(farmerBankAccount);
//        List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerBankAccountRequest.getFarmerBankAccountNumber());
//        if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
//            farmerBankAccountResponse.setError(true);
//            farmerBankAccountResponse.setError_description("FarmerBankAccount number already exist");
////        } else if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(Predicate.not(FarmerBankAccount::getActive)).findAny().isPresent()) {
////            //throw new ValidationException("Village name already exist with inactive state");
////            farmerBankAccountResponse.setError(true);
////            farmerBankAccountResponse.setError_description("FarmerBankAccount number already exist with inactive state");
//        } else {
//            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccountRepository.save(farmerBankAccount), FarmerBankAccountResponse.class);
//            farmerBankAccountResponse.setError(false);
//        }
//        return farmerBankAccountResponse;
//    }
@Transactional
public FarmerBankAccountResponse insertFarmerBankAccountDetails(FarmerBankAccountRequest farmerBankAccountRequest) {
    FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
    FarmerBankAccount farmerBankAccount = mapper.farmerBankAccountObjectToEntity(farmerBankAccountRequest, FarmerBankAccount.class);
    validator.validate(farmerBankAccount);

    List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerBankAccountRequest.getFarmerBankAccountNumber());
    if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
        farmerBankAccountResponse.setError(true);
        farmerBankAccountResponse.setError_description("FarmerBankAccount number already exists");
    } else {
        // Save the farmer bank account details
        FarmerBankAccount savedFarmerBankAccount = farmerBankAccountRepository.save(farmerBankAccount);
        farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(savedFarmerBankAccount, FarmerBankAccountResponse.class);
        farmerBankAccountResponse.setError(false);

        // Save the audit details
        FarmerBankAccountAudit farmerBankAccountAudit = new FarmerBankAccountAudit();
        farmerBankAccountAudit.setFarmerBankAccountId(savedFarmerBankAccount.getFarmerBankAccountId());
        farmerBankAccountAudit.setFarmerBankName(savedFarmerBankAccount.getFarmerBankName());
        farmerBankAccountAudit.setFarmerBankAccountNumber(savedFarmerBankAccount.getFarmerBankAccountNumber());
        farmerBankAccountAudit.setFarmerBankBranchName(savedFarmerBankAccount.getFarmerBankBranchName());
        farmerBankAccountAudit.setFarmerBankIfscCode(savedFarmerBankAccount.getFarmerBankIfscCode());
        farmerBankAccountAudit.setFarmerId(savedFarmerBankAccount.getFarmerId());
        // Add reasonMasterId and remark if applicable
        farmerBankAccountAudit.setReasonMasterId(farmerBankAccountRequest.getReasonMasterId());
        farmerBankAccountAudit.setRemark(farmerBankAccountRequest.getRemark());

        farmerBankAccountAuditRepository.save(farmerBankAccountAudit);
    }

    return farmerBankAccountResponse;
}


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

//    @Transactional
//    public FarmerBankAccountResponse updateFarmerBankAccountDetails(EditFarmerBankAccountRequest editFarmerBankAccountRequest) {
//        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
//        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActiveIn(editFarmerBankAccountRequest.getFarmerBankAccountId(), Set.of(true, false));
//        if (Objects.nonNull(farmerBankAccount)) {
//            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumberAndActiveAndFarmerBankAccountIdIsNot(editFarmerBankAccountRequest.getFarmerBankAccountNumber(), true, editFarmerBankAccountRequest.getFarmerBankAccountId());
//            if (farmerBankAccountList.size() > 0) {
//                farmerBankAccountResponse.setError(true);
//                farmerBankAccountResponse.setError_description("Please check account number already exist");
//            } else {
//                farmerBankAccount.setFarmerBankBranchName(editFarmerBankAccountRequest.getFarmerBankBranchName());
//                farmerBankAccount.setFarmerBankIfscCode(editFarmerBankAccountRequest.getFarmerBankIfscCode());
//                farmerBankAccount.setFarmerBankAccountNumber(editFarmerBankAccountRequest.getFarmerBankAccountNumber());
//                farmerBankAccount.setFarmerBankName(editFarmerBankAccountRequest.getFarmerBankName());
//                farmerBankAccount.setFarmerId(editFarmerBankAccountRequest.getFarmerId());
//                farmerBankAccount.setFarmerBankAccountId(editFarmerBankAccountRequest.getFarmerBankAccountId());
//                farmerBankAccount.setAccountImagePath(editFarmerBankAccountRequest.getAccountImagePath());
//                farmerBankAccount.setLock(editFarmerBankAccountRequest.getLock());
//                farmerBankAccount.setActive(true);
//                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerBankAccount);
//                farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount1, FarmerBankAccountResponse.class);
//                farmerBankAccountResponse.setError(false);
//            }
//        }else{
//            farmerBankAccountResponse.setError(true);
//            farmerBankAccountResponse.setError_description("Error occurred while fetching farmerBankAccount");
//        }
//        return farmerBankAccountResponse;
//    }

    @Transactional
    public FarmerBankAccountResponse updateFarmerBankAccountDetails(EditFarmerBankAccountRequest editFarmerBankAccountRequest) {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();

        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActiveIn(editFarmerBankAccountRequest.getFarmerBankAccountId(), Set.of(true, false));

        if (Objects.nonNull(farmerBankAccount)) {
            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumberAndActiveAndFarmerBankAccountIdIsNot(editFarmerBankAccountRequest.getFarmerBankAccountNumber(), true, editFarmerBankAccountRequest.getFarmerBankAccountId());

            if (farmerBankAccountList.size() > 0) {
                farmerBankAccountResponse.setError(true);
                farmerBankAccountResponse.setError_description("Please check, account number already exists");
            } else {
                // Update the farmer's bank account details
                farmerBankAccount.setFarmerBankBranchName(editFarmerBankAccountRequest.getFarmerBankBranchName());
                farmerBankAccount.setFarmerBankIfscCode(editFarmerBankAccountRequest.getFarmerBankIfscCode());
                farmerBankAccount.setFarmerBankAccountNumber(editFarmerBankAccountRequest.getFarmerBankAccountNumber());
                farmerBankAccount.setFarmerBankName(editFarmerBankAccountRequest.getFarmerBankName());
                farmerBankAccount.setFarmerId(editFarmerBankAccountRequest.getFarmerId());
                farmerBankAccount.setFarmerBankAccountId(editFarmerBankAccountRequest.getFarmerBankAccountId());
                farmerBankAccount.setAccountImagePath(editFarmerBankAccountRequest.getAccountImagePath());
                farmerBankAccount.setLock(editFarmerBankAccountRequest.getLock());
                farmerBankAccount.setReasonMasterId(editFarmerBankAccountRequest.getReasonMasterId());
                farmerBankAccount.setRemark(editFarmerBankAccountRequest.getRemark());
                farmerBankAccount.setActive(true);

                // Save the updated farmer bank account details
                FarmerBankAccount updatedFarmerBankAccount = farmerBankAccountRepository.save(farmerBankAccount);

                // Update farmer bank account audit details
                FarmerBankAccountAudit farmerBankAccountAudit = new FarmerBankAccountAudit();
                farmerBankAccountAudit.setFarmerBankAccountId(updatedFarmerBankAccount.getFarmerBankAccountId());
                farmerBankAccountAudit.setFarmerBankName(updatedFarmerBankAccount.getFarmerBankName());
                farmerBankAccountAudit.setFarmerBankAccountNumber(updatedFarmerBankAccount.getFarmerBankAccountNumber());
                farmerBankAccountAudit.setFarmerBankBranchName(updatedFarmerBankAccount.getFarmerBankBranchName());
                farmerBankAccountAudit.setFarmerBankIfscCode(updatedFarmerBankAccount.getFarmerBankIfscCode());
                farmerBankAccountAudit.setFarmerId(updatedFarmerBankAccount.getFarmerId());
                farmerBankAccountAudit.setReasonMasterId(editFarmerBankAccountRequest.getReasonMasterId()); // Reason for the update
                farmerBankAccountAudit.setRemark(editFarmerBankAccountRequest.getRemark()); // Additional remarks

                // Save the audit information
                farmerBankAccountAuditRepository.save(farmerBankAccountAudit);

                // Map the updated account entity to response
                farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(updatedFarmerBankAccount, FarmerBankAccountResponse.class);
                farmerBankAccountResponse.setError(false);
            }
        } else {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Error occurred while fetching Farmer Bank Account.");
        }

        return farmerBankAccountResponse;
    }


    @Transactional
    public FarmerBankAccountResponse updateBankAccountPhotoPath(MultipartFile multipartFile, String farmerBankAccountId) throws Exception {
        FarmerBankAccountResponse farmerBankAccountResponse = new FarmerBankAccountResponse();
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerBankAccountIdAndActive(Long.parseLong(farmerBankAccountId),true);
        if (Objects.nonNull(farmerBankAccount)) {
            UUID uuid = UUID.randomUUID();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            String fileName = "farmer_bank_account/"+farmerBankAccount.getFarmerId()+"_"+farmerBankAccountId+"_"+uuid+"_"+extension;
            s3Controller.uploadFile(multipartFile, fileName);
            farmerBankAccount.setAccountImagePath(fileName);
            FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerBankAccount);
            farmerBankAccountResponse = mapper.farmerBankAccountEntityToObject(farmerBankAccount1, FarmerBankAccountResponse.class);
            farmerBankAccountResponse.setError(false);
        } else {
            farmerBankAccountResponse.setError(true);
            farmerBankAccountResponse.setError_description("Error occurred while fetching farmerBankAccount");
        }

        return farmerBankAccountResponse;
    }
}
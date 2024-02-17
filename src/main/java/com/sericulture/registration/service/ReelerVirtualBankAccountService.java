package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.EditReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
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
        ReelerVirtualBankAccountResponse reelerVirtualBankAccountResponse = new ReelerVirtualBankAccountResponse();
        ReelerVirtualBankAccount reelerVirtualBankAccount = mapper.reelerVirtualBankAccountObjectToEntity(reelerVirtualBankAccountRequest,ReelerVirtualBankAccount.class);
        validator.validate(reelerVirtualBankAccount);
        List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByVirtualAccountNumber(reelerVirtualBankAccount.getVirtualAccountNumber());
        if (!reelerVirtualBankAccountList.isEmpty() && reelerVirtualBankAccountList.stream().filter(ReelerVirtualBankAccount::getActive).findAny().isPresent()) {
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("Reeler virtual bank account number already exist");
        } else if (!reelerVirtualBankAccountList.isEmpty() && reelerVirtualBankAccountList.stream().filter(Predicate.not(ReelerVirtualBankAccount::getActive)).findAny().isPresent()) {
            //throw new ValidationException("Village name already exist with inactive state");
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("FarmerBankAccount number already exist with inactive state");
        } else {
            reelerVirtualBankAccountResponse = mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount), ReelerVirtualBankAccountResponse.class);
            reelerVirtualBankAccountResponse.setError(false);
        }
        return reelerVirtualBankAccountResponse;
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
    public ReelerVirtualBankAccountResponse deleteReelerVirtualBankAccountDetails(long id) {
        ReelerVirtualBankAccountResponse reelerVirtualBankAccountResponse = new ReelerVirtualBankAccountResponse();
        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActive(id, true);
        if (Objects.nonNull(reelerVirtualBankAccount)) {
            reelerVirtualBankAccount.setActive(false);
            reelerVirtualBankAccountResponse = mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount), ReelerVirtualBankAccountResponse.class);
            reelerVirtualBankAccountResponse.setError(false);
        } else {
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return reelerVirtualBankAccountResponse;
    }

    @Transactional
    public ReelerVirtualBankAccountResponse getById(int id){
        ReelerVirtualBankAccountResponse reelerVirtualBankAccountResponse = new ReelerVirtualBankAccountResponse();
        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActive(id,true);
        if(reelerVirtualBankAccount == null){
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("Invalid id");
        } else {
            reelerVirtualBankAccountResponse = mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount, ReelerVirtualBankAccountResponse.class);
            reelerVirtualBankAccountResponse.setError(false);
        }
        log.info("Entity is ", reelerVirtualBankAccount);
        return reelerVirtualBankAccountResponse;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getByReelerId(int ReelerId) {
        Map<String, Object> response = new HashMap<>();
        List<ReelerVirtualBankAccount> reelerList = reelerVirtualBankAccountRepository.findByReelerIdAndActive(ReelerId, true);
        if (reelerList.isEmpty()) {
            response.put("error", "Error");
            response.put("error_description", "Invalid id");
            return response;
        } else {
            response = convertListToMapResponse(reelerList);
            return response;
//        return convertListToMapResponse(reelerList);
        }
    }

    @Transactional
    public ReelerVirtualBankAccountResponse getByIdJoin(int id){
        ReelerVirtualBankAccountResponse reelerVirtualBankAccountResponse = new ReelerVirtualBankAccountResponse();
        ReelerVirtualBankAccountDTO reelerVirtualBankAccountDTO = reelerVirtualBankAccountRepository.getByReelerVirtualBankAccountIdAndActive(id,true);
        if(reelerVirtualBankAccountDTO == null){
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("Invalid id");
        } else {
            reelerVirtualBankAccountResponse = mapper.reelerVirtualBankAccountDTOToObject(reelerVirtualBankAccountDTO, ReelerVirtualBankAccountResponse.class);
            reelerVirtualBankAccountResponse.setError(false);
        }
        // log.info("Entity is ", farmerAddressDTO);
//        return mapper.reelerVirtualBankAccountDTOToObject(reelerVirtualBankAccountDTO, ReelerVirtualBankAccountResponse.class);
        return  reelerVirtualBankAccountResponse;
    }

    @Transactional
    public ReelerVirtualBankAccountResponse updateReelerVirtualBankAccountDetails(EditReelerVirtualBankAccountRequest reelerVirtualBankAccountRequest){
        ReelerVirtualBankAccountResponse reelerVirtualBankAccountResponse = new ReelerVirtualBankAccountResponse();
       /* List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountName(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountName());
        if(reelerVirtualBankAccountList.size()>0){
            throw new ValidationException("ReelerVirtualBankAccount already exists with this name, duplicates are not allowed.");
        }*/

        ReelerVirtualBankAccount reelerVirtualBankAccount = reelerVirtualBankAccountRepository.findByReelerVirtualBankAccountIdAndActiveIn(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountId(), Set.of(true,false));
        if(Objects.nonNull(reelerVirtualBankAccount)){
            List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByVirtualAccountNumberAndActiveAndReelerVirtualBankAccountIdIsNot(reelerVirtualBankAccountRequest.getVirtualAccountNumber(), true, reelerVirtualBankAccountRequest.getReelerVirtualBankAccountId());
            if (reelerVirtualBankAccountList.size() > 0) {
                reelerVirtualBankAccountResponse.setError(true);
                reelerVirtualBankAccountResponse.setError_description("Please check virtual account number");
            }else{
                reelerVirtualBankAccount.setReelerVirtualBankAccountId(reelerVirtualBankAccountRequest.getReelerVirtualBankAccountId());
                reelerVirtualBankAccount.setReelerId(reelerVirtualBankAccountRequest.getReelerId());
                reelerVirtualBankAccount.setVirtualAccountNumber(reelerVirtualBankAccountRequest.getVirtualAccountNumber());
                reelerVirtualBankAccount.setBranchName(reelerVirtualBankAccountRequest.getBranchName());
                reelerVirtualBankAccount.setIfscCode(reelerVirtualBankAccountRequest.getIfscCode());
                reelerVirtualBankAccount.setMarketMasterId(reelerVirtualBankAccountRequest.getMarketMasterId());
                reelerVirtualBankAccount.setActive(true);
                ReelerVirtualBankAccount reelerVirtualBankAccount1 = reelerVirtualBankAccountRepository.save(reelerVirtualBankAccount);
                reelerVirtualBankAccountResponse = mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount1, ReelerVirtualBankAccountResponse.class);
                reelerVirtualBankAccountResponse.setError(false);
            }
        } else {
            reelerVirtualBankAccountResponse.setError(true);
            reelerVirtualBankAccountResponse.setError_description("Error occurred while fetching reelerVirtualBankAccount");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerVirtualBankAccountResponse;
    }

    private Map<String, Object> convertListToMapResponse(List<ReelerVirtualBankAccount> reelerVirtualBankAccountList) {
        Map<String, Object> response = new HashMap<>();
        List<ReelerVirtualBankAccountResponse> reelerVBAccountResponse = reelerVirtualBankAccountList.stream()
                .map(reelerVirtualBankAccount -> mapper.reelerVirtualBankAccountEntityToObject(reelerVirtualBankAccount,ReelerVirtualBankAccountResponse.class)).collect(Collectors.toList());
        response.put("reelerVirtualBankAccounts",reelerVBAccountResponse);
        response.put("totalItems", reelerVirtualBankAccountList.size());
        return response;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getByReelerIdJoin(int reelerID){
        Map<String, Object> response = new HashMap<>();
        List<ReelerVirtualBankAccountDTO> reelerVirtualBankAccountDTO = reelerVirtualBankAccountRepository.getByReelerIdAndActive(reelerID, true);
        if(reelerVirtualBankAccountDTO.isEmpty()){
            response.put("error","Error");
            response.put("error_description","Invalid id");
            return response;
        }else {
            response = convertListDTOToMapResponse(reelerVirtualBankAccountDTO);
            return response;

        }

    }

    private Map<String, Object> convertListDTOToMapResponse(List<ReelerVirtualBankAccountDTO> reelerVirtualBankAccountDTOList) {
        Map<String, Object> response = new HashMap<>();
        List<ReelerVirtualBankAccountResponse> reelerVirtualBankAccountResponse = reelerVirtualBankAccountDTOList.stream()
                .map(reelerVirtualBankAccountDTO -> mapper.reelerVirtualBankAccountDTOToObject(reelerVirtualBankAccountDTO, ReelerVirtualBankAccountResponse.class)).collect(Collectors.toList());
        response.put("reelerVirtualBankAccount", reelerVirtualBankAccountResponse);
        response.put("totalItems", reelerVirtualBankAccountDTOList.size());
        return response;
    }

}
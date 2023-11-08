package com.sericulture.registration.service;

import com.sericulture.registration.model.api.reelerLicenseTransaction.EditReelerLicenseTransactionRequest;
import com.sericulture.registration.model.api.reelerLicenseTransaction.ReelerLicenseTransactionRequest;
import com.sericulture.registration.model.api.reelerLicenseTransaction.ReelerLicenseTransactionResponse;
import com.sericulture.registration.model.entity.ReelerLicenseTransaction;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ReelerLicenseTransactionRepository;
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
public class ReelerLicenseTransactionService {

    @Autowired
    ReelerLicenseTransactionRepository reelerLicenseTransactionRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public ReelerLicenseTransactionResponse insertReelerLicenseTransactionDetails(ReelerLicenseTransactionRequest reelerLicenseTransactionRequest){
        ReelerLicenseTransaction reelerLicenseTransaction = mapper.reelerLicenseTransactionObjectToEntity(reelerLicenseTransactionRequest,ReelerLicenseTransaction.class);
        validator.validate(reelerLicenseTransaction);
        /*List<ReelerLicenseTransaction> reelerLicenseTransactionList = reelerLicenseTransactionRepository.findByReelerLicenseTransactionName(reelerLicenseTransactionRequest.getReelerLicenseTransactionName());
        if(!reelerLicenseTransactionList.isEmpty() && reelerLicenseTransactionList.stream().filter(ReelerLicenseTransaction::getActive).findAny().isPresent()){
            throw new ValidationException("ReelerLicenseTransaction name already exist");
        }
        if(!reelerLicenseTransactionList.isEmpty() && reelerLicenseTransactionList.stream().filter(Predicate.not(ReelerLicenseTransaction::getActive)).findAny().isPresent()){
            throw new ValidationException("ReelerLicenseTransaction name already exist with inactive state");
        }*/
        return mapper.reelerLicenseTransactionEntityToObject(reelerLicenseTransactionRepository.save(reelerLicenseTransaction),ReelerLicenseTransactionResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedReelerLicenseTransactionDetails(final Pageable pageable){
        return convertToMapResponse(reelerLicenseTransactionRepository.findByActiveOrderByReelerLicenseTransactionIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<ReelerLicenseTransaction> activeReelerLicenseTransactions) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerLicenseTransactionResponse> reelerLicenseTransactionResponses = activeReelerLicenseTransactions.getContent().stream()
                .map(reelerLicenseTransaction -> mapper.reelerLicenseTransactionEntityToObject(reelerLicenseTransaction,ReelerLicenseTransactionResponse.class)).collect(Collectors.toList());
        response.put("reelerLicenseTransaction",reelerLicenseTransactionResponses);
        response.put("currentPage", activeReelerLicenseTransactions.getNumber());
        response.put("totalItems", activeReelerLicenseTransactions.getTotalElements());
        response.put("totalPages", activeReelerLicenseTransactions.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteReelerLicenseTransactionDetails(long id) {
        ReelerLicenseTransaction reelerLicenseTransaction = reelerLicenseTransactionRepository.findByReelerLicenseTransactionIdAndActive(id, true);
        if (Objects.nonNull(reelerLicenseTransaction)) {
            reelerLicenseTransaction.setActive(false);
            reelerLicenseTransactionRepository.save(reelerLicenseTransaction);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public ReelerLicenseTransactionResponse getById(int id){
        ReelerLicenseTransaction reelerLicenseTransaction = reelerLicenseTransactionRepository.findByReelerLicenseTransactionIdAndActive(id,true);
        if(reelerLicenseTransaction == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",reelerLicenseTransaction);
        return mapper.reelerLicenseTransactionEntityToObject(reelerLicenseTransaction,ReelerLicenseTransactionResponse.class);
    }

    @Transactional
    public ReelerLicenseTransactionResponse updateReelerLicenseTransactionDetails(EditReelerLicenseTransactionRequest reelerLicenseTransactionRequest){
        /*List<ReelerLicenseTransaction> reelerLicenseTransactionList = reelerLicenseTransactionRepository.findByReelerLicenseTransactionName(reelerLicenseTransactionRequest.getReelerLicenseTransactionName());
        if(reelerLicenseTransactionList.size()>0){
            throw new ValidationException("ReelerLicenseTransaction already exists with this name, duplicates are not allowed.");
        }*/

        ReelerLicenseTransaction reelerLicenseTransaction = reelerLicenseTransactionRepository.findByReelerLicenseTransactionIdAndActiveIn(reelerLicenseTransactionRequest.getReelerLicenseTransactionId(), Set.of(true,false));
        if(Objects.nonNull(reelerLicenseTransaction)){
            reelerLicenseTransaction.setReelerId(reelerLicenseTransactionRequest.getReelerId());
            reelerLicenseTransaction.setFeeAmount(reelerLicenseTransactionRequest.getFeeAmount());
            reelerLicenseTransaction.setRenewedDate(reelerLicenseTransactionRequest.getRenewedDate());
            reelerLicenseTransaction.setExpirationDate(reelerLicenseTransactionRequest.getExpirationDate());
            reelerLicenseTransaction.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching reelerLicenseTransaction");
        }
        return mapper.reelerLicenseTransactionEntityToObject(reelerLicenseTransactionRepository.save(reelerLicenseTransaction),ReelerLicenseTransactionResponse.class);
    }

}
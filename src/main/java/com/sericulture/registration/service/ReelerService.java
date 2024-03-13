package com.sericulture.registration.service;

import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.api.common.SearchByColumnRequest;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.reeler.*;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerSearchDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ReelerLicenseTransactionRepository;
import com.sericulture.registration.repository.ReelerRepository;
import com.sericulture.registration.repository.ReelerVirtualBankAccountRepository;
import com.sericulture.registration.repository.SerialCounterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReelerService {

    @Autowired
    ReelerRepository reelerRepository;

    @Autowired
    ReelerLicenseTransactionRepository reelerLicenseTransactionRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    ReelerVirtualBankAccountRepository reelerVirtualBankAccountRepository;

    @Autowired
    SerialCounterRepository serialCounterRepository;

    @Autowired
    S3Controller s3Controller;

    @Transactional
    public ReelerResponse insertReelerDetails(ReelerRequest reelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = mapper.reelerObjectToEntity(reelerRequest,Reeler.class);
        validator.validate(reeler);
        List<Reeler> reelerListByLicenseNumber = reelerRepository.findByReelingLicenseNumber(reeler.getReelingLicenseNumber());
        if(!reelerListByLicenseNumber.isEmpty() && reelerListByLicenseNumber.stream().filter(Reeler::getActive).findAny().isPresent()){
            reelerResponse.setError(true);
            reelerResponse.setError_description("Reeler License Number already exist");
//        }
//        else if(!reelerListByLicenseNumber.isEmpty() && reelerListByLicenseNumber.stream().filter(Predicate.not(Reeler::getActive)).findAny().isPresent()){
//            //throw new ValidationException("Village name already exist with inactive state");
//            reelerResponse.setError(true);
//            reelerResponse.setError_description("Reeler License Number already exist with inactive state");
        } else {
            // Check for duplicate Reeler Number
            List<Reeler> reelerListByNumber = reelerRepository.findByReelerNumber(reeler.getReelerNumber());
            if (!reelerListByNumber.isEmpty() && reelerListByNumber.stream().anyMatch(Reeler::getActive)) {
                reelerResponse.setError(true);
                reelerResponse.setError_description("Reeler Number already exists");
//            } else if (!reelerListByNumber.isEmpty() && reelerListByNumber.stream().anyMatch(Predicate.not(Reeler::getActive))) {
//                reelerResponse.setError(true);
//                reelerResponse.setError_description("Reeler Number already exists with inactive state");
            } else {
                // If no duplicates found, save the reeler
                LocalDate today = Util.getISTLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                String formattedDate = today.format(formatter);
                List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
                SerialCounter serialCounter = new SerialCounter();
                if(reelerRequest.getTransferReelerId() == 0) {
                    if (serialCounters.size() > 0) {
                        serialCounter = serialCounters.get(0);
                        long counterValue = 1L;
                        if (serialCounter.getReelerCounterNumber() != null) {
                            counterValue = serialCounter.getReelerCounterNumber() + 1;
                        }
                        serialCounter.setReelerCounterNumber(counterValue);
                    } else {
                        serialCounter.setReelerCounterNumber(1L);
                    }
                    serialCounterRepository.save(serialCounter);
                    String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

                    reeler.setArnNumber("NRL/"+formattedDate+"/"+formattedNumber);
                }else{
                    if (serialCounters.size() > 0) {
                        serialCounter = serialCounters.get(0);
                        long counterValue = 1L;
                        if (serialCounter.getTransferReelerLicenseCounterNumber() != null) {
                            counterValue = serialCounter.getTransferReelerLicenseCounterNumber() + 1;
                        }
                        serialCounter.setReelerCounterNumber(counterValue);
                    } else {
                        serialCounter.setReelerCounterNumber(1L);
                    }
                    serialCounterRepository.save(serialCounter);
                    String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

                    reeler.setArnNumber("TRL/"+formattedDate+"/"+formattedNumber);
                }

                reelerResponse = mapper.reelerEntityToObject(reelerRepository.save(reeler), ReelerResponse.class);
                reelerResponse.setError(false);
            }
        }
        return reelerResponse;
    }

    @Transactional
    public ReelerResponse dummyReelerDetails(ReelerRequest reelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        for(int i=0; i< 1000; i++) {
            UUID uuid = UUID.randomUUID();
            reelerRequest.setReelerName("dummy_reeler"+ uuid);
            reelerRequest.setPassbookNumber(String.valueOf(uuid));
            reelerRequest.setReelerNumber(String.valueOf(uuid));
            reelerRequest.setLicenseReceiptNumber(String.valueOf(uuid));
            reelerRequest.setReelingLicenseNumber(String.valueOf(uuid));
            reelerRequest.setMobileNumber(String.valueOf(uuid));
            reelerRequest.setBankAccountNumber("dummy_acc"+ uuid);
            Reeler reeler = mapper.reelerObjectToEntity(reelerRequest, Reeler.class);
            validator.validate(reeler);
            List<Reeler> reelerListByLicenseNumber = reelerRepository.findByReelingLicenseNumber(reeler.getReelingLicenseNumber());
            if (!reelerListByLicenseNumber.isEmpty() && reelerListByLicenseNumber.stream().filter(Reeler::getActive).findAny().isPresent()) {
                reelerResponse.setError(true);
                reelerResponse.setError_description("Reeler License Number already exist");
//        }
//        else if(!reelerListByLicenseNumber.isEmpty() && reelerListByLicenseNumber.stream().filter(Predicate.not(Reeler::getActive)).findAny().isPresent()){
//            //throw new ValidationException("Village name already exist with inactive state");
//            reelerResponse.setError(true);
//            reelerResponse.setError_description("Reeler License Number already exist with inactive state");
            } else {
                // Check for duplicate Reeler Number
                List<Reeler> reelerListByNumber = reelerRepository.findByReelerNumber(reeler.getReelerNumber());
                if (!reelerListByNumber.isEmpty() && reelerListByNumber.stream().anyMatch(Reeler::getActive)) {
                    reelerResponse.setError(true);
                    reelerResponse.setError_description("Reeler Number already exists");
//            } else if (!reelerListByNumber.isEmpty() && reelerListByNumber.stream().anyMatch(Predicate.not(Reeler::getActive))) {
//                reelerResponse.setError(true);
//                reelerResponse.setError_description("Reeler Number already exists with inactive state");
                } else {
                    // If no duplicates found, save the reeler
                    LocalDate today = Util.getISTLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                    String formattedDate = today.format(formatter);
                    List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
                    SerialCounter serialCounter = new SerialCounter();
                    if (reelerRequest.getTransferReelerId() == 0) {
                        if (serialCounters.size() > 0) {
                            serialCounter = serialCounters.get(0);
                            long counterValue = 1L;
                            if (serialCounter.getReelerCounterNumber() != null) {
                                counterValue = serialCounter.getReelerCounterNumber() + 1;
                            }
                            serialCounter.setReelerCounterNumber(counterValue);
                        } else {
                            serialCounter.setReelerCounterNumber(1L);
                        }
                        serialCounterRepository.save(serialCounter);
                        String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

                        reeler.setArnNumber("NRL/" + formattedDate + "/" + formattedNumber);
                    } else {
                        if (serialCounters.size() > 0) {
                            serialCounter = serialCounters.get(0);
                            long counterValue = 1L;
                            if (serialCounter.getTransferReelerLicenseCounterNumber() != null) {
                                counterValue = serialCounter.getTransferReelerLicenseCounterNumber() + 1;
                            }
                            serialCounter.setReelerCounterNumber(counterValue);
                        } else {
                            serialCounter.setReelerCounterNumber(1L);
                        }
                        serialCounterRepository.save(serialCounter);
                        String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

                        reeler.setArnNumber("TRL/" + formattedDate + "/" + formattedNumber);
                    }
                    reelerResponse = mapper.reelerEntityToObject(reelerRepository.save(reeler), ReelerResponse.class);
                    reelerResponse.setError(false);
                }
            }
        }
        return reelerResponse;
    }

    @Transactional
    public ReelerResponse insertTransferReelerDetails(ReelerRequest reelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = mapper.reelerObjectToEntity(reelerRequest,Reeler.class);
        validator.validate(reeler);
        // If no duplicates found, save the reeler
        LocalDate today = Util.getISTLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String formattedDate = today.format(formatter);
        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
        SerialCounter serialCounter = new SerialCounter();
        if (serialCounters.size() > 0) {
            serialCounter = serialCounters.get(0);
            long counterValue = 1L;
            if (serialCounter.getTransferReelerLicenseCounterNumber() != null) {
                counterValue = serialCounter.getTransferReelerLicenseCounterNumber() + 1;
            }
            serialCounter.setReelerCounterNumber(counterValue);
        } else {
            serialCounter.setReelerCounterNumber(1L);
        }
        serialCounterRepository.save(serialCounter);
        String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

        reeler.setArnNumber("TRL/"+formattedDate+"/"+formattedNumber);


        reelerResponse = mapper.reelerEntityToObject(reelerRepository.save(reeler), ReelerResponse.class);
        reelerResponse.setError(false);


        return reelerResponse;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedReelerDetails(final Pageable pageable){
        return convertToMapResponse(reelerRepository.findByActiveOrderByReelerIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Reeler> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerEntityToObject(reeler,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler",reelerResponses);
        response.put("currentPage", activeReelers.getNumber());
        response.put("totalItems", activeReelers.getTotalElements());
        response.put("totalPages", activeReelers.getTotalPages());

        return response;
    }

    @Transactional
    public ReelerResponse deleteReelerDetails(long id) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(id, true);
        if (Objects.nonNull(reeler)) {
            reeler.setActive(false);
            reelerResponse = mapper.reelerEntityToObject(reelerRepository.save(reeler), ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return reelerResponse;
    }

    @Transactional
    public ReelerResponse getById(int id){
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(id,true);
        if(reeler == null){
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        }else{
            reelerResponse =  mapper.reelerEntityToObject(reeler,ReelerResponse.class);
            reelerResponse.setError(false);
        }
        log.info("Entity is ",reeler);
        return reelerResponse;
    }
    @Transactional
    public ReelerResponse updateReelerStatus(UpdateReelerStatusRequest updateReelerStatusRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(updateReelerStatusRequest.getReelerId(), true);
        if (Objects.nonNull(reeler)) {
            reeler.setStatus(updateReelerStatusRequest.getStatus());
            reelerRepository.save(reeler);
//        } else {
//            throw new ValidationException("Invalid Id");
//        }
//        return mapper.reelerEntityToObject(reeler,ReelerResponse.class);
            reelerResponse = mapper.reelerEntityToObject(reeler, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse ;
    }

    @Transactional
    public ReelerResponse updateReelerLicense(UpdateReelerLicenseRequest updateReelerLicenseRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(updateReelerLicenseRequest.getReelerId(), true);
        if (Objects.nonNull(reeler)) {
            reeler.setStatus(updateReelerLicenseRequest.getStatus());
            reeler.setFeeAmount(updateReelerLicenseRequest.getFeeAmount());
            reeler.setLicenseRenewalDate(updateReelerLicenseRequest.getLicenseRenewalDate());
            reeler.setLicenseExpiryDate(updateReelerLicenseRequest.getLicenseExpiryDate());

            LocalDate today = Util.getISTLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            String formattedDate = today.format(formatter);
            List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
            SerialCounter serialCounter = new SerialCounter();
            if(serialCounters.size()>0){
                serialCounter = serialCounters.get(0);
                long counterValue = 1L;
                if(serialCounter.getReelerLicenseRenewalCounterNumber() != null){
                    counterValue =serialCounter.getReelerLicenseRenewalCounterNumber() + 1;
                }
                serialCounter.setReelerLicenseRenewalCounterNumber(counterValue);
            }else{
                serialCounter.setReelerLicenseRenewalCounterNumber(1L);
            }
            serialCounterRepository.save(serialCounter);
            String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

            reeler.setArnNumber("RRL/"+formattedDate+"/"+formattedNumber);


            Reeler savedReeler = reelerRepository.save(reeler);

            //Save record to reeler license transaction table
            ReelerLicenseTransaction reelerLicenseTransaction = new ReelerLicenseTransaction();
            reelerLicenseTransaction.setReelerId(savedReeler.getReelerId());
            reelerLicenseTransaction.setExpirationDate(savedReeler.getLicenseExpiryDate());
            reelerLicenseTransaction.setRenewedDate(savedReeler.getLicenseRenewalDate());
            reelerLicenseTransaction.setFeeAmount(savedReeler.getFeeAmount());
            reelerLicenseTransactionRepository.save(reelerLicenseTransaction);
            reelerResponse = mapper.reelerEntityToObject(reeler, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }

        return reelerResponse;
    }

    @Transactional
    public ReelerResponse updateReelerDetails(EditReelerRequest reelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        /*List<Reeler> reelerList = reelerRepository.findByReelerName(reelerRequest.getReelerName());
        if(reelerList.size()>0){
            throw new ValidationException("Reeler already exists with this name, duplicates are not allowed.");
        }*/

        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(reelerRequest.getReelerId(), Set.of(true,false));
        if(Objects.nonNull(reeler)){
            reeler.setReelerName(reelerRequest.getReelerName());
            reeler.setWardNumber(reelerRequest.getWardNumber());
            reeler.setPassbookNumber(reelerRequest.getPassbookNumber());
            reeler.setFatherName(reelerRequest.getFatherName());
            reeler.setEducationId(reelerRequest.getEducationId());
            reeler.setReelingUnitBoundary(reelerRequest.getReelingUnitBoundary());
            reeler.setDob(reelerRequest.getDob());
            reeler.setRationCard(reelerRequest.getRationCard());
            reeler.setMachineTypeId(reelerRequest.getMachineTypeId());
            reeler.setGender(reelerRequest.getGender());
            reeler.setDateOfMachineInstallation(reelerRequest.getDateOfMachineInstallation());
            reeler.setElectricityRrNumber(reelerRequest.getElectricityRrNumber());
            reeler.setCasteId(reelerRequest.getCasteId());
            reeler.setRevenueDocument(reelerRequest.getRevenueDocument());
            reeler.setNumberOfBasins(reelerRequest.getNumberOfBasins());
            reeler.setMobileNumber(reelerRequest.getMobileNumber());
            reeler.setRecipientId(reelerRequest.getRecipientId());
            reeler.setMahajarDetails(reelerRequest.getMahajarDetails());

            reeler.setEmailId(reelerRequest.getEmailId());
            reeler.setRepresentativeNameAddress(reelerRequest.getRepresentativeNameAddress());
            reeler.setLoanDetails(reelerRequest.getLoanDetails());
            reeler.setAssignToInspectId(reelerRequest.getAssignToInspectId());
            reeler.setGpsLat(reelerRequest.getGpsLat());
            reeler.setGpsLng(reelerRequest.getGpsLng());
            reeler.setInspectionDate(reelerRequest.getInspectionDate());
          //  reeler.setArnNumber(reelerRequest.getArnNumber());
            reeler.setChakbandiLat(reelerRequest.getChakbandiLat());
            reeler.setChakbandiLng(reelerRequest.getChakbandiLng());
            reeler.setAddress(reelerRequest.getAddress());
            reeler.setPincode(reelerRequest.getPincode());
            reeler.setStateId(reelerRequest.getStateId());
            reeler.setDistrictId(reelerRequest.getDistrictId());
            reeler.setTalukId(reelerRequest.getTalukId());
            reeler.setHobliId(reelerRequest.getHobliId());
            reeler.setVillageId(reelerRequest.getVillageId());
            reeler.setLicenseReceiptNumber(reelerRequest.getLicenseReceiptNumber());
            reeler.setLicenseExpiryDate(reelerRequest.getLicenseExpiryDate());
            reeler.setReceiptDate(reelerRequest.getReceiptDate());
            reeler.setFunctionOfUnit(reelerRequest.getFunctionOfUnit());
            reeler.setReelingLicenseNumber(reelerRequest.getReelingLicenseNumber());
            reeler.setFeeAmount(reelerRequest.getFeeAmount());
            reeler.setMemberLoanDetails(reelerRequest.getMemberLoanDetails());
            reeler.setMahajarEast(reelerRequest.getMahajarEast());
            reeler.setMahajarWest(reelerRequest.getMahajarWest());
            reeler.setMahajarNorth(reelerRequest.getMahajarNorth());
            reeler.setMahajarSouth(reelerRequest.getMahajarSouth());
            reeler.setMahajarNorthWest(reelerRequest.getMahajarNorthWest());
            reeler.setMahajarNorthEast(reelerRequest.getMahajarNorthEast());
            reeler.setMahajarSouthEast(reelerRequest.getMahajarSouthEast());
            reeler.setMahajarSouthWest(reelerRequest.getMahajarSouthWest());
            reeler.setBankName(reelerRequest.getBankName());
            reeler.setBankAccountNumber(reelerRequest.getBankAccountNumber());
            reeler.setBranchName(reelerRequest.getBranchName());
            reeler.setIfscCode(reelerRequest.getIfscCode());
            reeler.setStatus(reelerRequest.getStatus());
            reeler.setLicenseRenewalDate(reelerRequest.getLicenseRenewalDate());
            reeler.setFruitsId(reelerRequest.getFruitsId());
            reeler.setIsActivated(reelerRequest.getIsActivated());
            reeler.setWalletAmount(reelerRequest.getWalletAmount());
            reeler.setReelerNumber(reelerRequest.getReelerNumber());
            reeler.setReelerTypeMasterId(reelerRequest.getReelerTypeMasterId());

            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse ;
    }

    @Transactional
    public ReelerResponse getByReelingLicenseNumber(String reelingLicenseNumber){
        ReelerResponse reelerResponse = new ReelerResponse();
        ReelerDTO reeler = reelerRepository.getByReelerLicenseNumberAndActive(reelingLicenseNumber,true);
        if(reeler == null){
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        }else{
            reelerResponse =  mapper.reelerDTOToObject(reeler,ReelerResponse.class);
            reelerResponse.setError(false);
        }
        log.info("Entity is ",reeler);
        return reelerResponse;
    }

    @Transactional
    public GetReelerResponse getReelerDetails(GetReelerRequest getReelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(getReelerRequest.getReelerId(),true);
        if(reeler == null){
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        }
        ReelerDTO reelerDTO = reelerRepository.getByReelerIdAndActive(reeler.getReelerId(), true);
        List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByReelerIdAndActive(reeler.getReelerId(), true);
        List<ReelerVirtualBankAccountDTO> reelerVirtualBankAccountDTOList = reelerVirtualBankAccountRepository.getByReelerIdAndActive(reeler.getReelerId(), true);

        GetReelerResponse getReelerResponse = new GetReelerResponse();
        getReelerResponse.setReelerResponse(mapper.reelerEntityToObject(reeler, ReelerResponse.class));
        getReelerResponse.setReelerDTO(reelerDTO);
        getReelerResponse.setReelerVirtualBankAccountList(reelerVirtualBankAccountList);
        getReelerResponse.setReelerVirtualBankAccountDTOList(reelerVirtualBankAccountDTOList);

        return getReelerResponse;
    }

    @Transactional
    public GetReelerResponse getReelerDetailsByFruitsId(GetFruitsIdRequest getFruitsIdRequest){
        Reeler reeler = reelerRepository.findByFruitsIdAndActive(getFruitsIdRequest.getFruitsId(),true);
        if(reeler == null){
            throw new ValidationException("Invalid reeler id");
        }
        List<ReelerVirtualBankAccount> reelerVirtualBankAccountList = reelerVirtualBankAccountRepository.findByReelerIdAndActive(reeler.getReelerId(), true);
        List<ReelerVirtualBankAccountDTO> reelerVirtualBankAccountDTOList = reelerVirtualBankAccountRepository.getByReelerIdAndActive(reeler.getReelerId(), true);

        GetReelerResponse getReelerResponse = new GetReelerResponse();
        getReelerResponse.setReelerResponse(mapper.reelerEntityToObject(reeler, ReelerResponse.class));
        getReelerResponse.setReelerVirtualBankAccountList(reelerVirtualBankAccountList);
        getReelerResponse.setReelerVirtualBankAccountDTOList(reelerVirtualBankAccountDTOList);

        return getReelerResponse;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReelerResponse getByReelerIdJoin(int reelerId){
        ReelerResponse reelerResponse = new ReelerResponse();
        ReelerDTO reelerDTO = reelerRepository.getByReelerIdAndActive(reelerId, true);
        if(reelerDTO==null){
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        }else{
            reelerResponse =  mapper.reelerDTOToObject(reelerDTO,ReelerResponse.class);
            reelerResponse.setError(false);
        }
        return reelerResponse;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedReelerDetailsWithJoin(final Pageable pageable){
        return convertDTOToMapResponse(reelerRepository.getByActiveOrderByReelerIdAsc( true, pageable));
    }


    private Map<String, Object> convertDTOToMapResponse(final Page<ReelerDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerDTOToObject(reeler,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler",reelerResponses);
        response.put("currentPage", activeReelers.getNumber());
        response.put("totalItems", activeReelers.getTotalElements());
        response.put("totalPages", activeReelers.getTotalPages());
        return response;
    }

    @Transactional
    public ReelerResponse activateReeler(ActivateReelerRequest activateReelerRequest){
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(activateReelerRequest.getReelerId(), Set.of(true,false));
        if(Objects.nonNull(reeler)){
            reeler.setIsActivated(activateReelerRequest.getIsActivated());
            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
        }

        return reelerResponse ;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> inactiveReelers(){
        return converListToResponse(reelerRepository.findByActiveAndIsActivatedOrderByReelerNameAsc( true,0));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getAllByActive(boolean isActive) {
        return convertListEntityToMapResponse(reelerRepository.findByActiveOrderByReelerNameAsc(isActive));
    }

    private Map<String, Object> convertListEntityToMapResponse(final List<Reeler> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.stream()
                .map(reeler -> mapper.reelerEntityToObject(reeler, ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler", reelerResponses);
        return response;
    }

    private Map<String, Object> converListToResponse(final List<Reeler> inactiveReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = inactiveReelers.stream()
                .map(reeler -> mapper.reelerEntityToObject(reeler,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler",reelerResponses);
        response.put("totalItems", inactiveReelers.size());
        return response;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest){
        if(searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")){
            searchWithSortRequest.setSearchText("%%");
        }else{
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if(searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")){
            searchWithSortRequest.setSortColumn("reelerName");
        }
        if(searchWithSortRequest.getSortOrder() == null || searchWithSortRequest.getSortOrder().equals("")){
            searchWithSortRequest.setSortOrder("asc");
        }
        if(searchWithSortRequest.getPageNumber() == null || searchWithSortRequest.getPageNumber().equals("")){
            searchWithSortRequest.setPageNumber("0");
        }
        if(searchWithSortRequest.getPageSize() == null || searchWithSortRequest.getPageSize().equals("")){
            searchWithSortRequest.setPageSize("5");
        }
        Sort sort;
        if(searchWithSortRequest.getSortOrder().equals("asc")){
            sort = Sort.by(Sort.Direction.ASC, searchWithSortRequest.getSortColumn());
        }else{
            sort = Sort.by(Sort.Direction.DESC, searchWithSortRequest.getSortColumn());
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(searchWithSortRequest.getPageNumber()), Integer.parseInt(searchWithSortRequest.getPageSize()), sort);
        Page<ReelerDTO> reelerDTOS = reelerRepository.getSortedReelers(searchWithSortRequest.getJoinColumn(),searchWithSortRequest.getSearchText(),true, pageable);
        log.info("Entity is ",reelerDTOS);
        return convertPageableDTOToMapResponse(reelerDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<ReelerDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerDTOToObject(reeler,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler",reelerResponses);
        response.put("currentPage", activeReelers.getNumber());
        response.put("totalItems", activeReelers.getTotalElements());
        response.put("totalPages", activeReelers.getTotalPages());

        return response;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> searchByColumn(SearchByColumnRequest searchByColumnRequest){
        if(searchByColumnRequest.getSearchText() == null || searchByColumnRequest.getSearchText().equals("")){
            searchByColumnRequest.setSearchText("%%");
        }else{
            searchByColumnRequest.setSearchText("%" + searchByColumnRequest.getSearchText() + "%");
        }
        if(searchByColumnRequest.getSearchColumn() == null || searchByColumnRequest.getSearchColumn().equals("")){
            searchByColumnRequest.setSearchColumn("reeler.reelerName");
        }
        List<ReelerSearchDTO> reelerDTOList = reelerRepository.getReelerBySearchText(searchByColumnRequest.getSearchText(),searchByColumnRequest.getSearchColumn());
        log.info("Entity is ",reelerDTOList);
        return convertPageableDTOToMapResponse(reelerDTOList);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final List<ReelerSearchDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

//        List<ReelerResponse> reelerResponses = activeReelers.stream()
//                .map(reelerDTO -> mapper.reelerDTOToObject(reelerDTO,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reelers",activeReelers);
        response.put("totalItems", activeReelers.size());
        return response;
    }
    @Transactional
    public ReelerResponse updateMahajarDetailsPath(MultipartFile multipartFile, String reelerId) throws Exception {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(Long.parseLong(reelerId),true);
        if (Objects.nonNull(reeler)) {
            UUID uuid = UUID.randomUUID();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            String fileName = "reeler/"+reeler.getReelerId()+"_"+reelerId+"_"+uuid+"_"+extension;
            s3Controller.uploadFile(multipartFile, fileName);
            reeler.setMahajarDetails(fileName);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
        }

        return reelerResponse;
    }
}
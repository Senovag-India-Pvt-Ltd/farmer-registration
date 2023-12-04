package com.sericulture.registration.service;

import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.api.reeler.*;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ReelerLicenseTransactionRepository;
import com.sericulture.registration.repository.ReelerRepository;
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

    @Transactional
    public ReelerResponse insertReelerDetails(ReelerRequest reelerRequest){
        Reeler reeler = mapper.reelerObjectToEntity(reelerRequest,Reeler.class);
        validator.validate(reeler);
        /*List<Reeler> reelerList = reelerRepository.findByReelerName(reelerRequest.getReelerName());
        if(!reelerList.isEmpty() && reelerList.stream().filter(Reeler::getActive).findAny().isPresent()){
            throw new ValidationException("Reeler name already exist");
        }
        if(!reelerList.isEmpty() && reelerList.stream().filter(Predicate.not(Reeler::getActive)).findAny().isPresent()){
            throw new ValidationException("Reeler name already exist with inactive state");
        }*/
        return mapper.reelerEntityToObject(reelerRepository.save(reeler),ReelerResponse.class);
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
    public void deleteReelerDetails(long id) {
        Reeler reeler = reelerRepository.findByReelerIdAndActive(id, true);
        if (Objects.nonNull(reeler)) {
            reeler.setActive(false);
            reelerRepository.save(reeler);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public ReelerResponse getById(int id){
        Reeler reeler = reelerRepository.findByReelerIdAndActive(id,true);
        if(reeler == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",reeler);
        return mapper.reelerEntityToObject(reeler,ReelerResponse.class);
    }

    @Transactional
    public ReelerResponse updateReelerStatus(UpdateReelerStatusRequest updateReelerStatusRequest) {
        Reeler reeler = reelerRepository.findByReelerIdAndActive(updateReelerStatusRequest.getReelerId(), true);
        if (Objects.nonNull(reeler)) {
            reeler.setStatus(updateReelerStatusRequest.getStatus());
            reelerRepository.save(reeler);
        } else {
            throw new ValidationException("Invalid Id");
        }
        return mapper.reelerEntityToObject(reeler,ReelerResponse.class);
    }

    @Transactional
    public ReelerResponse updateReelerLicense(UpdateReelerLicenseRequest updateReelerLicenseRequest) {
        Reeler reeler = reelerRepository.findByReelerIdAndActive(updateReelerLicenseRequest.getReelerId(), true);
        if (Objects.nonNull(reeler)) {
            reeler.setStatus(updateReelerLicenseRequest.getStatus());
            reeler.setFeeAmount(updateReelerLicenseRequest.getFeeAmount());
            reeler.setLicenseRenewalDate(updateReelerLicenseRequest.getLicenseRenewalDate());
            reeler.setLicenseExpiryDate(updateReelerLicenseRequest.getLicenseExpiryDate());
            Reeler savedReeler = reelerRepository.save(reeler);

            //Save record to reeler license transaction table
            ReelerLicenseTransaction reelerLicenseTransaction = new ReelerLicenseTransaction();
            reelerLicenseTransaction.setReelerId(savedReeler.getReelerId());
            reelerLicenseTransaction.setExpirationDate(savedReeler.getLicenseExpiryDate());
            reelerLicenseTransaction.setRenewedDate(savedReeler.getLicenseRenewalDate());
            reelerLicenseTransaction.setFeeAmount(savedReeler.getFeeAmount());
            reelerLicenseTransactionRepository.save(reelerLicenseTransaction);
        } else {
            throw new ValidationException("Invalid Id");
        }
        return mapper.reelerEntityToObject(reeler,ReelerResponse.class);
    }

    @Transactional
    public ReelerResponse updateReelerDetails(EditReelerRequest reelerRequest){
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
            reeler.setArnNumber(reelerRequest.getArnNumber());
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

            reeler.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching reeler");
        }
        return mapper.reelerEntityToObject(reelerRepository.save(reeler),ReelerResponse.class);
    }

    @Transactional
    public ReelerResponse getByReelingLicenseNumber(String reelingLicenseNumber){
        Reeler reeler = reelerRepository.findByReelingLicenseNumberAndActive(reelingLicenseNumber,true);
        if(reeler == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",reeler);
        return mapper.reelerEntityToObject(reeler,ReelerResponse.class);
    }

    @Transactional
    public GetReelerResponse getReelerDetails(GetReelerRequest getReelerRequest){
        Reeler reeler = reelerRepository.findByReelerIdAndActive(getReelerRequest.getReelerId(),true);
        if(reeler == null){
            throw new ValidationException("Invalid reeler id");
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
        ReelerDTO reelerDTO = reelerRepository.getByReelerIdAndActive(reelerId, true);
        if(reelerDTO==null){
            throw new ValidationException("Reeler not found by Reeler Id");
        }
        return mapper.reelerDTOToObject(reelerDTO,ReelerResponse.class);
    }
}
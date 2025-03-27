package com.sericulture.registration.service;

import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.api.traderLicense.*;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.SerialCounter;
import com.sericulture.registration.model.entity.TraderLicense;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.SerialCounterRepository;
import com.sericulture.registration.repository.TraderLicenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraderLicenseService {

    @Autowired
    TraderLicenseRepository traderLicenseRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    SerialCounterRepository serialCounterRepository;

//    @Transactional
//    public TraderLicenseResponse insertTraderLicenseDetails(TraderLicenseRequest traderLicenseRequest){
//        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
//        TraderLicense traderLicense = mapper.traderLicenseObjectToEntity(traderLicenseRequest,TraderLicense.class);
//        validator.validate(traderLicense);
//        List<TraderLicense> traderLicenseList = traderLicenseRepository.findByTraderTypeMasterIdAndTraderLicenseNumberAndLicenseChallanNumberAndActive(traderLicenseRequest.getTraderTypeMasterId(),traderLicenseRequest.getTraderLicenseNumber(),traderLicenseRequest.getLicenseChallanNumber(),true);
//        if(!traderLicenseList.isEmpty() && traderLicenseList.stream().filter(TraderLicense::getActive).findAny().isPresent()){
//            traderLicenseResponse.setError(true);
//            traderLicenseResponse.setError_description("Trader License is already exist");
//            return traderLicenseResponse;
//        }
////        if(!traderLicenseList.isEmpty() && traderLicenseList.stream().filter(Predicate.not(TraderLicense::getActive)).findAny().isPresent()){
////            throw new ValidationException("TraderLicense number already exist with inactive traderLicense");
////        }
//        LocalDate today = Util.getISTLocalDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
//        String formattedDate = today.format(formatter);
//        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
//        SerialCounter serialCounter = new SerialCounter();
//        if(serialCounters.size()>0){
//            serialCounter = serialCounters.get(0);
//            long counterValue = 1L;
//            if(serialCounter.getTraderCounterNumber() != null){
//                counterValue =serialCounter.getTraderCounterNumber() + 1;
//            }
//            serialCounter.setTraderCounterNumber(counterValue);
//        }else{
//            serialCounter.setTraderCounterNumber(1L);
//        }
//        serialCounterRepository.save(serialCounter);
//        String formattedNumber = String.format("%05d", serialCounter.getTraderCounterNumber());
//
//        traderLicense.setArnNumber("NTL/"+formattedDate+"/"+formattedNumber);
//        return mapper.traderLicenseEntityToObject(traderLicenseRepository.save(traderLicense),TraderLicenseResponse.class);
//    }

@Transactional
public TraderLicenseResponse insertTraderLicenseDetails(TraderLicenseRequest traderLicenseRequest) {
    TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
    List<Long> traderLicenseIds = new ArrayList<>(); // Store IDs of saved records

    // Validate if TraderLicenseDetailsRequest is empty
    if (traderLicenseRequest.getTraderLicenseDetailsRequests() == null
            || traderLicenseRequest.getTraderLicenseDetailsRequests().isEmpty()) {
        traderLicenseResponse.setError(true);
        traderLicenseResponse.setError_description("Fill the Virtual Bank Details");
        return traderLicenseResponse;
    }

    // Check if a trader license already exists
    List<TraderLicense> traderLicenseList = traderLicenseRepository.findByTraderTypeMasterIdAndTraderLicenseNumberAndLicenseChallanNumberAndActive(
            traderLicenseRequest.getTraderTypeMasterId(),
            traderLicenseRequest.getTraderLicenseNumber(),
            traderLicenseRequest.getLicenseChallanNumber(),
            true);

    if (!traderLicenseList.isEmpty() && traderLicenseList.stream().anyMatch(TraderLicense::getActive)) {
        traderLicenseResponse.setError(true);
        traderLicenseResponse.setError_description("Trader License already exists");
        return traderLicenseResponse;
    }

    // Generate ARN Number only once
    LocalDate today = Util.getISTLocalDate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
    String formattedDate = today.format(formatter);

    List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
    SerialCounter serialCounter = serialCounters.isEmpty() ? new SerialCounter() : serialCounters.get(0);

    long counterValue = (serialCounter.getTraderCounterNumber() != null) ? serialCounter.getTraderCounterNumber() + 1 : 1L;
    serialCounter.setTraderCounterNumber(counterValue);
    serialCounterRepository.save(serialCounter);

    String formattedNumber = String.format("%05d", serialCounter.getTraderCounterNumber());
    String arnNumber = "NTL/" + formattedDate + "/" + formattedNumber; // Single ARN for all

    // Loop through details and save each entry with the same ARN number
    for (TraderLicenseDetailsRequest details : traderLicenseRequest.getTraderLicenseDetailsRequests()) {
        TraderLicense traderLicense = mapper.traderLicenseObjectToEntity(traderLicenseRequest, TraderLicense.class);
        validator.validate(traderLicense);

        traderLicense.setArnNumber(arnNumber); // Assign the same ARN Number

        // Setting fields from TraderLicenseDetailsRequest
        traderLicense.setVirtualAccountNumber(details.getVirtualAccountNumber());
        traderLicense.setBranchName(details.getBranchName());
        traderLicense.setIfscCode(details.getIfscCode());
        traderLicense.setMarketMasterId(details.getMarketMasterId());

        // Save trader license and get the generated ID
        traderLicense = traderLicenseRepository.save(traderLicense);

        // Store the generated IDs
        traderLicenseIds.add(traderLicense.getTraderLicenseId());
    }

    // Update response after all iterations
    traderLicenseResponse.setError(false);
    traderLicenseResponse.setTraderLicenseIds(traderLicenseIds); // Store multiple IDs
    traderLicenseResponse.setArnNumber(arnNumber); // Return only ONE ARN Number

    return traderLicenseResponse;
}





    public Map<String,Object> getPaginatedTraderLicenseDetails(final Pageable pageable){
        return convertToMapResponse(traderLicenseRepository.findByActiveOrderByTraderLicenseIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<TraderLicense> activeTraderLicenses) {
        Map<String, Object> response = new HashMap<>();

        List<TraderLicenseResponse> traderLicenseResponses = activeTraderLicenses.getContent().stream()
                .map(traderLicense -> mapper.traderLicenseEntityToObject(traderLicense,TraderLicenseResponse.class)).collect(Collectors.toList());
        response.put("traderLicense",traderLicenseResponses);
        response.put("currentPage", activeTraderLicenses.getNumber());
        response.put("totalItems", activeTraderLicenses.getTotalElements());
        response.put("totalPages", activeTraderLicenses.getTotalPages());

        return response;
    }

    public Map<String,Object> getPaginatedTraderLicenseDetailsWithJoin(final Pageable pageable){
        return convertDTOToMapResponse(traderLicenseRepository.getByActiveOrderByTraderLicenseIdAsc( true, pageable));
    }

    private Map<String, Object> convertDTOToMapResponse(final Page<TraderLicenseDTO> activeTraderLicenses) {
        Map<String, Object> response = new HashMap<>();

        List<TraderLicenseResponse> traderLicenseResponses = activeTraderLicenses.getContent().stream()
                .map(traderLicense -> mapper.traderLicenseDTOToObject(traderLicense,TraderLicenseResponse.class)).collect(Collectors.toList());
        response.put("traderLicense",traderLicenseResponses);
        response.put("currentPage", activeTraderLicenses.getNumber());
        response.put("totalItems", activeTraderLicenses.getTotalElements());
        response.put("totalPages", activeTraderLicenses.getTotalPages());
        return response;
    }

    @Transactional
    public TraderLicenseResponse deleteTraderLicenseDetails(long id) {
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActive(id, true);
        if (Objects.nonNull(traderLicense)) {
            traderLicense.setActive(false);
            traderLicenseResponse = mapper.traderLicenseEntityToObject(traderLicenseRepository.save(traderLicense), TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        } else {
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return traderLicenseResponse;
    }

    public TraderLicenseResponse getById(int id){
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActive(id,true);
        if(traderLicense == null){
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Invalid id");
        }else {
            traderLicenseResponse = mapper.traderLicenseEntityToObject(traderLicense, TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        }
        log.info("Entity is ",traderLicense);
//        return mapper.traderLicenseEntityToObject(traderLicense,TraderLicenseResponse.class);
        return traderLicenseResponse;
    }

    public TraderLicenseResponse getByIdJoin(int id){
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        TraderLicenseDTO traderLicenseDTO = traderLicenseRepository.getByTraderLicenseIdAndActive(id,true);
        if(traderLicenseDTO == null){
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Invalid id");
        } else {
            traderLicenseResponse = mapper.traderLicenseDTOToObject(traderLicenseDTO, TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        }
        log.info("Entity is ", traderLicenseDTO);
        return traderLicenseResponse;
    }

    @Transactional
    public TraderLicenseResponse updateTraderLicenseDetails(EditTraderLicenseRequest traderLicenseRequest){
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        /*List<TraderLicense> traderLicenseList = traderLicenseRepository.findByTraderLicenseNumber(traderLicenseRequest.getTraderLicenseNumber());
        if(traderLicenseList.size()>0){
            throw new ValidationException("traderLicense already exists with this name, duplicates are not allowed.");
        }
*/
        TraderLicense traderLicense = traderLicenseRepository.findByTraderLicenseIdAndActiveIn(traderLicenseRequest.getTraderLicenseId(), Set.of(true,false));
        if(Objects.nonNull(traderLicense)){
          //  traderLicense.setArnNumber(traderLicenseRequest.getArnNumber());
            traderLicense.setTraderTypeMasterId(traderLicenseRequest.getTraderTypeMasterId());
            traderLicense.setFirstName(traderLicenseRequest.getFirstName());
            traderLicense.setMiddleName(traderLicenseRequest.getMiddleName());
            traderLicense.setLastName(traderLicenseRequest.getLastName());
            traderLicense.setMobileNumber(traderLicenseRequest.getMobileNumber());
            traderLicense.setFatherName(traderLicenseRequest.getFatherName());
            traderLicense.setStateId(traderLicenseRequest.getStateId());
            traderLicense.setDistrictId(traderLicenseRequest.getDistrictId());
            traderLicense.setAddress(traderLicenseRequest.getAddress());
            traderLicense.setPremisesDescription(traderLicenseRequest.getPremisesDescription());
            traderLicense.setMarketMasterId(traderLicenseRequest.getMarketMasterId());
            traderLicense.setApplicationDate(traderLicenseRequest.getApplicationDate());
            traderLicense.setApplicationNumber(traderLicenseRequest.getApplicationNumber());
            traderLicense.setTraderLicenseNumber(traderLicenseRequest.getTraderLicenseNumber());
            traderLicense.setRepresentativeDetails(traderLicenseRequest.getRepresentativeDetails());
            traderLicense.setLicenseFee(traderLicenseRequest.getLicenseFee());
            traderLicense.setLicenseChallanNumber(traderLicenseRequest.getLicenseChallanNumber());
            traderLicense.setGodownDetails(traderLicenseRequest.getGodownDetails());
            traderLicense.setSilkExchangeMahajar(traderLicenseRequest.getSilkExchangeMahajar());
            traderLicense.setSilkType(traderLicenseRequest.getSilkType());
            traderLicense.setBranchName(traderLicenseRequest.getBranchName());
            traderLicense.setVirtualAccountNumber(traderLicenseRequest.getVirtualAccountNumber());
            traderLicense.setIfscCode(traderLicenseRequest.getIfscCode());

            traderLicense.setActive(true);
            TraderLicense traderLicense1 = traderLicenseRepository.save(traderLicense);
            traderLicenseResponse = mapper.traderLicenseEntityToObject(traderLicense1, TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        } else {
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Error occurred while fetching traderLicense");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return traderLicenseResponse;
    }

    public Map<String,Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest){
        if(searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")){
            searchWithSortRequest.setSearchText("%%");
        }else{
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if(searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")){
            searchWithSortRequest.setSortColumn("firstName");
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
        Page<TraderLicenseDTO> traderLicenseDTOS = traderLicenseRepository.getSortedTraderLicenses(searchWithSortRequest.getJoinColumn(),searchWithSortRequest.getSearchText(),true, pageable);
        log.info("Entity is ",traderLicenseDTOS);
        return convertPageableDTOToMapResponse(traderLicenseDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<TraderLicenseDTO> activeTraderLicenses) {
        Map<String, Object> response = new HashMap<>();

        List<TraderLicenseResponse> traderLicenseResponses = activeTraderLicenses.getContent().stream()
                .map(traderLicense -> mapper.traderLicenseDTOToObject(traderLicense,TraderLicenseResponse.class)).collect(Collectors.toList());
        response.put("traderLicense",traderLicenseResponses);
        response.put("currentPage", activeTraderLicenses.getNumber());
        response.put("totalItems", activeTraderLicenses.getTotalElements());
        response.put("totalPages", activeTraderLicenses.getTotalPages());

        return response;
    }

    public Map<String,Object> getTradersByMarketId(long marketId){
        return convertTraderDTOToMapResponse(traderLicenseRepository.getByTradersByMarketId( marketId,true));
    }

    private Map<String, Object> convertTraderDTOToMapResponse(final List<TraderLicenseDTO> activeTraderLicenses) {
        Map<String, Object> response = new HashMap<>();

        List<TraderLicenseResponse> traderLicenseResponses = activeTraderLicenses.stream()
                .map(traderLicense -> mapper.traderLicenseDTOToObject(traderLicense,TraderLicenseResponse.class)).collect(Collectors.toList());
        response.put("traderLicense",traderLicenseResponses);
        response.put("totalItems", activeTraderLicenses.size());
        return response;
    }
    public TraderLicenseResponse getByTraderLicenseNumber(String traderLicenseNumber) {
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        TraderLicenseDTO traderLicense = traderLicenseRepository.getByTraderLicenseNumberAndActive(traderLicenseNumber, true);
        if (traderLicense == null) {
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Invalid id");
        } else {
            traderLicenseResponse = mapper.traderLicenseDTOToObject(traderLicense, TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        }
        log.info("Entity is ", traderLicense);
        return traderLicenseResponse;
    }

    public TraderLicenseResponse getTraderDetailsByMobileOrReelerNumber(GetTraderLicenseRequest getTraderLicenseRequest) throws Exception{
        TraderLicenseResponse traderLicenseResponse = new TraderLicenseResponse();
        TraderLicenseDTO traderLicenseDTO = new TraderLicenseDTO();
        if(getTraderLicenseRequest.getTraderLicenseNumber() != null && !getTraderLicenseRequest.getTraderLicenseNumber().equals("")) {
            traderLicenseDTO = traderLicenseRepository.getByTraderLicenseByMarketIdAndTraderLicenseNumber(getTraderLicenseRequest.getMarketId(), getTraderLicenseRequest.getTraderLicenseNumber(), true);
//        }else if(getTraderLicenseRequest.getTraderLicenseNumber() != null && !getTraderLicenseRequest.getTraderLicenseNumber().equals("")){
//            traderLicenseDTO = traderLicenseRepository.getByReelerByMarketIdAndReelerNumber(getTraderLicenseRequest.getMarketId(),getTraderLicenseRequest.getTraderLicenseNumber(), true);
//        }
        }else{
            traderLicenseDTO = traderLicenseRepository.getByTraderLicenseByMarketIdAndMobileNumber(getTraderLicenseRequest.getMarketId(),getTraderLicenseRequest.getMobileNumber(), true);
        }
        if(traderLicenseDTO == null){
            traderLicenseResponse.setError(true);
            traderLicenseResponse.setError_description("Invalid id");
        }else{
            traderLicenseResponse =  mapper.traderLicenseDTOToObject(traderLicenseDTO, TraderLicenseResponse.class);
            traderLicenseResponse.setError(false);
        }
        log.info("Entity is ",traderLicenseDTO);
        return traderLicenseResponse;
    }





}

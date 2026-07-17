package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.*;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerAddress.EditFarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.EditFarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.farmerLandDetails.EditFarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetLandDetailsResponse;
import com.sericulture.registration.model.api.reeler.ReelerDetailsResponse;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.dto.caste.CasteDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.farmer.FarmerFamilyDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.dto.village.VillageDTO;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.projection.FarmerPrimaryDetailsProjection;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.*;
import com.sericulture.registration.utils.ObjectToUrlEncodedConverter;
import io.micrometer.core.instrument.MultiGauge;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import com.sericulture.authentication.model.JwtPayloadData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerService {

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    FarmerAddressRepository farmerAddressRepository;

    @Autowired
    FarmerLandDetailsRepository farmerLandDetailsRepository;

    @Autowired
    FarmerFamilyRepository farmerFamilyRepository;

    @Autowired
    FarmerBankAccountRepository farmerBankAccountRepository;

    @Autowired
    FruitsApiService fruitsApiService;

    @Autowired
    FarmerTypeRepository farmerTypeRepository;

    @Autowired
    CasteRepository casteRepository;

    @Autowired
    VillageRepository villageRepository;
    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    S3Controller s3Controller;

    @Autowired
    SerialCounterRepository serialCounterRepository;

    @Autowired
    RequestInspectionMappingRepository requestInspectionMappingRepository;

    @Autowired
    InspectionTaskRepository inspectionTaskRepository;

    @Autowired
    FarmerBankAccountService farmerBankAccountService;

    @Autowired
    FarmerFamilyService farmerFamilyService;

    @Autowired
    FarmerAddressService farmerAddressService;

    @Autowired
    FarmerLandDetailsService farmerLandDetailsService;

    @Autowired
    FarmerVirtualBankAccountService farmerVirtualBankAccountService;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    TalukRepository talukRepository;

    @Autowired
    HobliRepository hobliRepository;

    @Autowired
    ChowkiManagementRepository chowkiManagementRepository;

    @Autowired
    MarketMasterRepository marketMasterRepository;

    // FIX: Injected shared RestTemplate bean - avoids new instance per request (connection leak)
    @Autowired
    RestTemplate restTemplate;

    @Transactional
    public FarmerResponse insertFarmerDetails(FarmerRequest farmerRequest) {
        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest, Farmer.class);
        farmer.setWithoutFruitsInwardCounter(0L);
        validator.validate(farmer);
        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if (!farmerList.isEmpty() && farmerList.stream().filter(Farmer::getActive).findAny().isPresent()) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist");
        } else if (!farmerList.isEmpty() && farmerList.stream().filter(Predicate.not(Farmer::getActive)).findAny().isPresent()) {
            //throw new ValidationException("Village name already exist with inactive state");
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist with inactive state");
        } else {
            // Check for duplicate Reeler Number
            List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
            if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("Farmer Mobile Number already exists");
            } else if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Predicate.not(Farmer::getActive))) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("Farmer Mobile Number already exists with inactive state");
            } else {
                // If no duplicates found, save the reeler
                Farmer savedResponse = farmerRepository.save(farmer);
                farmerResponse = mapper.farmerEntityToObject(savedResponse, FarmerResponse.class);

                //Once farmer created, trigger inspection if farmer created
                /*if(savedResponse.getFarmerId() != null) {
                    InspectionTask inspectionTask = new InspectionTask();
                    inspectionTask.setInspectionDate(LocalDate.now());
                    inspectionTask.setStatus(1); //Open (Newly created)
                    inspectionTask.setUserMasterId(farmerRequest.getInspectorId());
                    inspectionTask.setRequestType("FARMER_REGISTRATION");
                    inspectionTask.setRequestTypeId(savedResponse.getFarmerId());

                    //To fetch inspection type
                    RequestInspectionMapping requestInspectionMapping = requestInspectionMappingRepository.findByRequestTypeNameAndActive("FARMER_REGISTRATION", true);

                    if(requestInspectionMapping != null){
                        inspectionTask.setInspectionType(requestInspectionMapping.getInspectionType());
                        inspectionTaskRepository.save(inspectionTask);
                        farmerResponse.setError(false);
                    }else{
                        farmerResponse.setError(true);
                        farmerResponse.setError_description("Farmer saved, but inspection not saved");
                    }

                }else{
                    farmerResponse.setError(true);
                    farmerResponse.setError_description("Farmer not saved");
                }*/
            }
        }
        return farmerResponse;
    }

//    @Transactional
//    public FarmerResponse insertCompleteFarmerDetails(FarmerSaveRequest farmerSaveRequest) {
//        FarmerRequest farmerRequest = farmerSaveRequest.getFarmerRequest();
//        FarmerBankAccountRequest farmerBankAccountRequest = farmerSaveRequest.getFarmerBankAccountRequest();
//        FarmerResponse farmerResponse = new FarmerResponse();
//        Farmer farmerCheck = farmerRepository.findByFruitsIdAndActive(farmerRequest.getFruitsId(), true);
//        if (farmerCheck != null) {
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Farmer already saved and please check the provided bank details is already exists");
//            return farmerResponse;
//        }
//        if (farmerRequest.getIsOtherStateFarmer() == null) {
//            farmerRequest.setIsOtherStateFarmer(false);
//        }
//        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest, Farmer.class);
//        farmer.setWithoutFruitsInwardCounter(0L);
//        validator.validate(farmer);
//        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
//        if (!farmerList.isEmpty() && farmerList.stream().filter(Farmer::getActive).findAny().isPresent()) {
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Farmer number already exist");
//        } else if (!farmerList.isEmpty() && farmerList.stream().filter(Predicate.not(Farmer::getActive)).findAny().isPresent()) {
//            //throw new ValidationException("Village name already exist with inactive state");
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Farmer number already exist with inactive state");
//        } else {
//            // Check for duplicate Reeler Number
//            List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
//            if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
//                farmerResponse.setError(true);
//                farmerResponse.setError_description("Farmer Mobile Number already exists");
//            } else if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Predicate.not(Farmer::getActive))) {
//                farmerResponse.setError(true);
//                farmerResponse.setError_description("Farmer Mobile Number already exists with inactive state");
//            } else {
//                // If no duplicates found, save the reeler
//                Farmer savedResponse = farmerRepository.save(farmer);
//                farmerResponse = mapper.farmerEntityToObject(savedResponse, FarmerResponse.class);
//
//                //Once farmer created, trigger inspection if farmer created
//                if (savedResponse.getFarmerId() != null) {
//
//
//                    //Save farmer bank acc details
//                    farmerSaveRequest.getFarmerBankAccountRequest().setFarmerId(savedResponse.getFarmerId());
//                    FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.insertFarmerBankAccountDetails(farmerSaveRequest.getFarmerBankAccountRequest());
//                    if (farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
//                        farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
//                    }
//
//                    for (int i = 0; i < farmerSaveRequest.getFarmerAddressRequests().size(); i++) {
//                        farmerSaveRequest.getFarmerAddressRequests().get(i).setFarmerId(savedResponse.getFarmerId());
//                        farmerAddressService.insertFarmerAddressDetails(farmerSaveRequest.getFarmerAddressRequests().get(i));
//                    }
//
//                    for (int i = 0; i < farmerSaveRequest.getFarmerFamilyRequestList().size(); i++) {
//                        farmerSaveRequest.getFarmerFamilyRequestList().get(i).setFarmerId(savedResponse.getFarmerId());
//                        farmerFamilyService.insertFarmerFamilyDetails(farmerSaveRequest.getFarmerFamilyRequestList().get(i));
//                    }
//
//                    for (int i = 0; i < farmerSaveRequest.getFarmerLandDetailsRequests().size(); i++) {
//                        farmerSaveRequest.getFarmerLandDetailsRequests().get(i).setFarmerId(savedResponse.getFarmerId());
//                        farmerLandDetailsService.insertFarmerLandDetailsDetails(farmerSaveRequest.getFarmerLandDetailsRequests().get(i));
//                    }
//
//
//                    /*InspectionTask inspectionTask = new InspectionTask();
//                    inspectionTask.setInspectionDate(LocalDate.now());
//                    inspectionTask.setStatus(1); //Open (Newly created)
//                    inspectionTask.setUserMasterId(farmerRequest.getInspectorId());
//                    inspectionTask.setRequestType("FARMER_REGISTRATION");
//                    inspectionTask.setRequestTypeId(savedResponse.getFarmerId());
//
//                    //To fetch inspection type
//                    RequestInspectionMapping requestInspectionMapping = requestInspectionMappingRepository.findByRequestTypeNameAndActive("FARMER_REGISTRATION", true);
//
//                    if(requestInspectionMapping != null){
//                        inspectionTask.setInspectionType(requestInspectionMapping.getInspectionType());
//                        inspectionTaskRepository.save(inspectionTask);
//                        farmerResponse.setError(false);
//                    }else{
//                        farmerResponse.setError(true);
//                        farmerResponse.setError_description("Farmer saved, but inspection not saved");
//                    }*/
//
//                } else {
//                    farmerResponse.setError(true);
//                }
//            }
//        }
//        return farmerResponse;
//    }

    @Transactional
    public FarmerResponse insertCompleteFarmerDetails(FarmerSaveRequest farmerSaveRequest) {
        FarmerRequest farmerRequest = farmerSaveRequest.getFarmerRequest();
        FarmerBankAccountRequest farmerBankAccountRequest = farmerSaveRequest.getFarmerBankAccountRequest();
        FarmerResponse farmerResponse = new FarmerResponse();

        // 1️⃣ Check Fruits ID
        Farmer farmerCheck = farmerRepository.findByFruitsIdAndActive(farmerRequest.getFruitsId(), true);
        if (farmerCheck != null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer already saved and please check the provided bank details is already exists");
            return farmerResponse;
        }

        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }

        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest, Farmer.class);
        farmer.setWithoutFruitsInwardCounter(0L);
        validator.validate(farmer);

        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if (!farmerList.isEmpty() && farmerList.stream().anyMatch(Farmer::getActive)) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist");
            return farmerResponse;
        } else if (!farmerList.isEmpty() && farmerList.stream().anyMatch(Predicate.not(Farmer::getActive))) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist with inactive state");
            return farmerResponse;
        }

        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Mobile Number already exists");
            return farmerResponse;
        } else if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Predicate.not(Farmer::getActive))) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Mobile Number already exists with inactive state");
            return farmerResponse;
        }

        if (farmerBankAccountRequest != null && farmerBankAccountRequest.getFarmerBankAccountNumber() != null) { // NEW
            List<FarmerBankAccount> existingAccounts = // NEW
                    farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerBankAccountRequest.getFarmerBankAccountNumber()); // NEW

            if (!existingAccounts.isEmpty() && existingAccounts.stream().anyMatch(FarmerBankAccount::getActive)) { // NEW
                farmerResponse.setError(true); // NEW
                farmerResponse.setError_description("Farmer Bank Account Number already exists"); // NEW
                return farmerResponse; // NEW
            } else if (!existingAccounts.isEmpty() && existingAccounts.stream().anyMatch(Predicate.not(FarmerBankAccount::getActive))) { // NEW
                farmerResponse.setError(true); // NEW
                farmerResponse.setError_description("Farmer Bank Account Number already exists with inactive state"); // NEW
                return farmerResponse; // NEW
            }
        }

        Farmer savedResponse = farmerRepository.save(farmer);
        farmerResponse = mapper.farmerEntityToObject(savedResponse, FarmerResponse.class);

        if (savedResponse.getFarmerId() != null) {
            farmerSaveRequest.getFarmerBankAccountRequest().setFarmerId(savedResponse.getFarmerId());
            FarmerBankAccountResponse farmerBankAccountResponse =
                    farmerBankAccountService.insertFarmerBankAccountDetails(farmerSaveRequest.getFarmerBankAccountRequest());
            if (farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
            }

            // Save addresses
            for (var addressRequest : farmerSaveRequest.getFarmerAddressRequests()) {
                addressRequest.setFarmerId(savedResponse.getFarmerId());
                farmerAddressService.insertFarmerAddressDetails(addressRequest);
            }

            // Save family details
            for (var familyRequest : farmerSaveRequest.getFarmerFamilyRequestList()) {
                familyRequest.setFarmerId(savedResponse.getFarmerId());
                farmerFamilyService.insertFarmerFamilyDetails(familyRequest);
            }

            // Save land details
            for (var landRequest : farmerSaveRequest.getFarmerLandDetailsRequests()) {
                landRequest.setFarmerId(savedResponse.getFarmerId());
                farmerLandDetailsService.insertFarmerLandDetailsDetails(landRequest);
            }

            // Save virtual bank account
            if (farmerSaveRequest.getFarmerVirtualBankAccountRequest() != null) {
                farmerSaveRequest.getFarmerVirtualBankAccountRequest().setFarmerId(savedResponse.getFarmerId());
                farmerVirtualBankAccountService.insertFarmerVirtualBankAccountDetails(farmerSaveRequest.getFarmerVirtualBankAccountRequest());
            }

        } else {
            farmerResponse.setError(true);
        }

        return farmerResponse;
    }


    public Map<String, Object> getPaginatedFarmerDetails(final Pageable pageable) {
        return convertToMapResponse(farmerRepository.findByActiveOrderByFarmerIdAsc(true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Farmer> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerEntityToObject(farmer, FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer", farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());

        return response;
    }


    public FarmerResponse deleteFarmerDetails(long id) {
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id, true);
        if (Objects.nonNull(farmer)) {
            farmer.setActive(false);
            farmerResponse = mapper.farmerEntityToObject(farmerRepository.save(farmer), FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerResponse;
    }

    public FarmerResponse getById(int id) {
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id, true);
        if (farmer == null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid id");
        } else {
            farmerResponse = mapper.farmerEntityToObject(farmer, FarmerResponse.class);
            farmerResponse.setError(false);
        }
        log.info("Entity is ", farmer);
        return farmerResponse;
    }

    @Transactional
    public FarmerResponse updateFarmerDetails(EditFarmerRequest farmerRequest) {
        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }
        FarmerResponse farmerResponse = new FarmerResponse();
        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }
*/
        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true, false));
        if (Objects.nonNull(farmer)) {
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setFruitsId(farmerRequest.getFruitsId());
            farmer.setFirstName(farmerRequest.getFirstName());
            farmer.setMiddleName(farmerRequest.getMiddleName());
            farmer.setLastName(farmerRequest.getLastName());
            farmer.setDob(farmerRequest.getDob());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setCasteId(farmerRequest.getCasteId());
            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
            farmer.setEmail(farmerRequest.getEmail());
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
            farmer.setEpicNumber(farmerRequest.getEpicNumber());
            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer.setLandCategoryId(farmerRequest.getLandCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer.setMinority(farmerRequest.getMinority());
            farmer.setRdNumber(farmerRequest.getRdNumber());
            farmer.setCasteStatus(farmerRequest.getCasteStatus());
            farmer.setGenderStatus(farmerRequest.getGenderStatus());
            farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer.setFatherName(farmerRequest.getFatherName());
            farmer.setNameKan(farmerRequest.getNameKan());
            farmer.setAssignToInspectId(farmerRequest.getAssignToInspectId());


            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerResponse;
    }

    @Transactional
    public FarmerResponse updateFarmerProfileDetails(EditFarmerRequest farmerRequest) {
        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }
        FarmerResponse farmerResponse = new FarmerResponse();
        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }
*/
        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true, false));
        if (Objects.nonNull(farmer)) {
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setTscMasterId(farmerRequest.getTscMasterId());
            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerResponse;
    }

//    @Transactional
//    public FarmerResponse editCompleteFarmerDetails(EditCompleteFarmerRequest editCompleteFarmerRequest) {
//        EditFarmerRequest farmerRequest = editCompleteFarmerRequest.getEditFarmerRequest();
//        if (farmerRequest.getIsOtherStateFarmer() == null) {
//            farmerRequest.setIsOtherStateFarmer(false);
//        }
//        FarmerResponse farmerResponse = new FarmerResponse();
//        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
//        if(farmerList.size()>0){
//            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
//        }
//*/
//        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true, false));
//        if (Objects.nonNull(farmer)) {
//            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
//            farmer.setFruitsId(farmerRequest.getFruitsId());
//            farmer.setFirstName(farmerRequest.getFirstName());
//            farmer.setMiddleName(farmerRequest.getMiddleName());
//            farmer.setTscMasterId(farmerRequest.getTscMasterId());
//            farmer.setLastName(farmerRequest.getLastName());
//            farmer.setDob(farmerRequest.getDob());
//            farmer.setGenderId(farmerRequest.getGenderId());
//            farmer.setGenderId(farmerRequest.getGenderId());
//            farmer.setCasteId(farmerRequest.getCasteId());
//            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
//            farmer.setEmail(farmerRequest.getEmail());
//            farmer.setMobileNumber(farmerRequest.getMobileNumber());
//            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
//            farmer.setEpicNumber(farmerRequest.getEpicNumber());
//            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
//            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
//            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
//            farmer.setLandCategoryId(farmerRequest.getLandCategoryId());
//            farmer.setEducationId(farmerRequest.getEducationId());
//            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
//            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
//            farmer.setPhotoPath(farmerRequest.getPhotoPath());
//            farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
//            farmer.setMinority(farmerRequest.getMinority());
//            farmer.setRdNumber(farmerRequest.getRdNumber());
//            farmer.setCasteStatus(farmerRequest.getCasteStatus());
//            farmer.setGenderStatus(farmerRequest.getGenderStatus());
//            farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
//            farmer.setFatherName(farmerRequest.getFatherName());
//            farmer.setNameKan(farmerRequest.getNameKan());
//
//            farmer.setActive(true);
//            Farmer farmer1 = farmerRepository.save(farmer);
//            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
//
//            if (farmerResponse.getFarmerId() > 0) {
//                //Save farmer bank acc details
//                editCompleteFarmerRequest.getEditFarmerBankAccountRequest().setFarmerId(farmerResponse.getFarmerId());
//                FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.updateFarmerBankAccountDetails(editCompleteFarmerRequest.getEditFarmerBankAccountRequest());
//                if (farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
//                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
//                }
//                if (editCompleteFarmerRequest.getEditFarmerFamilyRequests() != null) {
//                    for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerFamilyRequests().size(); i++) {
//                        editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
//                        farmerFamilyService.updateFarmerFamilyDetails(editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i));
//                    }
//                }
//
//                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerAddressRequests().size(); i++) {
//                    editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
//                    farmerAddressService.updateFarmerAddressDetails(editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i));
//                }
//
//                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().size(); i++) {
//                    editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
//                    farmerLandDetailsService.updateFarmerLandDetailsDetails(editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i));
//                }
//            }
//
//            farmerResponse.setError(false);
//        } else {
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Error occurred while fetching Farmer");
//            // throw new ValidationException("Error occurred while fetching village");
//        }
//
//        return farmerResponse;
//    }

        @Transactional
    public FarmerResponse editCompleteFarmerDetails(EditCompleteFarmerRequest editCompleteFarmerRequest) {
        EditFarmerRequest farmerRequest = editCompleteFarmerRequest.getEditFarmerRequest();
        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }
        FarmerResponse farmerResponse = new FarmerResponse();

        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true, false));
        if (Objects.nonNull(farmer)) {
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setFruitsId(farmerRequest.getFruitsId());
            farmer.setFirstName(farmerRequest.getFirstName());
            farmer.setMiddleName(farmerRequest.getMiddleName());
            farmer.setTscMasterId(farmerRequest.getTscMasterId());
            farmer.setLastName(farmerRequest.getLastName());
            farmer.setDob(farmerRequest.getDob());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setCasteId(farmerRequest.getCasteId());
            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
            farmer.setEmail(farmerRequest.getEmail());
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
            farmer.setEpicNumber(farmerRequest.getEpicNumber());
            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer.setLandCategoryId(farmerRequest.getLandCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer.setMinority(farmerRequest.getMinority());
            farmer.setRdNumber(farmerRequest.getRdNumber());
            farmer.setCasteStatus(farmerRequest.getCasteStatus());
            farmer.setGenderStatus(farmerRequest.getGenderStatus());
            farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer.setFatherName(farmerRequest.getFatherName());
            farmer.setNameKan(farmerRequest.getNameKan());

            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);

            if (farmerResponse.getFarmerId() > 0) {
                editCompleteFarmerRequest.getEditFarmerBankAccountRequest().setFarmerId(farmerResponse.getFarmerId());
                FarmerBankAccountResponse farmerBankAccountResponse =
                        farmerBankAccountService.updateFarmerBankAccountDetails(editCompleteFarmerRequest.getEditFarmerBankAccountRequest());
                if (Boolean.TRUE.equals(farmerBankAccountResponse.getError())) {
                    farmerResponse.setError(true);
                    farmerResponse.setError_description(farmerBankAccountResponse.getError_description());
                    return farmerResponse;
                }
                if (farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                }

                // Update farmer family details
                if (editCompleteFarmerRequest.getEditFarmerFamilyRequests() != null) {
                    for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerFamilyRequests().size(); i++) {
                        editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                        farmerFamilyService.updateFarmerFamilyDetails(editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i));
                    }
                }

                // Update farmer address details
                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerAddressRequests().size(); i++) {
                    editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                    farmerAddressService.updateFarmerAddressDetails(editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i));
                }

                // Update farmer land details
                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().size(); i++) {
                    editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                    farmerLandDetailsService.updateFarmerLandDetailsDetails(editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i));
                }
            }

            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
        }

        return farmerResponse;
    }


    @Transactional
    public FarmerResponse updateFruitsId(EditCompleteFarmerRequest editCompleteFarmerRequest) {
        EditFarmerRequest farmerRequest = editCompleteFarmerRequest.getEditFarmerRequest();
        if (farmerRequest.getIsOtherStateFarmer() == null) {
            farmerRequest.setIsOtherStateFarmer(false);
        }
        FarmerResponse farmerResponse = new FarmerResponse();
        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }
*/
        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true, false));
        if (Objects.nonNull(farmer)) {
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setFruitsId(farmerRequest.getFruitsId());
            farmer.setFirstName(farmerRequest.getFirstName());
            farmer.setMiddleName(farmerRequest.getMiddleName());
            farmer.setTscMasterId(farmerRequest.getTscMasterId());
            farmer.setLastName(farmerRequest.getLastName());
            farmer.setDob(farmerRequest.getDob());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setCasteId(farmerRequest.getCasteId());
            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
            farmer.setEmail(farmerRequest.getEmail());
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
            farmer.setEpicNumber(farmerRequest.getEpicNumber());
            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer.setLandCategoryId(farmerRequest.getLandCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer.setMinority(farmerRequest.getMinority());
            farmer.setRdNumber(farmerRequest.getRdNumber());
            farmer.setCasteStatus(farmerRequest.getCasteStatus());
            farmer.setGenderStatus(farmerRequest.getGenderStatus());
            farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer.setFatherName(farmerRequest.getFatherName());
            farmer.setNameKan(farmerRequest.getNameKan());

            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);

            if (farmerResponse.getFarmerId() > 0) {
                //Save farmer bank acc details
                editCompleteFarmerRequest.getEditFarmerBankAccountRequest().setFarmerId(farmerResponse.getFarmerId());
                FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.updateFarmerBankAccountDetails(editCompleteFarmerRequest.getEditFarmerBankAccountRequest());
                if (farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                }
                if (editCompleteFarmerRequest.getEditFarmerFamilyRequests() != null) {
                    for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerFamilyRequests().size(); i++) {
                        editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                        farmerFamilyService.updateFarmerFamilyDetails(editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i));
                    }
                }

                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerAddressRequests().size(); i++) {
                    editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                    farmerAddressService.updateFarmerAddressDetails(editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i));
                }

//                for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().size(); i++) {
//                    editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
//                    farmerLandDetailsService.updateFarmerLandDetailsDetails(editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i));
//                }

                // Handle farmer land details even when empty
//                List<EditFarmerLandDetailsRequest> landDetailsRequests = editCompleteFarmerRequest.getEditFarmerLandDetailsRequests();
//                if (landDetailsRequests == null || landDetailsRequests.isEmpty()) {
//                    // Create a default land details entry if required, or skip saving.
//                    FarmerLandDetailsRequest defaultLandRequest = new FarmerLandDetailsRequest();
//                    defaultLandRequest.setFarmerId(farmerResponse.getFarmerId());
//                    // Add default or mandatory fields to the land request...
//                    farmerLandDetailsService.insertFarmerLandDetailsDetails(defaultLandRequest);
//                } else {
//                    for (int i = 0; i < editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().size(); i++) {
//                        editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
//                        farmerLandDetailsService.updateFarmerLandDetailsDetails(editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i));
//                    }
//                }
//            }
                // Handle farmer land details
//                List<EditFarmerLandDetailsRequest> landDetailsRequests = editCompleteFarmerRequest.getEditFarmerLandDetailsRequests();
//                if (landDetailsRequests == null || landDetailsRequests.isEmpty()) {
//                    // Create a default land details entry if required
//                    FarmerLandDetailsRequest defaultLandRequest = new FarmerLandDetailsRequest();
//                    defaultLandRequest.setFarmerId(farmerResponse.getFarmerId());
//                    // Set other mandatory fields for the default land entry
//                    // e.g., defaultLandRequest.setLandArea(...);
//                    farmerLandDetailsService.insertFarmerLandDetailsDetails(defaultLandRequest);
//                } else {
//                    // Iterate through land details requests and save them
//                    for (EditFarmerLandDetailsRequest landRequest : landDetailsRequests) {
//                        landRequest.setFarmerId(farmerResponse.getFarmerId());
//                        // Check if farmerLandDetailsId is null to save as new
//                        if (landRequest.getFarmerLandDetailsId() == null) {
//                            farmerLandDetailsService.insertFarmerLandDetailsDetails(landRequest); // Call save method instead of update
//                        } else {
//                            farmerLandDetailsService.updateFarmerLandDetailsDetails(landRequest); // Update if ID exists
//                        }
//                    }
//                }
//            }
// Handle farmer land details
                List<EditFarmerLandDetailsRequest> landDetailsRequests = editCompleteFarmerRequest.getEditFarmerLandDetailsRequests();
                if (landDetailsRequests == null || landDetailsRequests.isEmpty()) {
                    // Create a default land details entry if required
                    FarmerLandDetailsRequest defaultLandRequest = new FarmerLandDetailsRequest();
                    defaultLandRequest.setFarmerId(farmerResponse.getFarmerId());
                    // Set other mandatory fields for the default land entry
                    // e.g., defaultLandRequest.setLandArea(...);
                    farmerLandDetailsService.insertFarmerLandDetailsDetails(defaultLandRequest);
                } else {
                    // Iterate through land details requests and save them
                    for (EditFarmerLandDetailsRequest landRequest : landDetailsRequests) {
                        landRequest.setFarmerId(farmerResponse.getFarmerId());
                        // Check if farmerLandDetailsId is null to save as new
                        if (landRequest.getFarmerLandDetailsId() == null) {
                            // Convert EditFarmerLandDetailsRequest to FarmerLandDetailsRequest
                            FarmerLandDetailsRequest newLandDetailsRequest = farmerLandDetailsService.editToFarmerLandDetailsRequest(landRequest);
                            farmerLandDetailsService.insertFarmerLandDetailsDetails(newLandDetailsRequest); // Call save method instead of update
                        }
//                        else {
//                            // Convert EditFarmerLandDetailsRequest to FarmerLandDetailsRequest for updating
//                            EditFarmerLandDetailsRequest updateLandDetailsRequest = farmerLandDetailsService.editToFarmerLandDetailsRequest(landRequest);
//                            farmerLandDetailsService.updateFarmerLandDetailsDetails(updateLandDetailsRequest); // Update if ID exists
//                        }
                    }
                }
            }


            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerResponse;
    }


    public GetFarmerResponse getFarmerDetails(GetFarmerRequest getFarmerRequest) {
        FarmerResponse farmerResponse = new FarmerResponse();
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
        if (getFarmerRequest.getFarmerNumber() == null || getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        } else {
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        }
        if (farmer == null) {
            getFarmerResponse.setError(true);
            getFarmerResponse.setError_description("Not Found");
        } else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerAddressDTO> farmerAddressDTOList = farmerAddressRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetailsDTO> farmerLandDetailsDTOList = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamilyDTO> farmerFamilyDTOList = farmerFamilyRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);


            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
//        getFarmerResponse.setFarmerDTO(farmerDTO);
//        getFarmerResponse.setFarmerDTOList(farmerDTOList);
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerAddressDTOList(farmerAddressDTOList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerFamilyDTOList(farmerFamilyDTOList);
            getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOList);
            getFarmerResponse.setFarmerBankAccount(farmerBankAccount);

        }
//        FarmerDTO farmerDTO = farmerRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
//        List<FarmerDTO> farmerDTOList = farmerRepository.getByIdAndActive(farmer.getFarmerId(), true);

        return getFarmerResponse;

    }

    public GetFarmerResponse getFarmerDetailsByFruitsId(GetFarmerRequest getFarmerRequest) throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
        if (getFarmerRequest.getFarmerNumber() == null || getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        } else {
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        }
        if (farmer == null) {
            FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
            fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

            //  GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
            String inputData = String.valueOf(fruitsApiService.getFarmerByFruitsId(fruitsFarmerDTO).getBody());



            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            GetFruitsResponse getFruitsResponse = objectMapper.readValue(inputData, GetFruitsResponse.class);

            Farmer farmer1 = new Farmer();
            farmer1.setFruitsId(getFruitsResponse.getFarmerID());
            farmer1.setFirstName(getFruitsResponse.getName());
            farmer1.setMiddleName(getFruitsResponse.getFatherName());

            List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndActive(getFruitsResponse.getFarmerType(), true);
            if (farmerTypeList.size() > 0) {
                farmer1.setFarmerTypeId(farmerTypeList.get(0).getFarmerTypeId());
            }

            farmer1.setMinority(getFruitsResponse.getMinority());
            farmer1.setRdNumber(getFruitsResponse.getRDNumber());
            farmer1.setCasteStatus(getFruitsResponse.getCasteStatus());
            farmer1.setGenderStatus(getFruitsResponse.getGenderStatus());
            farmer1.setFatherNameKan(getFruitsResponse.getFatherNameKan());
            farmer1.setFatherName(getFruitsResponse.getFatherName());
            farmer1.setNameKan(getFruitsResponse.getNameKan());

            // log.info("getFruitsResponse: " + getFruitsResponse);
            // log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
            // log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
            // log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );

            if (getFruitsResponse.getGender().equals("Male")) {
                farmer1.setGenderId(1L);
            } else if (getFruitsResponse.getGender().equals("Female")) {
                farmer1.setGenderId(2L);
            } else {
                farmer1.setGenderId(3L);
            }
            CasteDTO casteDTO = new CasteDTO();
            casteDTO.setCaste(getFruitsResponse.getCaste());
            ResponseWrapper responseWrapper = getCaste(casteDTO);

            farmer1.setCasteId(Long.valueOf(((LinkedHashMap) responseWrapper.getContent()).get("id").toString()));

            if (getFruitsResponse.getPhysicallyChallenged().equals("No")) {
                farmer1.setDifferentlyAbled(false);
            } else {
                farmer1.setDifferentlyAbled(true);
            }
            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer1, FarmerResponse.class));

            List<FarmerAddress> farmerAddressList = new ArrayList<>();
            FarmerAddress farmerAddress = new FarmerAddress();
            farmerAddress.setAddressText(getFruitsResponse.getResidentialAddress());
            farmerAddress.setPincode(getFruitsResponse.getPincode());
            farmerAddressList.add(farmerAddress);
            getFarmerResponse.setFarmerAddressList(farmerAddressList);


            List<FarmerLandDetailsDTO> farmerLandDetailsList = new ArrayList<>();
            for (GetLandDetailsResponse getLandDetailsResponse : getFruitsResponse.getLanddata()) {
                FarmerLandDetailsDTO farmerLandDetails = new FarmerLandDetailsDTO();
                VillageDTO villageDTO = new VillageDTO();
                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);
                // if(responseWrapper1 != null) {
                if (((LinkedHashMap) responseWrapper1.getContent()).get("error").equals(false)) {
                    farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                    farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                    farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                    farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                    farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));

                    farmerLandDetails.setStateName(((LinkedHashMap) responseWrapper1.getContent()).get("stateName").toString());
                    farmerLandDetails.setDistrictName(((LinkedHashMap) responseWrapper1.getContent()).get("districtName").toString());
                    farmerLandDetails.setTalukName(((LinkedHashMap) responseWrapper1.getContent()).get("talukName").toString());
                    farmerLandDetails.setHobliName(((LinkedHashMap) responseWrapper1.getContent()).get("hobliName").toString());
                    farmerLandDetails.setVillageName(((LinkedHashMap) responseWrapper1.getContent()).get("villageName").toString());
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);
                }

                farmerLandDetails.setHissa(getLandDetailsResponse.getHissano());
                farmerLandDetails.setSurveyNumber(String.valueOf(getLandDetailsResponse.getSurveyno()));

                farmerLandDetails.setOwnerName(getLandDetailsResponse.getOwnerName());
                farmerLandDetails.setSurNoc(String.valueOf(getLandDetailsResponse.getSurnoc()));
                farmerLandDetails.setAcre(Long.valueOf(getLandDetailsResponse.getAcre()));
                farmerLandDetails.setNameScore(Long.valueOf(getLandDetailsResponse.getNameScore()));
                farmerLandDetails.setOwnerNo(Long.valueOf(getLandDetailsResponse.getOwnerNo()));
                farmerLandDetails.setMainOwnerNo(Long.valueOf(String.valueOf(getLandDetailsResponse.getMainOwnerNo())));
                farmerLandDetails.setGunta(Long.valueOf(getLandDetailsResponse.getGunta()));
                farmerLandDetails.setFGunta(Double.valueOf(getLandDetailsResponse.getFgunta()));

                farmerLandDetailsList.add(farmerLandDetails);
            }
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(1);
        } else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetailsDTO> farmerLandDetailsDTOS = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOS);
            getFarmerResponse.setIsFruitService(0);
        }

        return getFarmerResponse;
    }

    public GetFarmerResponse getFarmerDetailsByFruitsIdOrFarmerNumberOrMobileNumber(GetFarmerRequest getFarmerRequest) throws Exception {
        log.info("Entered to function");
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
        if (getFarmerRequest.getFarmerNumber() != null && !getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        } else if (getFarmerRequest.getFruitsId() != null && !getFarmerRequest.getFruitsId().equals("")) {
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        } else {
            farmer = farmerRepository.findByMobileNumberAndActive(getFarmerRequest.getMobileNumber(), true);
        }
        if (farmer == null) {
            FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
            fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

            //  GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
            String inputData = String.valueOf(fruitsApiService.getFarmerByFruitsId(fruitsFarmerDTO).getBody());
            log.info("InputData" + inputData);

            if (inputData.equals("Error!, Please try again")) {
                getFarmerResponse.setError(true);
                getFarmerResponse.setError_description("Farmer not found");
            } else {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                GetFruitsResponse getFruitsResponse = objectMapper.readValue(inputData, GetFruitsResponse.class);
                log.info("getFruitsResponse" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getFruitsResponse));

                Farmer farmer1 = new Farmer();
                farmer1.setFruitsId(getFruitsResponse.getFarmerID());
                farmer1.setFirstName(getFruitsResponse.getName());
                farmer1.setMiddleName(getFruitsResponse.getFatherName());

                List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndActive(getFruitsResponse.getFarmerType(), true);
                if (farmerTypeList.size() > 0) {
                    farmer1.setFarmerTypeId(farmerTypeList.get(0).getFarmerTypeId());
                }

                farmer1.setMinority(getFruitsResponse.getMinority());
                farmer1.setRdNumber(getFruitsResponse.getRDNumber());
                farmer1.setCasteStatus(getFruitsResponse.getCasteStatus());
                farmer1.setGenderStatus(getFruitsResponse.getGenderStatus());
                farmer1.setFatherNameKan(getFruitsResponse.getFatherNameKan());
                farmer1.setFatherName(getFruitsResponse.getFatherName());
                farmer1.setNameKan(getFruitsResponse.getNameKan());

                // log.info("getFruitsResponse: " + getFruitsResponse);
                // log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
                // log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
                // log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );
                if (getFruitsResponse.getGender() != null) {
                    if (getFruitsResponse.getGender().equals("Male")) {
                        farmer1.setGenderId(1L);
                    } else if (getFruitsResponse.getGender().equals("Female")) {
                        farmer1.setGenderId(2L);
                    } else {
                        farmer1.setGenderId(3L);
                    }
                } else {
                    farmer1.setGenderId(0L);
                }

            /*CasteDTO casteDTO = new CasteDTO();
            casteDTO.setCaste(getFruitsResponse.getCaste());
            ResponseWrapper responseWrapper = getCaste(casteDTO);

            farmer1.setCasteId(Long.valueOf(((LinkedHashMap) responseWrapper.getContent()).get("id").toString()));
*/
                Caste caste = casteRepository.findByTitleAndActive(getFruitsResponse.getCaste(), true);
                if (caste != null) {
                    farmer1.setCasteId(caste.getCasteId());
                } else {
                    farmer1.setCasteId(0L);
                }

                if (getFruitsResponse.getPhysicallyChallenged().equals("No")) {
                    farmer1.setDifferentlyAbled(false);
                } else {
                    farmer1.setDifferentlyAbled(true);
                }
                getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer1, FarmerResponse.class));

                List<FarmerAddress> farmerAddressList = new ArrayList<>();
                FarmerAddress farmerAddress = new FarmerAddress();
                farmerAddress.setAddressText(getFruitsResponse.getResidentialAddress());
                farmerAddress.setPincode(getFruitsResponse.getPincode());
                farmerAddressList.add(farmerAddress);
                getFarmerResponse.setFarmerAddressList(farmerAddressList);


                List<FarmerLandDetailsDTO> farmerLandDetailsList = new ArrayList<>();
                for (GetLandDetailsResponse getLandDetailsResponse : getFruitsResponse.getLanddata()) {
                    log.info("ENtered inside land details loop");
                    FarmerLandDetailsDTO farmerLandDetails = new FarmerLandDetailsDTO();
//                VillageDTO villageDTO = new VillageDTO();
//                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
//                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);

                    log.info("District code: " + getLandDetailsResponse.getDistrictCode());
                    District district = districtRepository.findByDistrictCodeAndActive(String.valueOf(getLandDetailsResponse.getDistrictCode()), true);
                    if (district != null) {
                        log.info("District name: " + district.getDistrictName() + ":districtId:" + district.getDistrictId() + ":lgDist:" + district.getDistrictCode());
                        log.info("Taluk code: " + getLandDetailsResponse.getTalukCode());
                        Taluk taluk = talukRepository.findByDistrictIdAndTalukCodeAndActive(district.getDistrictId(), String.valueOf(getLandDetailsResponse.getTalukCode()), true);
                        if (taluk != null) {
                            log.info("Taluk name: " + taluk.getTalukName() + ":talukId:" + taluk.getTalukId() + ":districtId" + taluk.getDistrictId() + "lgTaluk:" + taluk.getLgTaluk());
                            log.info("Hobli code: " + getLandDetailsResponse.getHobliCode());
                            Hobli hobli = hobliRepository.findByTalukIdAndHobliCodeAndActive(taluk.getTalukId(), String.valueOf(getLandDetailsResponse.getHobliCode()), true);
                            if (hobli != null) {
                                log.info("Hobli name: " + hobli.getHobliName() + ":hobliId:" + hobli.getHobliId() + ":districtId" + hobli.getDistrictId() + ":talukId:" + hobli.getTalukId());
                                log.info("Village code: " + getLandDetailsResponse.getVillageCode());
                                Village village = villageRepository.findByHobliIdAndVillageCodeAndActive(hobli.getHobliId(), String.valueOf(getLandDetailsResponse.getVillageCode()), true);
                                if (village == null) {
                                    log.info("Village name: " + village.getVillageName() + ":hobliId:" + village.getHobliId() + ":districtId" + village.getDistrictId() + ":talukId:" + village.getTalukId() + ":villageId:" + village.getVillageId() + ":lgVillage:" + village.getLgVillage());
                                    farmerLandDetails.setVillageId(null);
                                    farmerLandDetails.setHobliId(null);
                                    farmerLandDetails.setTalukId(null);
                                    farmerLandDetails.setDistrictId(null);
                                    farmerLandDetails.setStateId(null);

                                    getFarmerResponse.setError(true);
                                    getFarmerResponse.setError_description("Village not found, please create village and then continue");
                                } else {
                                    log.info("Village name: " + village.getVillageName() + ":hobliId:" + village.getHobliId() + ":districtId" + village.getDistrictId() + ":talukId:" + village.getTalukId() + ":villageId:" + village.getVillageId() + ":lgVillage:" + village.getLgVillage());
                                    VillageDTO villageDTO1 = villageRepository.getByVillageIdAndActive(village.getVillageId(), true);
                                    log.info("VillageDTO1: - Village name: " + villageDTO1.getVillageName() + ":hobliId:" + villageDTO1.getHobliId() + ":districtId" + villageDTO1.getDistrictId() + ":talukId:" + villageDTO1.getTalukId() + ":villageId:" + villageDTO1.getVillageId());

                                    farmerLandDetails.setVillageId(villageDTO1.getVillageId());
                                    if (villageDTO1.getHobliId().equals("") || villageDTO1.getHobliId() == null) {
                                        farmerLandDetails.setHobliId(0L);
                                    } else {
                                        farmerLandDetails.setHobliId(villageDTO1.getHobliId());
                                    }
                                    farmerLandDetails.setTalukId(villageDTO1.getTalukId());
                                    farmerLandDetails.setDistrictId(villageDTO1.getDistrictId());
                                    farmerLandDetails.setStateId(villageDTO1.getStateId());

                                    farmerLandDetails.setStateName(villageDTO1.getStateName());
                                    farmerLandDetails.setDistrictName(villageDTO1.getDistrictName());
                                    farmerLandDetails.setTalukName(villageDTO1.getTalukName());
                                    if (villageDTO1.getHobliName().equals("") || villageDTO1.getHobliName() == null) {
                                        farmerLandDetails.setHobliName("");
                                    } else {
                                        farmerLandDetails.setHobliId(villageDTO1.getHobliId());
                                    }
                                    farmerLandDetails.setHobliName(villageDTO1.getHobliName());
                                    farmerLandDetails.setVillageName(villageDTO1.getVillageName());
                                }
                            } else {
                                log.info("Hobli not found ");
                                farmerLandDetails.setVillageId(null);
                                farmerLandDetails.setHobliId(null);
                                farmerLandDetails.setTalukId(null);
                                farmerLandDetails.setDistrictId(null);
                                farmerLandDetails.setStateId(null);

                                getFarmerResponse.setError(true);
                                getFarmerResponse.setError_description("Hobli not found, please create hobli and then continue");
                            }
                        } else {
                            log.info("Taluk not found: " + farmerLandDetails.getTalukCode());
                            farmerLandDetails.setVillageId(null);
                            farmerLandDetails.setHobliId(null);
                            farmerLandDetails.setTalukId(null);
                            farmerLandDetails.setDistrictId(null);
                            farmerLandDetails.setStateId(null);

                            getFarmerResponse.setError(true);
                            getFarmerResponse.setError_description("Taluk not found, please create taluk and then continue");
                        }
                    } else {
                        log.info("District name not found: " + district.getDistrictName());
                        farmerLandDetails.setVillageId(null);
                        farmerLandDetails.setHobliId(null);
                        farmerLandDetails.setTalukId(null);
                        farmerLandDetails.setDistrictId(null);
                        farmerLandDetails.setStateId(null);

                        getFarmerResponse.setError(true);
                        getFarmerResponse.setError_description("District not found, please create district and then continue");
                    }

                /*if(responseWrapper1 != null) {
                if(((LinkedHashMap) responseWrapper1.getContent()).get("error").equals(false)){
                    farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                    farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                    farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                    farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                    farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));

                    farmerLandDetails.setStateName(((LinkedHashMap) responseWrapper1.getContent()).get("stateName").toString());
                    farmerLandDetails.setDistrictName(((LinkedHashMap) responseWrapper1.getContent()).get("districtName").toString());
                    farmerLandDetails.setTalukName(((LinkedHashMap) responseWrapper1.getContent()).get("talukName").toString());
                    farmerLandDetails.setHobliName(((LinkedHashMap) responseWrapper1.getContent()).get("hobliName").toString());
                    farmerLandDetails.setVillageName(((LinkedHashMap) responseWrapper1.getContent()).get("villageName").toString());
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);
                }*/

                    farmerLandDetails.setHissa(getLandDetailsResponse.getHissano());
                    farmerLandDetails.setSurveyNumber(String.valueOf(getLandDetailsResponse.getSurveyno()));

                    farmerLandDetails.setOwnerName(getLandDetailsResponse.getOwnerName());
                    farmerLandDetails.setSurNoc(String.valueOf(getLandDetailsResponse.getSurnoc()));
                    farmerLandDetails.setAcre(Long.valueOf(getLandDetailsResponse.getAcre()));
                    farmerLandDetails.setNameScore(Long.valueOf(getLandDetailsResponse.getNameScore()));
                    farmerLandDetails.setOwnerNo(Long.valueOf(getLandDetailsResponse.getOwnerNo()));
                    farmerLandDetails.setMainOwnerNo(Long.valueOf(String.valueOf(getLandDetailsResponse.getMainOwnerNo())));
                    farmerLandDetails.setGunta(Long.valueOf(getLandDetailsResponse.getGunta()));
                    farmerLandDetails.setFGunta(Double.valueOf(getLandDetailsResponse.getFgunta()));
                    farmerLandDetails.setLandCode(Long.valueOf(getLandDetailsResponse.getLandCode()));
                    farmerLandDetails.setDistrictCode(Long.valueOf(getLandDetailsResponse.getDistrictCode()));
                    farmerLandDetails.setTalukCode(Long.valueOf(getLandDetailsResponse.getTalukCode()));
                    farmerLandDetails.setHobliCode(Long.valueOf(String.valueOf(getLandDetailsResponse.getHobliCode())));
                    farmerLandDetails.setVillageCode(Long.valueOf(getLandDetailsResponse.getVillageCode()));

                    farmerLandDetailsList.add(farmerLandDetails);
                }
                getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsList);
                getFarmerResponse.setIsFruitService(1);
                getFarmerResponse.setError(false);
            }
        } else {
            List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
            SerialCounter serialCounter = new SerialCounter();
            if (serialCounters.size() > 0) {
                serialCounter = serialCounters.get(0);
            } else {
                serialCounter.setFarmerWithoutFruitsAllowedNumber(0L);
            }
            if (farmer.getWithoutFruitsInwardCounter() == null) {
                farmer.setWithoutFruitsInwardCounter(0L);
            }
            if (serialCounter.getFarmerWithoutFruitsAllowedNumber() == null) {
                serialCounter.setFarmerWithoutFruitsAllowedNumber(0L);
            }
            if (farmer.getWithoutFruitsInwardCounter() > serialCounter.getFarmerWithoutFruitsAllowedNumber() && (farmer.getFruitsId().equals("") || farmer.getFruitsId() == null)) {
                getFarmerResponse.setError(true);
                getFarmerResponse.setError_description("Maximum allowance of allotment for farmer is reached. Please come back with fruits id.");
            } else {
                List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
                List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
                List<FarmerLandDetailsDTO> farmerLandDetailsDTOS = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
                List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
                FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

                getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
                getFarmerResponse.setFarmerAddressList(farmerAddressList);
                getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
                getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOS);
                getFarmerResponse.setFarmerBankAccount(farmerBankAccount);
                getFarmerResponse.setIsFruitService(0);
                getFarmerResponse.setError(false);
            }
        }

        return getFarmerResponse;
    }


    public GetFarmerResponse getDetailsByFruitsId(GetFarmerRequest getFarmerRequest) throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
//        if (getFarmerRequest.getFarmerNumber() != null && !getFarmerRequest.getFarmerNumber().equals("")) {
//            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);}
//        else if (getFarmerRequest.getFruitsId() != null && !getFarmerRequest.getFruitsId().equals("")) {
//            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);}
//        else {
//            farmer = farmerRepository.findByMobileNumberAndActive(getFarmerRequest.getMobileNumber(), true);
//        }
//        if (farmer == null) {
        FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
        fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

        //  GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
        String inputData = String.valueOf(fruitsApiService.getFarmerByFruitsId(fruitsFarmerDTO).getBody());

        if (inputData.equals("Error!, Please try again")) {
            getFarmerResponse.setError(true);
            getFarmerResponse.setError_description("Farmer not found");
        } else {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            GetFruitsResponse getFruitsResponse = objectMapper.readValue(inputData, GetFruitsResponse.class);

            Farmer farmer1 = new Farmer();
            farmer1.setFruitsId(getFruitsResponse.getFarmerID());
            farmer1.setFirstName(getFruitsResponse.getName());
            farmer1.setMiddleName(getFruitsResponse.getFatherName());

            List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndActive(getFruitsResponse.getFarmerType(), true);
            if (farmerTypeList.size() > 0) {
                farmer1.setFarmerTypeId(farmerTypeList.get(0).getFarmerTypeId());
            }

            farmer1.setMinority(getFruitsResponse.getMinority());
            farmer1.setRdNumber(getFruitsResponse.getRDNumber());
            farmer1.setCasteStatus(getFruitsResponse.getCasteStatus());
            farmer1.setGenderStatus(getFruitsResponse.getGenderStatus());
            farmer1.setFatherNameKan(getFruitsResponse.getFatherNameKan());
            farmer1.setFatherName(getFruitsResponse.getFatherName());
            farmer1.setNameKan(getFruitsResponse.getNameKan());

            // log.info("getFruitsResponse: " + getFruitsResponse);
            // log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
            // log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
            // log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );
            if (getFruitsResponse.getGender() != null) {
                if (getFruitsResponse.getGender().equals("Male")) {
                    farmer1.setGenderId(1L);
                } else if (getFruitsResponse.getGender().equals("Female")) {
                    farmer1.setGenderId(2L);
                } else {
                    farmer1.setGenderId(3L);
                }
            } else {
                farmer1.setGenderId(0L);
            }

            /*CasteDTO casteDTO = new CasteDTO();
            casteDTO.setCaste(getFruitsResponse.getCaste());
            ResponseWrapper responseWrapper = getCaste(casteDTO);

            farmer1.setCasteId(Long.valueOf(((LinkedHashMap) responseWrapper.getContent()).get("id").toString()));
*/
            Caste caste = casteRepository.findByTitleAndActive(getFruitsResponse.getCaste(), true);
            if (caste != null) {
                farmer1.setCasteId(caste.getCasteId());
            } else {
                farmer1.setCasteId(0L);
            }

            if (getFruitsResponse.getPhysicallyChallenged().equals("No")) {
                farmer1.setDifferentlyAbled(false);
            } else {
                farmer1.setDifferentlyAbled(true);
            }
            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer1, FarmerResponse.class));

            List<FarmerAddressDTO> farmerAddressDTOList = new ArrayList<>();
            FarmerAddressDTO farmerAddressDTO = new FarmerAddressDTO();
            farmerAddressDTO.setAddressText(getFruitsResponse.getResidentialAddress());
            farmerAddressDTO.setPincode(getFruitsResponse.getPincode());
            farmerAddressDTOList.add(farmerAddressDTO);
            getFarmerResponse.setFarmerAddressDTOList(farmerAddressDTOList);


            List<FarmerLandDetailsDTO> farmerLandDetailsList = new ArrayList<>();
            for (GetLandDetailsResponse getLandDetailsResponse : getFruitsResponse.getLanddata()) {
                FarmerLandDetailsDTO farmerLandDetails = new FarmerLandDetailsDTO();
//                VillageDTO villageDTO = new VillageDTO();
//                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
//                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);

//                District district = districtRepository.findByDistrictCodeAndActive(String.valueOf(farmerLandDetails.getDistrictCode()), true);
//                if (district != null) {
//
//                    Taluk taluk = talukRepository.findByDistrictIdAndTalukCodeAndActive(district.getDistrictId(), String.valueOf(farmerLandDetails.getTalukCode()), true);
//                    if (taluk != null) {
//                        Hobli hobli = hobliRepository.findByTalukIdAndHobliCodeAndActive(taluk.getTalukId(), String.valueOf(farmerLandDetails.getHobliCode()), true);
//                        if (hobli != null) {
//
//                            Village village = villageRepository.findByHobliIdAndVillageCodeAndActive(hobli.getHobliId(), String.valueOf(farmerLandDetails.getVillageCode()), true);
                District district = districtRepository.findByDistrictCodeAndActive(String.valueOf(getLandDetailsResponse.getDistrictCode()), true);
                if (district != null) {

                    Taluk taluk = talukRepository.findByDistrictIdAndTalukCodeAndActive(district.getDistrictId(), String.valueOf(getLandDetailsResponse.getTalukCode()), true);
                    if (taluk != null) {
                        Hobli hobli = hobliRepository.findByTalukIdAndHobliCodeAndActive(taluk.getTalukId(), String.valueOf(getLandDetailsResponse.getHobliCode()), true);
                        if (hobli != null) {

                            Village village = villageRepository.findByHobliIdAndVillageCodeAndActive(hobli.getHobliId(), String.valueOf(getLandDetailsResponse.getVillageCode()), true);
                            if (village == null) {
                                farmerLandDetails.setVillageId(null);
                                farmerLandDetails.setHobliId(null);
                                farmerLandDetails.setTalukId(null);
                                farmerLandDetails.setDistrictId(null);
                                farmerLandDetails.setStateId(null);

                                getFarmerResponse.setError(true);
                                getFarmerResponse.setError_description("Village not found, please create village and then continue");
                            } else {
                                VillageDTO villageDTO1 = villageRepository.getByVillageIdAndActive(village.getVillageId(), true);
                                farmerLandDetails.setVillageId(villageDTO1.getVillageId());
                                if (villageDTO1.getHobliId().equals("") || villageDTO1.getHobliId() == null) {
                                    farmerLandDetails.setHobliId(0L);
                                } else {
                                    farmerLandDetails.setHobliId(villageDTO1.getHobliId());
                                }
                                farmerLandDetails.setTalukId(villageDTO1.getTalukId());
                                farmerLandDetails.setDistrictId(villageDTO1.getDistrictId());
                                farmerLandDetails.setStateId(villageDTO1.getStateId());

                                farmerLandDetails.setStateName(villageDTO1.getStateName());
                                farmerLandDetails.setDistrictName(villageDTO1.getDistrictName());
                                farmerLandDetails.setTalukName(villageDTO1.getTalukName());
                                if (villageDTO1.getHobliName().equals("") || villageDTO1.getHobliName() == null) {
                                    farmerLandDetails.setHobliName("");
                                } else {
                                    farmerLandDetails.setHobliName(villageDTO1.getHobliName());
                                }
                                farmerLandDetails.setVillageName(villageDTO1.getVillageName());
                            }
                        } else {
                            farmerLandDetails.setVillageId(null);
                            farmerLandDetails.setHobliId(null);
                            farmerLandDetails.setTalukId(null);
                            farmerLandDetails.setDistrictId(null);
                            farmerLandDetails.setStateId(null);

                            getFarmerResponse.setError(true);
                            getFarmerResponse.setError_description("Hobli not found, please create hobli and then continue");
                        }
                    } else {
                        farmerLandDetails.setVillageId(null);
                        farmerLandDetails.setHobliId(null);
                        farmerLandDetails.setTalukId(null);
                        farmerLandDetails.setDistrictId(null);
                        farmerLandDetails.setStateId(null);

                        getFarmerResponse.setError(true);
                        getFarmerResponse.setError_description("Taluk not found, please create taluk and then continue");
                    }
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);

                    getFarmerResponse.setError(true);
                    getFarmerResponse.setError_description("District not found, please create district and then continue");
                }

                /*if(responseWrapper1 != null) {
                if(((LinkedHashMap) responseWrapper1.getContent()).get("error").equals(false)){
                    farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                    farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                    farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                    farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                    farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));

                    farmerLandDetails.setStateName(((LinkedHashMap) responseWrapper1.getContent()).get("stateName").toString());
                    farmerLandDetails.setDistrictName(((LinkedHashMap) responseWrapper1.getContent()).get("districtName").toString());
                    farmerLandDetails.setTalukName(((LinkedHashMap) responseWrapper1.getContent()).get("talukName").toString());
                    farmerLandDetails.setHobliName(((LinkedHashMap) responseWrapper1.getContent()).get("hobliName").toString());
                    farmerLandDetails.setVillageName(((LinkedHashMap) responseWrapper1.getContent()).get("villageName").toString());
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);
                }*/

                farmerLandDetails.setHissa(getLandDetailsResponse.getHissano());
                farmerLandDetails.setSurveyNumber(String.valueOf(getLandDetailsResponse.getSurveyno()));

                farmerLandDetails.setOwnerName(getLandDetailsResponse.getOwnerName());
                farmerLandDetails.setSurNoc(String.valueOf(getLandDetailsResponse.getSurnoc()));
                farmerLandDetails.setAcre(Long.valueOf(getLandDetailsResponse.getAcre()));
                farmerLandDetails.setNameScore(Long.valueOf(getLandDetailsResponse.getNameScore()));
                farmerLandDetails.setOwnerNo(Long.valueOf(getLandDetailsResponse.getOwnerNo()));
                farmerLandDetails.setMainOwnerNo(Long.valueOf(String.valueOf(getLandDetailsResponse.getMainOwnerNo())));
                farmerLandDetails.setGunta(Long.valueOf(getLandDetailsResponse.getGunta()));
                farmerLandDetails.setFGunta(Double.valueOf(getLandDetailsResponse.getFgunta()));
                farmerLandDetails.setLandCode(Long.valueOf(getLandDetailsResponse.getLandCode()));
                farmerLandDetails.setDistrictCode(Long.valueOf(getLandDetailsResponse.getDistrictCode()));
                farmerLandDetails.setTalukCode(Long.valueOf(getLandDetailsResponse.getTalukCode()));
                farmerLandDetails.setHobliCode(Long.valueOf(String.valueOf(getLandDetailsResponse.getHobliCode())));
                farmerLandDetails.setVillageCode(Long.valueOf(getLandDetailsResponse.getVillageCode()));

                farmerLandDetails.setDistrictName(getLandDetailsResponse.getDistrictName());

                farmerLandDetailsList.add(farmerLandDetails);
            }
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(1);
            getFarmerResponse.setError(false);
        }

        return getFarmerResponse;
    }

    public GetFarmerResponse getFarmerDetailsByFruitsIdTest(GetFarmerRequest getFarmerRequest) {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        if (farmer == null) {
            getFarmerResponse.setIsFruitService(1);
        } else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(0);
        }

        return getFarmerResponse;
    }

    public ResponseWrapper getCaste(CasteDTO body) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        try {
            String uri = "http://localhost:8001/master-data/v1/" + "caste/get-by-title";
            //String uri = "http://13.200.62.144:8001/master-data/v1/" + "caste/get-by-title";

            log.info("Caste REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper1 = new ObjectMapper();

            HttpEntity<String> request = new HttpEntity<>(mapper1.writeValueAsString(body), headers);

            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<ResponseWrapper> result = restTemplate.postForEntity(uri, request, ResponseWrapper.class);

            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("CASTE ERROR: " + e.getMessage());
            return responseWrapper;
        }
    }

    public ResponseWrapper getVillageDetails(VillageDTO body) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        try {
            String uri = "http://localhost:8001/master-data/v1/" + "village/get-details-by-village-name";
            //String uri = "http://13.200.62.144:8001/master-data/v1/" + "village/get-details-by-village-name";
            log.info("Caste REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper1 = new ObjectMapper();

            HttpEntity<String> request = new HttpEntity<>(mapper1.writeValueAsString(body), headers);

            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<ResponseWrapper> result = restTemplate.postForEntity(uri, request, ResponseWrapper.class);

            return result.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("VILLAGE ERROR: " + e.getMessage());
            return responseWrapper;
        }
    }

    @Transactional
    public GetFarmerResponse test() throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        String input = "{\n" +
                "    \"StatusCode\": 1,\n" +
                "    \"StatusText\": \"Success\",\n" +
                "    \"FarmerID\": \"FID2806000009439\",\n" +
                "    \"name\": \"MUNIHANUMAPPA\",\n" +
                "    \"NameKan\": \"MUNIHANUMAPPA\",\n" +
                "    \"FatherName\": \"Munivenkatappa\",\n" +
                "    \"FatherNameKan\": \"Munivenkatappa\",\n" +
                "    \"Gender\": \"Male\",\n" +
                "    \"GenderStatus\": \"Declared\",\n" +
                "    \"Caste\": \"SC\",\n" +
                "    \"CasteStatus\": \"Declared\",\n" +
                "    \"RDNumber\": \"\",\n" +
                "    \"PhysicallyChallenged\": \"No\",\n" +
                "    \"Minority\": \"No\",\n" +
                "    \"FarmerType\": \"Margin Farmer\",\n" +
                "    \"ResidentialAddress\": \"Kempadenahlli  Village ambajidurga Hobli Chintamani Taluk\",\n" +
                "    \"Pincode\": \"\",\n" +
                "    \"Landdata\": [\n" +
                "        {\n" +
                "            \"DistrictName\": \"Chikkaballapur\",\n" +
                "            \"TalukName\": \"Chinthamani\",\n" +
                "            \"HobliName\": \"Ambajidurga\",\n" +
                "            \"VillageName\": \"Kempadenahalli\",\n" +
                "            \"OwnerName\": \"ಮುನಿಹನುಮಪ್ಪ\", \n" +
                "            \"NameScore\": 100,\n" +
                "            \"Surveyno\": 37, \n" +
                "            \"Surnoc\": \"*\",\n" +
                "            \"Hissano\": \"*\",\n" +
                "            \"OwnerNo\": 16,\n" +
                "            \"MainOwnerNo\": 16,\n" +
                "            \"Acre\": 0,\n" +
                "            \"Gunta\": 26,\n" +
                "            \"Fgunta\": 10.67\n" +
                "        },\n" +
                "        {\n" +
                "            \"DistrictName\": \"Chikkaballapur\",\n" +
                "            \"TalukName\": \"Chinthamani\",\n" +
                "            \"HobliName\": \"Ambajidurga\",\n" +
                "            \"VillageName\": \"Kempadenahalli\",\n" +
                "            \"OwnerName\": \"ಮುನಿಹನುಮಪ್ಪ\",\n" +
                "            \"NameScore\": 100,\n" +
                "            \"Surveyno\": 37,\n" +
                "            \"Surnoc\": \"*\",\n" +
                "            \"Hissano\": \"*\",\n" +
                "            \"OwnerNo\": 42,\n" +
                "            \"MainOwnerNo\": 42,\n" +
                "            \"Acre\": 1,\n" +
                "            \"Gunta\": 0,\n" +
                "            \"Fgunta\": 0\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        GetFruitsResponse getFruitsResponse = objectMapper.readValue(input, GetFruitsResponse.class);

        String name = getFruitsResponse.getName();

        return getFarmerResponse;
    }

    public FarmerResponse getByFarmerIdJoin(int farmerId) {
        FarmerResponse farmerResponse = new FarmerResponse();
        FarmerDTO farmerDTO = farmerRepository.getByFarmerIdAndActive(farmerId, true);
        if (farmerDTO == null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid id");
        } else {
            farmerResponse = mapper.farmerDTOToObject(farmerDTO, FarmerResponse.class);
            farmerResponse.setError(false);
        }
        log.info("Entity is ", farmerDTO);
        return farmerResponse;
    }

    //    @Transactional(isolation = Isolation.READ_COMMITTED)
//    public Map<String,Object> getByIdJoin(int farmerId){
//        List<FarmerDTO> farmerDTO = farmerRepository.getByIdAndActive(farmerId, true);
//        if(farmerDTO.isEmpty()){
//            throw new ValidationException("Farmer  not found by Farmer Id");
//        }
//        return convertListDTOToMapResponse(farmerDTO);
//    }
//
//    private Map<String, Object> convertListDTOToMapResponse(List<FarmerDTO> farmerDTOList) {
//        Map<String, Object> response = new HashMap<>();
//        List<FarmerResponse> farmerResponse = farmerDTOList.stream()
//                .map(farmerDTO -> mapper.farmerDTOToObject(farmerDTO, FarmerResponse.class)).collect(Collectors.toList());
//        response.put("farmer", farmerResponse);
//        response.put("totalItems", farmerDTOList.size());
//        return response;
//    }
    public Map<String, Object> getPaginatedFarmerDetailsWithJoin(final Pageable pageable) {
        return convertDTOToMapResponse(farmerRepository.getByActiveOrderByFarmerIdAsc(true, pageable));
    }

    //    public Map<String, Object> getPaginatedFarmerDetailsWithJoinWithFilters(final Pageable pageable, int type, String searchText, int joinColumnType) {
//        Page<FarmerDTO> page;
//        if (searchText == null || searchText.equals("")) {
//            searchText = "%%";
//        } else {
//            searchText = "%" + searchText + "%";
//        }
//        String joinColumn = "";
//
//        if (joinColumnType == 0) {
//            joinColumn = "farmer.farmerNumber";
//        } else if (joinColumnType == 1) {
//            joinColumn = "farmer.fruitsId";
//        } else {
//            joinColumn = "farmer.mobileNumber";
//        }
//
//        if (type == 0) {
//            page = farmerRepository.getByActiveOrderByFarmerIdAsc(true, joinColumn, searchText, pageable);
//        } else if (type == 1) {
//            page = farmerRepository.getByActiveOrderByFarmerIdAscForNonKAFarmers(true, joinColumn, searchText, pageable);
//        } else if (type == 2) {
//            page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(true, joinColumn, searchText, pageable);
//        } else {
//            page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsId(true, joinColumn, searchText, pageable);
//        }
//        return convertDTOToMapResponse(page);
//    }

    private Long normalizeFilter(Long value) {
        return (value != null && value == 0) ? null : value;
    }
    public Map<String, Object> getPaginatedFarmerDetailsWithJoinWithFilters(final Pageable pageable, int type, String searchText, int joinColumnType) {
        Page<FarmerDTO> page;

        // Handle null or empty searchText
        if (StringUtils.hasText(searchText)) {
            searchText = "%" + searchText + "%";
        } else {
            searchText = "%%";
        }

        // Determine the join column based on joinColumnType
        String joinColumn;
        switch (joinColumnType) {
            case 0:
                joinColumn = "farmer.farmerNumber";
                break;
            case 1:
                joinColumn = "farmer.fruitsId";
                break;
            case 2:
                joinColumn = "farmer.mobileNumber";
                break;
            case 3:
                joinColumn = "farmerBankAccount.farmerBankAccountNumber";
                break;
            default:
                throw new IllegalArgumentException("Invalid joinColumnType: " + joinColumnType);
        }

        // Handle different type values
        switch (type) {
            case 0:
                page = farmerRepository.getByActiveOrderByFarmerIdAsc(true, joinColumn, searchText, pageable);
                break;
            case 1:
                page = farmerRepository.getByActiveOrderByFarmerIdAscForNonKAFarmers(true, joinColumn, searchText, pageable);
                break;
            case 2:
                page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(true, joinColumn, searchText, pageable);
                break;
            case 3:
                page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(true, joinColumn, searchText, pageable);
                break;
            default:
                page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsId(true, joinColumn, searchText, pageable);
                break;
        }

        return convertDTOToMapResponse(page);
    }

    public ResponseEntity<?> kaFarmersWithoutFruitsIds(Long stateId, Long districtId, Long talukId, Long hobliId, Long casteId,
                                                       int pageNumber, int pageSize) {


        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<FarmerResponse> responseList = new ArrayList<>();

        stateId = normalizeFilter(stateId);
        districtId = normalizeFilter(districtId);
        talukId = normalizeFilter(talukId);
        hobliId = normalizeFilter(hobliId);
        casteId = normalizeFilter(casteId);


        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FarmerDTO> page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsIds(
                true, stateId, districtId, talukId, hobliId, casteId, pageable);

        mapFarmerResponses(responseList, page.getContent(), pageNumber, pageSize);

        rw.setTotalRecords(page.getTotalElements());
        rw.setContent(responseList);
        return ResponseEntity.ok(rw);
    }

    public ResponseEntity<?> nonKaFarmers(Long stateId, Long districtId, Long talukId, Long hobliId, Long casteId,
                                          int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<FarmerResponse> responseList = new ArrayList<>();

        stateId = normalizeFilter(stateId);
        districtId = normalizeFilter(districtId);
        talukId = normalizeFilter(talukId);
        hobliId = normalizeFilter(hobliId);
        casteId = normalizeFilter(casteId);


        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FarmerDTO> page = farmerRepository.getByActiveOrderByFarmerIdAscForNonKAFarmersList(
                true, stateId, districtId, talukId, hobliId, casteId, pageable);

        mapFarmerResponses(responseList, page.getContent(), pageNumber, pageSize);

        rw.setTotalRecords(page.getTotalElements());
        rw.setContent(responseList);
        return ResponseEntity.ok(rw);
    }



    public FileInputStream kaFarmersWithoutFruitsIdsReport(Long stateId, Long districtId, Long talukId, Long hobliId, Long casteId,
                                                           boolean isActive, int pageNumber, int pageSize) throws Exception {
        stateId = normalizeFilter(stateId);
        districtId = normalizeFilter(districtId);
        talukId = normalizeFilter(talukId);
        hobliId = normalizeFilter(hobliId);
        casteId = normalizeFilter(casteId);

        Pageable pageable = null;
        Page<FarmerDTO> page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsIds(
                isActive, stateId, districtId, talukId, hobliId, casteId, pageable);

        return exportFarmerReport(page.getContent(), "ka_farmers_report");

    }

    public FileInputStream nonKaFarmersReport(Long stateId, Long districtId, Long talukId, Long hobliId, Long casteId,
                                              boolean isActive, int pageNumber, int pageSize) throws Exception {
        stateId = normalizeFilter(stateId);
        districtId = normalizeFilter(districtId);
        talukId = normalizeFilter(talukId);
        hobliId = normalizeFilter(hobliId);
        casteId = normalizeFilter(casteId);


        Pageable pageable = null;
        Page<FarmerDTO> page = farmerRepository.getByActiveOrderByFarmerIdAscForNonKAFarmersList(
                isActive, stateId, districtId, talukId, hobliId, casteId,  pageable);

        return exportFarmerReport(page.getContent(), "non_ka_farmers_report");
    }




    private static void mapFarmerResponses(List<FarmerResponse> responseList, List<FarmerDTO> dtoList,
                                           int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (FarmerDTO dto : dtoList) {
            FarmerResponse response = FarmerResponse.builder()
                    .serialNumber(serialNumber++)
                    .farmerId(dto.getFarmerId())
                    .farmerNumber(dto.getFarmerNumber())
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .mobileNumber(dto.getMobileNumber())
                    .aadhaarNumber(dto.getAadhaarNumber())
                    .farmerTypeName(dto.getFarmerTypeName())
                    .title(dto.getTitle())



                    .build();
            responseList.add(response);
        }
    }

    private FileInputStream exportFarmerReport(List<FarmerDTO> farmers, String filePrefix) throws Exception {
        // SXSSFWorkbook streams rows to a temp file instead of holding the whole
        // workbook in heap memory, which is what was causing large exports to OOM the server.
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        try {
            Sheet sheet = workbook.createSheet("Farmers");

            // Fixed widths instead of autoSizeColumn: autoSizeColumn forces POI to render
            // every cell with AWT font metrics, which is extremely slow/memory-heavy on
            // large sheets (and doesn't work correctly with streaming rows anyway).
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 25 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 18 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 18 * 256);

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("S.No");
            headerRow.createCell(1).setCellValue("Farmer Number");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Mobile");
            headerRow.createCell(4).setCellValue("Aadhaar");
            headerRow.createCell(5).setCellValue("Farmer Type");
            headerRow.createCell(6).setCellValue("Caste");


            // Data
            int rowIdx = 1, serial = 1;
            for (FarmerDTO dto : farmers) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(serial++);
                row.createCell(1).setCellValue(dto.getFarmerNumber());
                row.createCell(2).setCellValue(dto.getFirstName() + " " + dto.getLastName());
                row.createCell(3).setCellValue(dto.getMobileNumber());
                row.createCell(4).setCellValue(dto.getAadhaarNumber());
                row.createCell(5).setCellValue(dto.getFarmerTypeName());
                row.createCell(6).setCellValue(dto.getTitle());

            }

            String userHome = System.getProperty("user.home");
            Path directory = Paths.get(userHome, "Downloads");
            Files.createDirectories(directory);
            Path filePath = directory.resolve(filePrefix + "_" + Util.getISTLocalDate() + ".xlsx");

            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }

            return new FileInputStream(filePath.toFile());
        } finally {
            // dispose() removes the backing temp files SXSSF writes to disk while streaming
            workbook.dispose();
            workbook.close();
        }
    }



    private Map<String, Object> convertDTOToMapResponse(final Page<FarmerDTO> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerDTOToObject(farmer, FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer", farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());
        return response;
    }



    public Map<String, Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest) {
        if (searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")) {
            searchWithSortRequest.setSearchText("%%");
        } else {
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if (searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")) {
            searchWithSortRequest.setSortColumn("firstName");
        }
        if (searchWithSortRequest.getSortOrder() == null || searchWithSortRequest.getSortOrder().equals("")) {
            searchWithSortRequest.setSortOrder("asc");
        }
        if (searchWithSortRequest.getPageNumber() == null || searchWithSortRequest.getPageNumber().equals("")) {
            searchWithSortRequest.setPageNumber("0");
        }
        if (searchWithSortRequest.getPageSize() == null || searchWithSortRequest.getPageSize().equals("")) {
            searchWithSortRequest.setPageSize("5");
        }
        if (searchWithSortRequest.getFarmerType() == null || searchWithSortRequest.getFarmerType().equals("")) {
            searchWithSortRequest.setFarmerType("0");
        }
        Sort sort;
        if (searchWithSortRequest.getSortOrder().equals("asc")) {
            sort = Sort.by(Sort.Direction.ASC, searchWithSortRequest.getSortColumn());
        } else {
            sort = Sort.by(Sort.Direction.DESC, searchWithSortRequest.getSortColumn());
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(searchWithSortRequest.getPageNumber()), Integer.parseInt(searchWithSortRequest.getPageSize()), sort);
        Page<FarmerDTO> farmerDTOS;
        //if(searchWithSortRequest.getFarmerType().equals("0")) {
        farmerDTOS = farmerRepository.getSortedFarmers(searchWithSortRequest.getJoinColumn(), searchWithSortRequest.getSearchText(), true, pageable);
        // }else if(searchWithSortRequest.getFarmerType().equals("1")){
//        }else{
//            farmerDTOS = farmerRepository.getSortedFarmersForKAWithFruits(searchWithSortRequest.getJoinColumn(), searchWithSortRequest.getSearchText(), true, pageable);
//        }/*else if(searchWithSortRequest.getFarmerType().equals("2")){

        //}else{

        //}*/
        log.info("Entity is ", farmerDTOS);
        return convertPageableDTOToMapResponse(farmerDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<FarmerDTO> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerDTOToObject(farmer, FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer", farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());

        return response;
    }

    @Transactional
    public FarmerResponse updatePhotoPath(MultipartFile multipartFile, String farmerId) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(Long.parseLong(farmerId), true);
        if (Objects.nonNull(farmer)) {
            UUID uuid = UUID.randomUUID();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            String fileName = "farmer/" + farmerId + "_" + uuid + "_" + extension;
            s3Controller.uploadFile(multipartFile, fileName);
            farmer.setPhotoPath(fileName);
            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }
        return farmerResponse;
    }

//    @Transactional
//    public FarmerResponse insertNonKarnatakaFarmers(NonKarnatakaFarmerRequest farmerRequest) throws Exception {
//        Farmer farmer2 = new Farmer();
//        Long farmerId;
//        FarmerRequest farmer1 = new FarmerRequest();
//        farmerRequest.setIsOtherStateFarmer(true);
//        FarmerResponse farmerResponse = new FarmerResponse();
//        farmer1.setIsOtherStateFarmer(true);
//        farmer1.setFirstName(farmerRequest.getFirstName());
//        farmer1.setMiddleName(farmerRequest.getMiddleName());
//        farmer1.setLastName(farmerRequest.getLastName());
//        farmer1.setNameKan(farmerRequest.getNameKan());
//        farmer1.setFatherName(farmerRequest.getFatherName());
//        farmer1.setFatherNameKan(farmerRequest.getFatherNameKan());
//        farmer1.setDob(farmerRequest.getDob());
//        farmer1.setCasteId(farmerRequest.getCasteId());
//        farmer1.setMobileNumber(farmerRequest.getMobileNumber());
//        farmer1.setEpicNumber(farmerRequest.getEpicNumber());
//        farmer1.setPassbookNumber(farmerRequest.getPassbookNumber());
//        farmer1.setFarmerTypeId(farmerRequest.getFarmerTypeId());
//        farmer1.setFarmerNumber(farmerRequest.getFarmerNumber());
//        farmer1.setGenderId(farmerRequest.getGenderId());
//
//        LocalDate today = Util.getISTLocalDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
//        String formattedDate = today.format(formatter);
////        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
////        SerialCounter serialCounter = new SerialCounter();
////        if (serialCounters.size() > 0) {
////            serialCounter = serialCounters.get(0);
////            long counterValue = 1L;
////            if (serialCounter.getOtherStateFarmerCounter() != null) {
////                counterValue = serialCounter.getOtherStateFarmerCounter() + 1;
////            }
////            serialCounter.setOtherStateFarmerCounter(counterValue);
////        } else {
////            serialCounter.setOtherStateFarmerCounter(1L);
////        }
////        serialCounterRepository.save(serialCounter);
////        String formattedNumber = String.format("%05d", serialCounter.getOtherStateFarmerCounter());
//
////        farmer1.setFarmerNumber(formattedNumber);
//

    /// /        UUID uuid = UUID.randomUUID();
    /// /        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
    /// /        String fileName = "farmer/" + uuid + "_" + extension;
    /// /        s3Controller.uploadFile(multipartFile, fileName);
    /// /        farmer1.setPhotoPath(fileName);
//
//        Farmer farmer = mapper.farmerObjectToEntity(farmer1, Farmer.class);
//        farmer.setWithoutFruitsInwardCounter(0L);
//        validator.validate(farmer);
//
//
//        // Check for duplicate Reeler Number
//        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
//        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Farmer Mobile Number already exists");
//        } else {
//            // If no duplicates found, save the reeler
//            farmer2 = farmerRepository.save(farmer);
//            farmerResponse = mapper.farmerEntityToObject(farmer2, FarmerResponse.class);
//            farmerResponse.setError(false);
//        }
//
//        if (!farmerResponse.getError()) {
//            farmerId = farmer2.getFarmerId();
//
//            for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
//                farmerAddress.setFarmerId(farmerId);
//                farmerAddress.setDefaultAddress(true);
//                farmerAddressRepository.save(farmerAddress);
//            }
//
//            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
//            if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
//                farmerResponse.setError(true);
//                farmerResponse.setError_description("FarmerBankAccount number already exist");
//            } else {
//                farmerRequest.getFarmerBankAccount().setFarmerId(farmerId);
//                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
//                farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());
//            }
//        }
//
//        return farmerResponse;
//    }
    @Transactional
    public FarmerResponse insertNonKarnatakaFarmers(NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();

        try {
            // Initialize FarmerRequest
            FarmerRequest farmer1 = new FarmerRequest();
            farmerRequest.setIsOtherStateFarmer(true);
            farmer1.setIsOtherStateFarmer(true);
            farmer1.setFirstName(farmerRequest.getFirstName());
            farmer1.setMiddleName(farmerRequest.getMiddleName());
            farmer1.setLastName(farmerRequest.getLastName());
            farmer1.setNameKan(farmerRequest.getNameKan());
            farmer1.setFatherName(farmerRequest.getFatherName());
            farmer1.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer1.setDob(farmerRequest.getDob());
            farmer1.setCasteId(farmerRequest.getCasteId());
            farmer1.setMobileNumber(farmerRequest.getMobileNumber());
            farmer1.setEpicNumber(farmerRequest.getEpicNumber());
            farmer1.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer1.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer1.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer1.setGenderId(farmerRequest.getGenderId());

            // Formatting Date
            LocalDate today = Util.getISTLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            String formattedDate = today.format(formatter);

            // Convert to Farmer Entity
            Farmer farmer = mapper.farmerObjectToEntity(farmer1, Farmer.class);
            farmer.setWithoutFruitsInwardCounter(0L);
            validator.validate(farmer);

            // Check for duplicate Mobile Number
            List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
            if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
                throw new Exception("Farmer Mobile Number already exists");
            }

            // Save Farmer
            Farmer savedFarmer = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(savedFarmer, FarmerResponse.class);
            farmerResponse.setError(false);

            Long farmerId = savedFarmer.getFarmerId();

            // Save Farmer Addresses
            for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
                farmerAddress.setFarmerId(farmerId);
                farmerAddress.setDefaultAddress(true);
                farmerAddressRepository.save(farmerAddress);
            }

            // Check for duplicate Bank Account
            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
            if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
                throw new Exception("FarmerBankAccount number already exists");
            }

            // Save Farmer Bank Account
            farmerRequest.getFarmerBankAccount().setFarmerId(farmerId);
            FarmerBankAccount savedFarmerBankAccount = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
            farmerResponse.setFarmerBankAccountId(savedFarmerBankAccount.getFarmerBankAccountId());

        } catch (Exception e) {
            farmerResponse.setError(true);
            farmerResponse.setError_description(e.getMessage());
            throw new ValidationException(String.format(e.getMessage()));
        }

        return farmerResponse;
    }


   /* @Transactional
    public FarmerResponse updateNonKarnatakaFarmer(EditNonKarnatakaFarmerRequest farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();

        // Retrieve the existing farmer entity
        Optional<Farmer> optionalFarmer = farmerRepository.findByFarmerId(farmerRequest.getFarmerId());
        if (!optionalFarmer.isPresent()) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer not found");
            return farmerResponse;
        }

        Farmer farmer = optionalFarmer.get();

        // Update farmer basic details
        farmer.setFirstName(farmerRequest.getFirstName());
        farmer.setMiddleName(farmerRequest.getMiddleName());
        farmer.setLastName(farmerRequest.getLastName());
        farmer.setNameKan(farmerRequest.getNameKan());
        farmer.setFatherName(farmerRequest.getFatherName());
        farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
        farmer.setDob(farmerRequest.getDob());
        farmer.setCasteId(farmerRequest.getCasteId());
        farmer.setMobileNumber(farmerRequest.getMobileNumber());
        farmer.setEpicNumber(farmerRequest.getEpicNumber());
        farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
        farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());

        validator.validate(farmer);

        // Check for duplicate mobile number
        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(f -> !f.getFarmerId().equals(farmerRequest.getFarmerId()) && f.getActive())) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Mobile Number already exists");
            return farmerResponse;
        }

        // Update farmer entity
        farmerRepository.save(farmer);

        // Update farmer addresses
        for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
            farmerAddress.setFarmerId(farmerRequest.getFarmerId());
            farmerAddressRepository.save(farmerAddress);
        }

        // Update farmer bank account
        List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
        if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().anyMatch(fba -> !fba.getFarmerId().equals(farmerRequest.getFarmerId()) && fba.getActive())) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Bank Account number already exists");
            return farmerResponse;
        } else {
            FarmerBankAccount existingFarmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmerRequest.getFarmerId(), true);
            if (existingFarmerBankAccount != null) {
                existingFarmerBankAccount.setFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
                existingFarmerBankAccount.setFarmerBankName(farmerRequest.getFarmerBankAccount().getFarmerBankName());
                existingFarmerBankAccount.setFarmerBankBranchName(farmerRequest.getFarmerBankAccount().getFarmerBankBranchName());
                existingFarmerBankAccount.setFarmerBankIfscCode(farmerRequest.getFarmerBankAccount().getFarmerBankIfscCode());
                farmerBankAccountRepository.save(existingFarmerBankAccount);
            } else {
                farmerRequest.getFarmerBankAccount().setFarmerId(farmerRequest.getFarmerId());
                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
                farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());
            }
        }

        farmerResponse.setError(false);
        farmerResponse = mapper.farmerEntityToObject(farmer, FarmerResponse.class);

        return farmerResponse;
    }
*/

//    @Transactional
//    public FarmerResponse editNonKarnatakaFarmers(EditNonKarnatakaFarmerRequest farmerRequest) throws Exception {
//        Farmer farmer2 = new Farmer();
//        FarmerResponse farmerResponse = new FarmerResponse();
//
//        Optional<Farmer> optionalFarmer = farmerRepository.findByFarmerId(farmerRequest.getFarmerId());
//        if (!optionalFarmer.isPresent()) {
//            farmerResponse.setError(true);
//            farmerResponse.setError_description("Farmer not found");
//            return farmerResponse;
//        }
//
//        Farmer farmer = optionalFarmer.get();
//        farmer.setFirstName(farmerRequest.getFirstName());
//        farmer.setMiddleName(farmerRequest.getMiddleName());
//        farmer.setLastName(farmerRequest.getLastName());
//        farmer.setNameKan(farmerRequest.getNameKan());
//        farmer.setFatherName(farmerRequest.getFatherName());
//        farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
//        farmer.setDob(farmerRequest.getDob());
//        farmer.setCasteId(farmerRequest.getCasteId());
//        farmer.setMobileNumber(farmerRequest.getMobileNumber());
//        farmer.setEpicNumber(farmerRequest.getEpicNumber());
//        farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
//        farmer.setWithoutFruitsInwardCounter(0L);
//        validator.validate(farmer);
//
//        // Check for duplicate Reeler Number
////        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
////        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
////            farmerResponse.setError(true);
////            farmerResponse.setError_description("Farmer Mobile Number already exists");
////        } else {
//            // If no duplicates found, save the reeler
//            farmer2 = farmerRepository.save(farmer);
//            farmerResponse = mapper.farmerEntityToObject(farmer2, FarmerResponse.class);
//            farmerResponse.setError(false);
////        }
//
//        if(!farmerResponse.getError()) {
//
//            for (EditFarmerAddressRequest editFarmerAddressRequest : farmerRequest.getEditFarmerAddressRequestList()) {
//                FarmerAddress farmerAddress = mapper.editFarmerAddressObjectToEntity(editFarmerAddressRequest,FarmerAddress.class);
//                farmerAddressRepository.save(farmerAddress);
//            }
//
////            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getEditFarmerBankAccountRequest().getFarmerBankAccountNumber());
////            if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
////                farmerResponse.setError(true);
////                farmerResponse.setError_description("FarmerBankAccount number already exist");
////            } else {
//                FarmerBankAccount farmerBankAccount = mapper.editFarmerBankAccountObjectToEntity(farmerRequest.getEditFarmerBankAccountRequest(),FarmerBankAccount.class);
//                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerBankAccount);
//                farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());

    /// /            }
//        }
//
//        return farmerResponse;
//    }
    public FarmerResponse editNonKarnatakaFarmers(EditNonKarnatakaFarmerRequest farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();

        Optional<Farmer> optionalFarmer = farmerRepository.findByFarmerId(farmerRequest.getFarmerId());
        if (!optionalFarmer.isPresent()) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer not found");
            return farmerResponse;
        }

        Farmer farmer = optionalFarmer.get();
        farmer.setFirstName(farmerRequest.getFirstName());
        farmer.setMiddleName(farmerRequest.getMiddleName());
        farmer.setLastName(farmerRequest.getLastName());
        farmer.setNameKan(farmerRequest.getNameKan());
        farmer.setFatherName(farmerRequest.getFatherName());
        farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
        farmer.setDob(farmerRequest.getDob());
        farmer.setCasteId(farmerRequest.getCasteId());
        farmer.setMobileNumber(farmerRequest.getMobileNumber());
        farmer.setEpicNumber(farmerRequest.getEpicNumber());
        farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
        farmer.setWithoutFruitsInwardCounter(0L);
        farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
        farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
        farmer.setGenderId(farmerRequest.getGenderId());
        farmer.setNameKan(farmerRequest.getNameKan());
        farmer.setPhotoPath(farmerRequest.getPhotoPath());

        // Validate the farmer entity
        validator.validate(farmer);

        // Save the updated farmer details
        Farmer updatedFarmer = farmerRepository.save(farmer);
        farmerResponse = mapper.farmerEntityToObject(updatedFarmer, FarmerResponse.class);
        farmerResponse.setError(false);

        long farmerId = farmerResponse.getFarmerId();
        if (farmerId > 0) {
            // Update farmer bank account details if provided
            EditFarmerBankAccountRequest editFarmerBankAccountRequest = farmerRequest.getEditFarmerBankAccountRequest();
            if (editFarmerBankAccountRequest != null && editFarmerBankAccountRequest.getFarmerBankAccountId() != 0) {
                editFarmerBankAccountRequest.setFarmerId(farmerId);
                FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.updateFarmerBankAccountDetails(editFarmerBankAccountRequest);
                if (farmerBankAccountResponse != null && farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                } else {
                    farmerResponse.setError(true);
                    farmerResponse.setError_description("Failed to update farmer bank account details");
                    // Optionally handle the error or throw an exception
                }
            } else {
                FarmerBankAccountRequest farmerBankAccountRequest = new FarmerBankAccountRequest();
                farmerBankAccountRequest.setFarmerId(farmerId);
                farmerBankAccountRequest.setFarmerBankName(editFarmerBankAccountRequest.getFarmerBankName());
                farmerBankAccountRequest.setFarmerBankBranchName(editFarmerBankAccountRequest.getFarmerBankBranchName());
                farmerBankAccountRequest.setFarmerBankAccountNumber(editFarmerBankAccountRequest.getFarmerBankAccountNumber());
                farmerBankAccountRequest.setFarmerBankIfscCode(editFarmerBankAccountRequest.getFarmerBankIfscCode());
                farmerBankAccountRequest.setAccountImagePath(editFarmerBankAccountRequest.getAccountImagePath());
                FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.insertFarmerBankAccountDetails(farmerBankAccountRequest);
                if (farmerBankAccountResponse != null && farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                } else {
                    farmerResponse.setError(true);
                    farmerResponse.setError_description("Failed to Save farmer bank account details");
                    // Optionally handle the error or throw an exception
                }

            }

            // Update farmer address details if provided
            EditFarmerAddressRequest addressRequest = farmerRequest.getEditFarmerAddressRequest();
            if (addressRequest != null) {
                addressRequest.setFarmerId(farmerId);
                farmerAddressService.updateFarmerAddressDetails(addressRequest);
            }
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while updating Farmer details");
        }

        return farmerResponse;
    }

    @Transactional
    public FarmerResponse editKarnatakaFarmerWithoutFruitsIdDetails(EditNonKarnatakaFarmerRequest farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();

        Optional<Farmer> optionalFarmer = farmerRepository.findByFarmerId(farmerRequest.getFarmerId());
        if (!optionalFarmer.isPresent()) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer not found");
            return farmerResponse;
        }

        Farmer farmer = optionalFarmer.get();
        farmer.setFirstName(farmerRequest.getFirstName());
        farmer.setMiddleName(farmerRequest.getMiddleName());
        farmer.setLastName(farmerRequest.getLastName());
        farmer.setNameKan(farmerRequest.getNameKan());
        farmer.setFatherName(farmerRequest.getFatherName());
        farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
        farmer.setDob(farmerRequest.getDob());
        farmer.setCasteId(farmerRequest.getCasteId());
        farmer.setMobileNumber(farmerRequest.getMobileNumber());
        farmer.setEpicNumber(farmerRequest.getEpicNumber());
        farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
        farmer.setWithoutFruitsInwardCounter(0L);
        farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
        farmer.setGenderId(farmerRequest.getGenderId());
        farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());

        // Validate the farmer entity
        validator.validate(farmer);

        // Save the updated farmer details
        Farmer updatedFarmer = farmerRepository.save(farmer);
        farmerResponse = mapper.farmerEntityToObject(updatedFarmer, FarmerResponse.class);
        farmerResponse.setError(false);

        long farmerId = farmerResponse.getFarmerId();
        if (farmerId > 0) {
            // Update farmer bank account details if provided
            EditFarmerBankAccountRequest editFarmerBankAccountRequest = farmerRequest.getEditFarmerBankAccountRequest();
            if (editFarmerBankAccountRequest != null) {
                editFarmerBankAccountRequest.setFarmerId(farmerId);
                FarmerBankAccountResponse farmerBankAccountResponse = farmerBankAccountService.updateFarmerBankAccountDetails(editFarmerBankAccountRequest);
                if (farmerBankAccountResponse != null && farmerBankAccountResponse.getFarmerBankAccountId() > 0) {
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                } else {
                    farmerResponse.setError(true);
                    farmerResponse.setError_description("Failed to update farmer bank account details");
                    // Optionally handle the error or throw an exception
                }
            }

            // Update farmer address details if provided
            EditFarmerAddressRequest addressRequest = farmerRequest.getEditFarmerAddressRequest();
            if (addressRequest != null) {
                addressRequest.setFarmerId(farmerId);
                farmerAddressService.updateFarmerAddressDetails(addressRequest);
            }
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while updating Farmer details");
        }

        return farmerResponse;
    }


    @Transactional
    public FarmerResponse insertKarnatakaFarmersWithoutFruitsId(NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        Farmer farmer2 = new Farmer();
        Long farmerId;
        FarmerRequest farmer1 = new FarmerRequest();
        FarmerResponse farmerResponse = new FarmerResponse();
        try {
            farmerRequest.setIsOtherStateFarmer(false);
            farmer1.setIsOtherStateFarmer(false);
            farmer1.setFirstName(farmerRequest.getFirstName());
            farmer1.setMiddleName(farmerRequest.getMiddleName());
            farmer1.setLastName(farmerRequest.getLastName());
            farmer1.setNameKan(farmerRequest.getNameKan());
            farmer1.setFatherName(farmerRequest.getFatherName());
            farmer1.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer1.setDob(farmerRequest.getDob());
            farmer1.setCasteId(farmerRequest.getCasteId());
            farmer1.setMobileNumber(farmerRequest.getMobileNumber());
            farmer1.setEpicNumber(farmerRequest.getEpicNumber());
            farmer1.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer1.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer1.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer1.setGenderId(farmerRequest.getGenderId());

            LocalDate today = Util.getISTLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            String formattedDate = today.format(formatter);
            List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
            SerialCounter serialCounter = new SerialCounter();
            if (serialCounters.size() > 0) {
                serialCounter = serialCounters.get(0);
                long counterValue = 1L;
                if (serialCounter.getFarmerFruitsIdCounterNumber() != null) {
                    counterValue = serialCounter.getFarmerFruitsIdCounterNumber() + 1;
                }
                serialCounter.setFarmerFruitsIdCounterNumber(counterValue);
            } else {
                serialCounter.setFarmerFruitsIdCounterNumber(1L);
            }
            serialCounterRepository.save(serialCounter);
            String formattedNumber = String.format("%05d", serialCounter.getFarmerFruitsIdCounterNumber());

            farmer1.setFarmerNumber("KSWFID" + formattedNumber);

//        UUID uuid = UUID.randomUUID();
//        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
//        String fileName = "farmer/" + uuid + "_" + extension;
//        s3Controller.uploadFile(multipartFile, fileName);
//        farmer1.setPhotoPath(fileName);

            Farmer farmer = mapper.farmerObjectToEntity(farmer1, Farmer.class);
            farmer.setWithoutFruitsInwardCounter(1L);
//           farmer.setWithoutFruitsInwardCounter(farmerRequest.getWithoutFruitsInwardCounter());
            validator.validate(farmer);


            // Check for duplicate Reeler Number
            List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
            if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
                throw new Exception("Farmer Mobile Number already exists");
            } else {
                // If no duplicates found, save the reeler
                farmer2 = farmerRepository.save(farmer);
                farmerResponse = mapper.farmerEntityToObject(farmer2, FarmerResponse.class);
                farmerResponse.setError(false);
            }

            if (!farmerResponse.getError()) {
                farmerId = farmer2.getFarmerId();

                for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
                    farmerAddress.setFarmerId(farmerId);
                    farmerAddress.setDefaultAddress(true);
                    farmerAddressRepository.save(farmerAddress);
                }

                List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
                if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
                    throw new Exception("FarmerBankAccount number already exists");
                } else {
                    farmerRequest.getFarmerBankAccount().setFarmerId(farmerId);
                    FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
                    farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());
                }
            }
        } catch (Exception e) {
            farmerResponse.setError(true);
            farmerResponse.setError_description(e.getMessage());
            throw new ValidationException(String.format(e.getMessage()));
        }

        return farmerResponse;
    }

    @Transactional
    public FarmerResponse configureFruitsIdAllowedNoCounter(FruitsIdAllowedCounterRequest farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();
        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
        SerialCounter serialCounter = new SerialCounter();
        if (serialCounters.size() > 0) {
            serialCounter = serialCounters.get(0);
        }
        serialCounter.setFarmerWithoutFruitsAllowedNumber(farmerRequest.getAllowedNoOfAttempts());
        serialCounterRepository.save(serialCounter);
        farmerResponse.setError(false);
        return farmerResponse;
    }

    @Transactional
    public FarmerResponse getConfiguredInward() {
        FarmerResponse farmerResponse = new FarmerResponse();
        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
        SerialCounter serialCounter = new SerialCounter();
        if (serialCounters.size() > 0) {
            serialCounter = serialCounters.get(0);
        } else {
            serialCounter.setFarmerWithoutFruitsAllowedNumber(0L);
        }

        farmerResponse.setError(false);
        farmerResponse.setConfiguredInward(serialCounter.getFarmerWithoutFruitsAllowedNumber());
        return farmerResponse;
    }

    @Transactional
    public FarmerResponse updateFarmerWithoutFruitsIdCounter(UpdateFruitsIdAllowedCounter farmerRequest) throws Exception {
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(farmerRequest.getFarmerId(), true);
        if (farmer == null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occured while fetching farmer details");
        } else {
            if (farmer.getWithoutFruitsInwardCounter() == null) {
                farmer.setWithoutFruitsInwardCounter(1L);
            } else {
                farmer.setWithoutFruitsInwardCounter(farmer.getWithoutFruitsInwardCounter() + 1L);
            }
            farmerRepository.save(farmer);
            farmerResponse.setError(false);
        }
        return farmerResponse;
    }

    public ResponseEntity<?> totalFarmerCount() {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<FarmerTotalCountResponse> farmerTotalCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = farmerRepository.getFarmerCountDetails();
        for (Object[] arr : applicableList) {
            FarmerTotalCountResponse farmerTotalCountResponse;
            farmerTotalCountResponse = FarmerTotalCountResponse.builder().
                    totalFarmerCount(Util.objectToString(arr[0]))

                    .build();
            farmerTotalCountResponseList.add(farmerTotalCountResponse);
        }
        rw.setContent(farmerTotalCountResponseList);

        return ResponseEntity.ok(rw);

    }


    public ResponseEntity<?> districtWiseFarmerCount() {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<DistrictWiseFarmerCountResponse> districtWiseFarmerCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = farmerRepository.getDistrictWiseCount();
        for (Object[] arr : applicableList) {
            DistrictWiseFarmerCountResponse districtWiseFarmerCountResponse;
            districtWiseFarmerCountResponse = DistrictWiseFarmerCountResponse.builder().
                    districtName(Util.objectToString(arr[0]))
                    .farmerCount(Util.objectToString(arr[1]))
                    .build();
            districtWiseFarmerCountResponseList.add(districtWiseFarmerCountResponse);
        }
        rw.setContent(districtWiseFarmerCountResponseList);

        return ResponseEntity.ok(rw);

    }

    public ResponseEntity<?> talukWise(ApplicationsDetailsDistrictWiseRequest applicationsDetailsDistrictWiseRequest) {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<TalukWiseFarmerCountResponse> talukWiseFarmerCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = farmerRepository.getTalukWise(applicationsDetailsDistrictWiseRequest.getDistrictId());
        for (Object[] arr : applicableList) {
            TalukWiseFarmerCountResponse talukWiseFarmerCountResponse;
            talukWiseFarmerCountResponse = TalukWiseFarmerCountResponse.builder().
                    talukName(Util.objectToString(arr[0]))
                    .farmerCount(Util.objectToString(arr[1]))


                    .build();
            talukWiseFarmerCountResponseList.add(talukWiseFarmerCountResponse);
        }
        rw.setContent(talukWiseFarmerCountResponseList);

        return ResponseEntity.ok(rw);

    }

    public ResponseEntity<?> primaryFarmerDetails(Long districtId,
                                                  Long talukId,
                                                  Long villageId,
                                                  Long tscMasterId,
                                                  Long casteId,
                                                  String landFilter,
                                                  int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryDetailsResponse> primaryDetailsResponseList = new ArrayList<>();

        districtId = (districtId != null && districtId == 0) ? null : districtId;
        talukId = (talukId != null && talukId == 0) ? null : talukId;
        villageId = (villageId != null && villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId != null && tscMasterId == 0) ? null : tscMasterId;
        casteId = (casteId != null && casteId == 0) ? null : casteId;


        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<FarmerPrimaryDetailsProjection> applicablePage = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, casteId, pageable);
        List<FarmerPrimaryDetailsProjection> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();


        farmerResponse(primaryDetailsResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(primaryDetailsResponseList);
        return ResponseEntity.ok(rw);
    }

    private static void farmerResponse(List<PrimaryDetailsResponse> primaryDetailsResponseList, List<FarmerPrimaryDetailsProjection> applicableList, int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (FarmerPrimaryDetailsProjection p : applicableList) {
            PrimaryDetailsResponse primaryDetailsResponse = PrimaryDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .farmerId(Util.objectToString(p.getFarmerId()))
                    .firstName(p.getFirstName())
                    .middleName(p.getMiddleName())
                    .lastName(p.getLastName())
                    .fruitsId(p.getFruitsId())
                    .farmerNumber(p.getFarmerNumber())
                    .fatherName(p.getFatherName())
                    .passbookNumber(p.getPassbookNumber())
                    .epicNumber(p.getEpicNumber())
                    .rationCardNumber(p.getRationCardNumber())
                    .dob(Util.objectToString(p.getDob()))
                    .districtName(p.getDistrictName())
                    .talukName(p.getTalukName())
                    .hobliName(p.getHobliName())
                    .villageName(p.getVillageName())
                    .farmerBankName(p.getFarmerBankName())
                    .farmerBankAccountNumber(p.getFarmerBankAccountNumber())
                    .farmerBankBranchName(p.getFarmerBankBranchName())
                    .farmerBankIfscCode(p.getFarmerBankIfscCode())
                    .caste(p.getCasteTitle())

                    .mulberryArea(Util.objectToString(p.getMulberryArea()))
                    .ownerName(p.getOwnerName())
                    .surveyNumber(p.getSurveyNumber())
                    .spacing(p.getSpacing())
                    .hissa(p.getHissa())
                    .rearingHouseDetails(p.getRearingHouseDetails())
                    .landAddress(p.getAddress())
                    .mulberryVarietyName(p.getMulberryVarietyName())
                    .mobileNumber(p.getMobileNumber())
                    .tscName(p.getTscName())
                    .build();
            primaryDetailsResponseList.add(primaryDetailsResponse);
        }
    }

    public FileInputStream farmerReport(Long districtId,
                                        Long talukId,
                                        Long villageId,
                                        Long tscMasterId,
                                        Long casteId,
                                        String landFilter,
                                        int pageNumber, int pageSize) throws Exception {
        List<PrimaryDetailsResponse> primaryDetailsResponseList = new ArrayList<>();


        Page<FarmerPrimaryDetailsProjection> applicablePage;
        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;
        casteId = (casteId == 0) ? null : casteId;

        Pageable pageable = Pageable.unpaged();
        applicablePage = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, casteId, pageable);
        List<FarmerPrimaryDetailsProjection> applicableList = applicablePage.getContent();
        farmerResponse(primaryDetailsResponseList, applicableList, pageNumber, pageSize);

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Farmer Registration Report");
        String[] headerLabels = {
            "First Name", "Middle Name",
            // "Last Name",
            "Fruits Id", "Farmer Number",
            "Father Name",
            // "Passbook Number", "Epic Number", "Ration Card Number",
            "DOB",
            "District Name", "Taluk Name",
            // "Hobli Name", "Village Name", "Bank Name",
            // "Bank Account Number", "Branch Name", "IFSC Code",
            "Caste",
            // "Mulberry Area", "Owner Name", "Survey Number", "Spacing", "Hissa",
            // "Rearing House Details",
            "Address",
            // "Mulberry Variety Name",
            "Mobile Number", "TSC Name"
        };
        final int TOTAL_COLS = headerLabels.length;

        // ── Colors (created once, safe with SXSSFWorkbook) ───────────────────
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)26,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)12,  (byte)74,  (byte)158}, null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)30,  (byte)58,  (byte)95},  null);

        // ── Fonts (created once) ─────────────────────────────────────────────
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setFontName("Calibri"); titleFont.setFontHeightInPoints((short)16);
        titleFont.setBold(true); titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setFontName("Calibri"); subFont.setFontHeightInPoints((short)11);
        subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setFontName("Calibri"); hdrFont.setFontHeightInPoints((short)11);
        hdrFont.setBold(true); hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontName("Calibri"); dataFont.setFontHeightInPoints((short)10);
        dataFont.setColor(darkText);

        // ── Styles (created once before loop) ────────────────────────────────
        XSSFColor black = new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0}, null);

        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(darkNavy);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setTopBorderColor(black);
        titleStyle.setBottomBorderColor(black);
        titleStyle.setLeftBorderColor(black);
        titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFont(subFont);
        subStyle.setFillForegroundColor(primaryBlue);
        subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER);
        subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setBorderTop(BorderStyle.THIN);
        subStyle.setBorderBottom(BorderStyle.THIN);
        subStyle.setBorderLeft(BorderStyle.THIN);
        subStyle.setBorderRight(BorderStyle.THIN);
        subStyle.setTopBorderColor(black);
        subStyle.setBottomBorderColor(black);
        subStyle.setLeftBorderColor(black);
        subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFont(hdrFont);
        hdrStyle.setFillForegroundColor(primaryBlue);
        hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER);
        hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN);
        hdrStyle.setBorderBottom(BorderStyle.THIN);
        hdrStyle.setBorderLeft(BorderStyle.THIN);
        hdrStyle.setBorderRight(BorderStyle.THIN);
        hdrStyle.setTopBorderColor(black);
        hdrStyle.setBottomBorderColor(black);
        hdrStyle.setLeftBorderColor(black);
        hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFont(dataFont);
        dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setVerticalAlignment(VerticalAlignment.CENTER);
        dataWhite.setWrapText(true);
        dataWhite.setBorderTop(BorderStyle.THIN);
        dataWhite.setBorderBottom(BorderStyle.THIN);
        dataWhite.setBorderLeft(BorderStyle.THIN);
        dataWhite.setBorderRight(BorderStyle.THIN);
        dataWhite.setTopBorderColor(black);
        dataWhite.setBottomBorderColor(black);
        dataWhite.setLeftBorderColor(black);
        dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.cloneStyleFrom(dataWhite);
        dataAlt.setFillForegroundColor(altRow);
        dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ── Row 0: Department title ───────────────────────────────────────────
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(36);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Department of Sericulture, Government of Karnataka");
        titleCell.setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { titleRow.createCell(c).setCellStyle(titleStyle); }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        // ── Row 1: Report name ────────────────────────────────────────────────
        Row reportRow = sheet.createRow(1);
        reportRow.setHeightInPoints(24);
        Cell reportCell = reportRow.createCell(0);
        reportCell.setCellValue("FARMER REGISTRATION REPORT");
        reportCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { reportRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        // ── Row 2: Generated on ───────────────────────────────────────────────
        Row genRow = sheet.createRow(2);
        genRow.setHeightInPoints(20);
        Cell genCell = genRow.createCell(0);
        genCell.setCellValue("Generated On: " + new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new java.util.Date()));
        genCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { genRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLS - 1));

        // ── Row 3: Column headers ─────────────────────────────────────────────
        Row headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints(36);
        for (int i = 0; i < headerLabels.length; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(headerLabels[i]);
            hCell.setCellStyle(hdrStyle);
        }

        // ── Rows 4+: Data (styles reused per row, not recreated) ─────────────
        int dataStartsFrom = 4;
        for (int i = 0; i < primaryDetailsResponseList.size(); i++) {
            Row contentRow = sheet.createRow(dataStartsFrom);
            // No fixed height here (unlike the title rows above): wrap text is on, so
            // leaving row height unset lets Excel auto-expand the row for long values
            // when the file is opened, instead of clipping wrapped text at a fixed 20pt.
            PrimaryDetailsResponse p = primaryDetailsResponseList.get(i);
            XSSFCellStyle rowStyle = (i % 2 != 0) ? dataAlt : dataWhite;
            String[] values = {
                p.getFirstName(), p.getMiddleName(),
                // p.getLastName(),
                p.getFruitsId(), p.getFarmerNumber(),
                p.getFatherName(),
                // p.getPassbookNumber(), p.getEpicNumber(), p.getRationCardNumber(),
                p.getDob(),
                p.getDistrictName(), p.getTalukName(),
                // p.getHobliName(), p.getVillageName(), p.getFarmerBankName(),
                // p.getFarmerBankAccountNumber(), p.getFarmerBankBranchName(), p.getFarmerBankIfscCode(),
                p.getCaste(),
                // p.getMulberryArea(), p.getOwnerName(), p.getSurveyNumber(), p.getSpacing(), p.getHissa(),
                // p.getRearingHouseDetails(),
                p.getLandAddress(),
                // p.getMulberryVarietyName(),
                p.getMobileNumber(), p.getTscName()
            };
            for (int c = 0; c < values.length; c++) {
                Cell dataCell = contentRow.createCell(c);
                dataCell.setCellValue(values[c] != null ? values[c] : "");
                dataCell.setCellStyle(rowStyle);
            }
            dataStartsFrom++;
        }

        sheet.createFreezePane(0, 4);

        for (int columnIndex = 0; columnIndex < TOTAL_COLS; columnIndex++) {
            sheet.setColumnWidth(columnIndex, 20 * 256);
        }

        // Write the workbook content to a file
        // Specify the directory where the file will be saved
        //String directoryPath = "C:\\Users\\Swathi V S\\Downloads\\";
        // Specify the directory where the file will be saved
        String userHome = System.getProperty("user.home");

        // Define the directory path relative to the user's home directory
        String directoryPath = Paths.get(userHome, "Downloads").toString();
        Path directory = Paths.get(directoryPath);
        Files.createDirectories(directory);
        Path filePath = directory.resolve("farmers" + Util.getISTLocalDate() + ".xlsx");

        // Write the workbook content to the specified file path
        try {
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
                workbook.write(fileOut);
            }
        } finally {
            // dispose() removes the backing temp files SXSSF writes to disk while streaming
            workbook.dispose();
            workbook.close();
        }
        return new FileInputStream(filePath.toString());
    }

    public List<FarmerDetailsResponse> getFarmerDetailsByFruitsIdOrMobileNumberOrCsbRegisterNumber(SearchRequest searchRequest) throws Exception {
        List<Object[]> objects = new ArrayList<>();
        List<FarmerDetailsResponse> farmerDetailsResponseList = new ArrayList<>();

        // Check the type and fetch farmer details
        if (Objects.equals(searchRequest.getType(), "fruitsId")) {
            if (searchRequest.getText() != null && !searchRequest.getText().isEmpty()) {
                objects = farmerRepository.getFarmerDetailsForSeedCocoonMarket(searchRequest.getText(), searchRequest.getType());
                if (objects.isEmpty()) {
                    throw new ValidationException("Invalid Fruits Id");
                }
            }
        } else if (Objects.equals(searchRequest.getType(), "mobileNumber")) {
            if (searchRequest.getText() != null && !searchRequest.getText().isEmpty()) {
                objects = farmerRepository.getFarmerDetailsForSeedCocoonMarket(searchRequest.getText(), searchRequest.getType());
                if (objects.isEmpty()) {
                    throw new ValidationException("Invalid Mobile number");
                }
            }
        } else if (Objects.equals(searchRequest.getType(), "farmerNumber")) {
            if (searchRequest.getText() != null && !searchRequest.getText().isEmpty()) {
                objects = farmerRepository.getFarmerDetailsForSeedCocoonMarket(searchRequest.getText(), searchRequest.getType());
                if (objects.isEmpty()) {
                    throw new ValidationException("Invalid Farmer Number");
                }
            }
        } else {
            throw new ValidationException("Invalid search type");
        }

        // Map the result to FarmerDetailsResponse
        JwtPayloadData jwtPayloadData = Util.getTokenValues();
        Integer marketId = Util.getMarketId(jwtPayloadData);
        boolean isOnlinePayment = false;
        if (marketId != null) {
            MarketMaster market = marketMasterRepository.findByMarketMasterIdAndActive(marketId, true);
            isOnlinePayment = market != null && "online".equalsIgnoreCase(market.getPaymentMode());
        }

        if (!objects.isEmpty()) {
            for (int i = 0; i < objects.size(); i++) {

                Object[] arr = objects.get(i);

                if (isOnlinePayment) {
                    Boolean lock = false;
                    if (arr.length > 21 && arr[21] != null) {
                        String val = arr[21].toString().trim();

                        if (val.equalsIgnoreCase("true") || val.equals("1")) {
                            lock = true;
                        }
                    }
                    if (!lock) {
                        throw new ValidationException("Bank Lock is not enabled.....");
                    }
                }

                FarmerDetailsResponse farmerDetailsResponse = FarmerDetailsResponse.builder()
                        .farmerId(Util.objectToLong(arr[0]))
                        .firstName(Util.objectToString(arr[1]))
                        .middleName(Util.objectToString(arr[2]))
                        .lastName(Util.objectToString(arr[3]))
                        .fruitsId(Util.objectToString(arr[4]))
                        .farmerNumber(Util.objectToString(arr[5]))
                        .fatherName(Util.objectToString(arr[6]))
                        .dob(Util.objectToString(arr[7]))
                        .mobileNumber(Util.objectToString(arr[8]))
                        .districtName(Util.objectToString(arr[9]))
                        .talukName(Util.objectToString(arr[10]))
                        .hobliName(Util.objectToString(arr[11]))
                        .villageName(Util.objectToString(arr[12]))
                        .tscName(Util.objectToString(arr[13]))
                        .dflsSource(Util.objectToString(arr[14]))
                        .numbersOfDfls(Util.objectToString(arr[15]))
                        .lotNumberRsp(Util.objectToString(arr[16]))
                        .stateName(Util.objectToString(arr[17]))
                        .raceOfDfls(Util.objectToLong(arr[18]))
                        .raceName(Util.objectToString(arr[19]))
                        .fitnessCertificatePath(Util.objectToString(arr[20]))
                        .build();
                farmerDetailsResponseList.add(farmerDetailsResponse);
            }
        }

        return farmerDetailsResponseList;
    }


    public ResponseEntity<?> primaryChowkiDetails(Long districtId,
                                                  Long talukId,
                                                  Long villageId,
                                                  Long tscMasterId,
                                                  int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<ChowkiManagementResponse> chowkiResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = chowkiManagementRepository.getChowkiDetails(districtId, talukId, villageId, tscMasterId, pageable);

        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();

        chowkiResponse(chowkiResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(chowkiResponseList);
        return ResponseEntity.ok(rw);
    }

    private static void chowkiResponse(List<ChowkiManagementResponse> chowkiResponseList,
                                       List<Object[]> applicableList,
                                       int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (Object[] arr : applicableList) {
            ChowkiManagementResponse response = ChowkiManagementResponse.builder()
                    .serialNumber(serialNumber++)
                    .chowkiId(Util.objectToInteger(arr[0]))
                    .farmerName(Util.objectToString(arr[1]))
                    .fatherName(Util.objectToString(arr[2]))
                    .fruitsId(Util.objectToString(arr[3]))
                    .dflsSource(Util.objectToString(arr[4]))
                    .raceId(Util.objectToLong(arr[5]))
                    .raceName(Util.objectToString(arr[6]))
                    .numbersOfDfls(Util.objectToLong(arr[7]))
                    .lotNumberRsp(Util.objectToString(arr[8]))
                    .lotNumberCrc(Util.objectToString(arr[9]))
                    .villageName(Util.objectToString(arr[10]))
                    .districtName(Util.objectToString(arr[11]))
                    .stateName(Util.objectToString(arr[12]))
                    .talukName(Util.objectToString(arr[13]))
                    .hobliName(Util.objectToString(arr[14]))
                    .tscName(Util.objectToString(arr[15]))
                    .soldAfter1stOr2ndMould(Util.objectToString(arr[22]))
                    .ratePer100Dfls(Util.objectToFloat(arr[23]))
                    .price(Util.objectToFloat(arr[24]))
                    .hatchingDateForReport(Util.objectToString(arr[25]))
                    .dispatchDateForReport(Util.objectToString(arr[26]))
                    .farmerId(Util.objectToLong(arr[27]))
                    .isVerified(Util.objectToInteger(arr[28]))
                    .receiptNo(Util.objectToString(arr[29]))
                    .build();
            chowkiResponseList.add(response);
        }
    }

    public FileInputStream chowkiReport(Long districtId,
                                        Long talukId,
                                        Long villageId,
                                        Long tscMasterId,
                                        int pageNumber,
                                        int pageSize) throws Exception {
        List<ChowkiManagementResponse> chowkiResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;

        Pageable pageable = null; // fetch all records
        Page<Object[]> applicablePage = chowkiManagementRepository.getChowkiDetails(districtId, talukId, villageId, tscMasterId, pageable);

        chowkiResponse(chowkiResponseList, applicablePage.getContent(), pageNumber, pageSize);

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Chowki Management Report");
        String[] headerLabels = {
            "Sl.No", "Farmer Name", "Father Name", "Fruits Id",
            "Source Of DFLs", "Race Of DFLs", "Race Name", "Numbers Of DFLs",
            "Lot No RSP", "Lot No CRC", "Village Name", "District Name",
            "State Name", "Taluk Name", "Hobli Name", "TSC Name",
            "Sold After Mould", "Rate Per 100 DFLs", "Price",
            "Hatching Date", "Dispatch Date", "Receipt No"
        };
        final int TOTAL_COLS = headerLabels.length;

        // ── Colors (created once, safe with SXSSFWorkbook) ───────────────────
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)26,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)12,  (byte)74,  (byte)158}, null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)30,  (byte)58,  (byte)95},  null);

        // ── Fonts (created once) ─────────────────────────────────────────────
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setFontName("Calibri"); titleFont.setFontHeightInPoints((short)16);
        titleFont.setBold(true); titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setFontName("Calibri"); subFont.setFontHeightInPoints((short)11);
        subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setFontName("Calibri"); hdrFont.setFontHeightInPoints((short)11);
        hdrFont.setBold(true); hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontName("Calibri"); dataFont.setFontHeightInPoints((short)10);
        dataFont.setColor(darkText);

        // ── Styles (created once before loop) ────────────────────────────────
        XSSFColor black = new XSSFColor(new byte[]{(byte)0, (byte)0, (byte)0}, null);

        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(darkNavy);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setTopBorderColor(black);
        titleStyle.setBottomBorderColor(black);
        titleStyle.setLeftBorderColor(black);
        titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFont(subFont);
        subStyle.setFillForegroundColor(primaryBlue);
        subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER);
        subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setBorderTop(BorderStyle.THIN);
        subStyle.setBorderBottom(BorderStyle.THIN);
        subStyle.setBorderLeft(BorderStyle.THIN);
        subStyle.setBorderRight(BorderStyle.THIN);
        subStyle.setTopBorderColor(black);
        subStyle.setBottomBorderColor(black);
        subStyle.setLeftBorderColor(black);
        subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFont(hdrFont);
        hdrStyle.setFillForegroundColor(primaryBlue);
        hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER);
        hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN);
        hdrStyle.setBorderBottom(BorderStyle.THIN);
        hdrStyle.setBorderLeft(BorderStyle.THIN);
        hdrStyle.setBorderRight(BorderStyle.THIN);
        hdrStyle.setTopBorderColor(black);
        hdrStyle.setBottomBorderColor(black);
        hdrStyle.setLeftBorderColor(black);
        hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFont(dataFont);
        dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setVerticalAlignment(VerticalAlignment.CENTER);
        dataWhite.setWrapText(true);
        dataWhite.setBorderTop(BorderStyle.THIN);
        dataWhite.setBorderBottom(BorderStyle.THIN);
        dataWhite.setBorderLeft(BorderStyle.THIN);
        dataWhite.setBorderRight(BorderStyle.THIN);
        dataWhite.setTopBorderColor(black);
        dataWhite.setBottomBorderColor(black);
        dataWhite.setLeftBorderColor(black);
        dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.cloneStyleFrom(dataWhite);
        dataAlt.setFillForegroundColor(altRow);
        dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ── Row 0: Department title ───────────────────────────────────────────
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(36);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Department of Sericulture, Government of Karnataka");
        titleCell.setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { titleRow.createCell(c).setCellStyle(titleStyle); }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        // ── Row 1: Report name ────────────────────────────────────────────────
        Row reportRow = sheet.createRow(1);
        reportRow.setHeightInPoints(24);
        Cell reportCell = reportRow.createCell(0);
        reportCell.setCellValue("CHOWKI MANAGEMENT REPORT");
        reportCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { reportRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        // ── Row 2: Generated on ───────────────────────────────────────────────
        Row genRow = sheet.createRow(2);
        genRow.setHeightInPoints(20);
        Cell genCell = genRow.createCell(0);
        genCell.setCellValue("Generated On: " + new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new java.util.Date()));
        genCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { genRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLS - 1));

        // ── Row 3: Column headers ─────────────────────────────────────────────
        Row headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints(36);
        for (int i = 0; i < headerLabels.length; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(headerLabels[i]);
            hCell.setCellStyle(hdrStyle);
        }

        // ── Rows 4+: Data (styles reused per row, not recreated) ─────────────
        java.text.SimpleDateFormat dateFmt = new java.text.SimpleDateFormat("dd-MMM-yyyy");
        int dataStartsFrom = 4;
        int serialNo = 1;
        for (ChowkiManagementResponse c : chowkiResponseList) {
            Row contentRow = sheet.createRow(dataStartsFrom);
            XSSFCellStyle rowStyle = ((dataStartsFrom - 4) % 2 != 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(serialNo++),
                c.getFarmerName(),
                c.getFatherName(),
                c.getFruitsId(),
                c.getDflsSource(),
                Util.objectToString(c.getRaceId()),
                c.getRaceName(),
                Util.objectToString(c.getNumbersOfDfls()),
                c.getLotNumberRsp(),
                c.getLotNumberCrc(),
                c.getVillageName(),
                c.getDistrictName(),
                c.getStateName(),
                c.getTalukName(),
                c.getHobliName(),
                c.getTscName(),
                c.getSoldAfter1stOr2ndMould(),
                Util.objectToString(c.getRatePer100Dfls()),
                Util.objectToString(c.getPrice()),
                c.getHatchingDate() != null ? dateFmt.format(c.getHatchingDate()) : "",
                c.getDispatchDate() != null ? dateFmt.format(c.getDispatchDate()) : "",
                c.getReceiptNo()
            };
            for (int col = 0; col < values.length; col++) {
                Cell dataCell = contentRow.createCell(col);
                dataCell.setCellValue(values[col] != null ? values[col] : "");
                dataCell.setCellStyle(rowStyle);
            }
            dataStartsFrom++;
        }

        sheet.createFreezePane(0, 4);

        for (int columnIndex = 0; columnIndex < TOTAL_COLS; columnIndex++) {
            sheet.setColumnWidth(columnIndex, 20 * 256);
        }

        String userHome = System.getProperty("user.home");
        String directoryPath = Paths.get(userHome, "Downloads").toString();
        Path directory = Paths.get(directoryPath);
        Files.createDirectories(directory);
        Path filePath = directory.resolve("chowki_report" + Util.getISTLocalDate() + ".xlsx");

        try {
            try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
                workbook.write(fileOut);
            }
        } finally {
            workbook.dispose();
            workbook.close();
        }
        return new FileInputStream(filePath.toString());
    }


    public ResponseEntity<?> primaryChowkiDistributionDetails(Long districtId,
                                                  Long talukId,
                                                  Long villageId,
                                                  Long tscMasterId,
                                                  int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<ChowkiManagementResponse> chowkiResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = chowkiManagementRepository.getChowkiDistributionDetails(districtId, talukId, villageId, tscMasterId, pageable);

        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();

        chowkiDistributionResponse(chowkiResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(chowkiResponseList);
        return ResponseEntity.ok(rw);
    }

    public FileInputStream chowkiDistributionReport(Long districtId,
                                                    Long talukId,
                                                    Long villageId,
                                                    Long tscMasterId,
                                                    int pageNumber,
                                                    int pageSize) throws Exception {
        List<ChowkiManagementResponse> chowkiResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;

        Pageable pageable = null; // fetch all records
        Page<Object[]> applicablePage = chowkiManagementRepository.getChowkiDistributionDetails(districtId, talukId, villageId, tscMasterId, pageable);

        chowkiDistributionResponse(chowkiResponseList, applicablePage.getContent(), pageNumber, pageSize);

        String[] headerLabels = {
            "Sl.No", "Farmer Name", "Father Name", "Fruits Id",
            "Source Of DFLs", "Race Of DFLs", "Race Name", "Numbers Of DFLs",
            "Lot No RSP", "Lot No CRC", "Village Name", "District Name",
            "State Name", "Taluk Name", "Hobli Name", "TSC Name",
            "Sold After Mould", "Rate Per 100 DFLs", "Price",
            "Hatching Date", "Dispatch Date", "Receipt No"
        };
        final int TOTAL_COLS = headerLabels.length;

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Chowki Distribution Report");

        // ── Colors ───────────────────────────────────────────────────────────
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)26,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)12,  (byte)74,  (byte)158}, null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)30,  (byte)58,  (byte)95},  null);
        XSSFColor black       = new XSSFColor(new byte[]{(byte)0,   (byte)0,   (byte)0},   null);

        // ── Fonts ────────────────────────────────────────────────────────────
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setFontName("Calibri"); titleFont.setFontHeightInPoints((short)16);
        titleFont.setBold(true); titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setFontName("Calibri"); subFont.setFontHeightInPoints((short)11);
        subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setFontName("Calibri"); hdrFont.setFontHeightInPoints((short)11);
        hdrFont.setBold(true); hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontName("Calibri"); dataFont.setFontHeightInPoints((short)10);
        dataFont.setColor(darkText);

        // ── Styles ───────────────────────────────────────────────────────────
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(darkNavy);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN); titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN); titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setTopBorderColor(black); titleStyle.setBottomBorderColor(black);
        titleStyle.setLeftBorderColor(black); titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFont(subFont);
        subStyle.setFillForegroundColor(primaryBlue);
        subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER);
        subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setBorderTop(BorderStyle.THIN); subStyle.setBorderBottom(BorderStyle.THIN);
        subStyle.setBorderLeft(BorderStyle.THIN); subStyle.setBorderRight(BorderStyle.THIN);
        subStyle.setTopBorderColor(black); subStyle.setBottomBorderColor(black);
        subStyle.setLeftBorderColor(black); subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFont(hdrFont);
        hdrStyle.setFillForegroundColor(primaryBlue);
        hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER);
        hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN); hdrStyle.setBorderBottom(BorderStyle.THIN);
        hdrStyle.setBorderLeft(BorderStyle.THIN); hdrStyle.setBorderRight(BorderStyle.THIN);
        hdrStyle.setTopBorderColor(black); hdrStyle.setBottomBorderColor(black);
        hdrStyle.setLeftBorderColor(black); hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFont(dataFont);
        dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setVerticalAlignment(VerticalAlignment.CENTER);
        dataWhite.setWrapText(true);
        dataWhite.setBorderTop(BorderStyle.THIN); dataWhite.setBorderBottom(BorderStyle.THIN);
        dataWhite.setBorderLeft(BorderStyle.THIN); dataWhite.setBorderRight(BorderStyle.THIN);
        dataWhite.setTopBorderColor(black); dataWhite.setBottomBorderColor(black);
        dataWhite.setLeftBorderColor(black); dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.cloneStyleFrom(dataWhite);
        dataAlt.setFillForegroundColor(altRow);
        dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ── Row 0: Department title ───────────────────────────────────────────
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(36);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Department of Sericulture, Government of Karnataka");
        titleCell.setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { titleRow.createCell(c).setCellStyle(titleStyle); }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        // ── Row 1: Report name ────────────────────────────────────────────────
        Row reportRow = sheet.createRow(1);
        reportRow.setHeightInPoints(24);
        Cell reportCell = reportRow.createCell(0);
        reportCell.setCellValue("CHOWKI DISTRIBUTION REPORT");
        reportCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { reportRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        // ── Row 2: Generated on ───────────────────────────────────────────────
        Row genRow = sheet.createRow(2);
        genRow.setHeightInPoints(20);
        Cell genCell = genRow.createCell(0);
        genCell.setCellValue("Generated On: " + new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new java.util.Date()));
        genCell.setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { genRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLS - 1));

        // ── Row 3: Column headers ─────────────────────────────────────────────
        Row headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints(36);
        for (int i = 0; i < TOTAL_COLS; i++) {
            Cell hCell = headerRow.createCell(i);
            hCell.setCellValue(headerLabels[i]);
            hCell.setCellStyle(hdrStyle);
        }

        // ── Rows 4+: Data rows ────────────────────────────────────────────────
        int dataStartsFrom = 4;
        for (int i = 0; i < chowkiResponseList.size(); i++) {
            ChowkiManagementResponse c = chowkiResponseList.get(i);
            Row row = sheet.createRow(dataStartsFrom + i);
            XSSFCellStyle rowStyle = (i % 2 != 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(c.getSerialNumber()),
                c.getFarmerName(),
                c.getFatherName(),
                c.getFruitsId(),
                c.getDflsSource(),
                c.getRaceId() != null ? String.valueOf(c.getRaceId()) : "",
                c.getRaceName(),
                c.getNumbersOfDfls() != null ? String.valueOf(c.getNumbersOfDfls()) : "",
                c.getLotNumberRsp(),
                c.getLotNumberCrc(),
                c.getVillageName(),
                c.getDistrictName(),
                c.getStateName(),
                c.getTalukName(),
                c.getHobliName(),
                c.getTscName(),
                c.getSoldAfter1stOr2ndMould(),
                c.getRatePer100Dfls() != null ? String.valueOf(c.getRatePer100Dfls()) : "",
                c.getPrice() != null ? String.valueOf(c.getPrice()) : "",
                c.getHatchingDate() != null ? new java.text.SimpleDateFormat("dd-MM-yyyy").format(c.getHatchingDate()) : "",
                c.getDispatchDate() != null ? new java.text.SimpleDateFormat("dd-MM-yyyy").format(c.getDispatchDate()) : "",
                c.getReceiptNo()
            };
            for (int col = 0; col < TOTAL_COLS; col++) {
                Cell cell = row.createCell(col);
                cell.setCellValue(values[col] != null ? values[col] : "");
                cell.setCellStyle(rowStyle);
            }
        }

        sheet.createFreezePane(0, 4);
        for (int col = 0; col < TOTAL_COLS; col++) {
            sheet.setColumnWidth(col, 20 * 256);
        }

        String userHome = System.getProperty("user.home");
        String directoryPath = Paths.get(userHome, "Downloads").toString();
        Files.createDirectories(Paths.get(directoryPath));
        Path filePath = Paths.get(directoryPath, "chowki_distribution_report" + Util.getISTLocalDate() + ".xlsx");

        FileOutputStream fileOut = new FileOutputStream(filePath.toString());
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        FileInputStream fileIn = new FileInputStream(filePath.toString());
        return fileIn;
    }


    private static void chowkiDistributionResponse(List<ChowkiManagementResponse> chowkiResponseList,
                                       List<Object[]> applicableList,
                                       int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (Object[] arr : applicableList) {
            ChowkiManagementResponse response = ChowkiManagementResponse.builder()
                    .serialNumber(serialNumber++)
                    .chowkiId(Util.objectToInteger(arr[0]))
                    .farmerName(Util.objectToString(arr[1]))
                    .fatherName(Util.objectToString(arr[2]))
                    .fruitsId(Util.objectToString(arr[3]))
                    .dflsSource(Util.objectToString(arr[4]))
                    .raceId(Util.objectToLong(arr[5]))
                    .raceName(Util.objectToString(arr[6]))
                    .numbersOfDfls(Util.objectToLong(arr[7]))
                    .lotNumberRsp(Util.objectToString(arr[8]))
                    .lotNumberCrc(Util.objectToString(arr[9]))
                    .villageName(Util.objectToString(arr[10]))
                    .districtName(Util.objectToString(arr[11]))
                    .stateName(Util.objectToString(arr[12]))
                    .talukName(Util.objectToString(arr[13]))
                    .hobliName(Util.objectToString(arr[14]))
                    .tscName(Util.objectToString(arr[15]))
                    .soldAfter1stOr2ndMould(Util.objectToString(arr[22]))
                    .ratePer100Dfls(Util.objectToFloat(arr[23]))
                    .price(Util.objectToFloat(arr[24]))
                    .hatchingDateForReport(Util.objectToString(arr[25]))
                    .dispatchDateForReport(Util.objectToString(arr[26]))
                    .receiptNo(Util.objectToString(arr[27]))
                    .build();
            chowkiResponseList.add(response);
        }
    }


}

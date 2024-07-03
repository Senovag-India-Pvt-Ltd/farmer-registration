package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.*;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetLandDetailsResponse;
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
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.*;
import com.sericulture.registration.utils.ObjectToUrlEncodedConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
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
    DistrictRepository districtRepository;

    @Autowired
    TalukRepository talukRepository;

    @Autowired
    HobliRepository hobliRepository;

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

    @Transactional
    public FarmerResponse insertCompleteFarmerDetails(FarmerSaveRequest farmerSaveRequest) {
        FarmerRequest farmerRequest = farmerSaveRequest.getFarmerRequest();
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
                if(savedResponse.getFarmerId() != null) {


                    //Save farmer bank acc details
                    farmerSaveRequest.getFarmerBankAccountRequest().setFarmerId(savedResponse.getFarmerId());
                    FarmerBankAccountResponse farmerBankAccountResponse =farmerBankAccountService.insertFarmerBankAccountDetails(farmerSaveRequest.getFarmerBankAccountRequest());
                    if(farmerBankAccountResponse.getFarmerBankAccountId()>0){
                        farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                    }

                    for(int i=0; i<farmerSaveRequest.getFarmerAddressRequests().size();i++) {
                        farmerSaveRequest.getFarmerAddressRequests().get(i).setFarmerId(savedResponse.getFarmerId());
                        farmerAddressService.insertFarmerAddressDetails(farmerSaveRequest.getFarmerAddressRequests().get(i));
                    }

                    for(int i=0; i<farmerSaveRequest.getFarmerFamilyRequestList().size();i++) {
                        farmerSaveRequest.getFarmerFamilyRequestList().get(i).setFarmerId(savedResponse.getFarmerId());
                        farmerFamilyService.insertFarmerFamilyDetails(farmerSaveRequest.getFarmerFamilyRequestList().get(i));
                    }

                    for(int i=0; i<farmerSaveRequest.getFarmerLandDetailsRequests().size();i++) {
                        farmerSaveRequest.getFarmerLandDetailsRequests().get(i).setFarmerId(savedResponse.getFarmerId());
                        farmerLandDetailsService.insertFarmerLandDetailsDetails(farmerSaveRequest.getFarmerLandDetailsRequests().get(i));
                    }


                    /*InspectionTask inspectionTask = new InspectionTask();
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
                    }*/

                }else{
                    farmerResponse.setError(true);
                }
            }
        }
        return farmerResponse;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    @Transactional
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

    @Transactional
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
    public FarmerResponse editCompleteFarmerDetails(EditCompleteFarmerRequest editCompleteFarmerRequest) {
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

            if(farmerResponse.getFarmerId()>0){
                //Save farmer bank acc details
                editCompleteFarmerRequest.getEditFarmerBankAccountRequest().setFarmerId(farmerResponse.getFarmerId());
                FarmerBankAccountResponse farmerBankAccountResponse =farmerBankAccountService.updateFarmerBankAccountDetails(editCompleteFarmerRequest.getEditFarmerBankAccountRequest());
                if(farmerBankAccountResponse.getFarmerBankAccountId()>0){
                    farmerResponse.setFarmerBankAccountId(Long.valueOf(farmerBankAccountResponse.getFarmerBankAccountId()));
                }
                if(editCompleteFarmerRequest.getEditFarmerFamilyRequests() != null){
                    for(int i=0; i<editCompleteFarmerRequest.getEditFarmerFamilyRequests().size();i++) {
                        editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                        farmerFamilyService.updateFarmerFamilyDetails(editCompleteFarmerRequest.getEditFarmerFamilyRequests().get(i));
                    }
                }

                for(int i=0; i<editCompleteFarmerRequest.getEditFarmerAddressRequests().size();i++) {
                    editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                    farmerAddressService.updateFarmerAddressDetails(editCompleteFarmerRequest.getEditFarmerAddressRequests().get(i));
                }

                for(int i=0; i<editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().size();i++) {
                    editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i).setFarmerId(farmerResponse.getFarmerId());
                    farmerLandDetailsService.updateFarmerLandDetailsDetails(editCompleteFarmerRequest.getEditFarmerLandDetailsRequests().get(i));
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

    @Transactional
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
        }else{
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

    @Transactional
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

    @Transactional
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

                    log.info("District code: "+farmerLandDetails.getDistrictCode());
                    District district = districtRepository.findByDistrictCode(String.valueOf(getLandDetailsResponse.getDistrictCode()));
                    if(district != null) {
                        log.info("District name: "+district.getDistrictName() +":districtId:"+district.getDistrictId()+":lgDist:"+district.getDistrictCode());
                        log.info("Taluk code: "+getLandDetailsResponse.getTalukCode());
                        Taluk taluk = talukRepository.findByDistrictIdAndTalukCode(district.getDistrictId(), String.valueOf(getLandDetailsResponse.getTalukCode()));
                        if(taluk != null) {
                            log.info("Taluk name: "+taluk.getTalukName()+":talukId:"+taluk.getTalukId()+":districtId"+taluk.getDistrictId()+"lgTaluk:"+taluk.getLgTaluk());
                            log.info("Hobli code: "+getLandDetailsResponse.getHobliCode());
                            Hobli hobli = hobliRepository.findByTalukIdAndHobliCode(taluk.getTalukId(), String.valueOf(getLandDetailsResponse.getHobliCode()));
                            if(hobli != null) {
                                log.info("Hobli name: "+hobli.getHobliName()+":hobliId:"+hobli.getHobliId()+":districtId"+hobli.getDistrictId()+":talukId:"+hobli.getTalukId());
                                log.info("Village code: "+getLandDetailsResponse.getVillageCode());
                                Village village = villageRepository.findByHobliIdAndVillageCode(hobli.getHobliId(), String.valueOf(getLandDetailsResponse.getVillageCode()));
                                if (village == null) {
                                    log.info("Village name: "+village.getVillageName()+":hobliId:"+village.getHobliId()+":districtId"+village.getDistrictId()+":talukId:"+village.getTalukId()+":villageId:"+village.getVillageId()+":lgVillage:"+village.getLgVillage());
                                    farmerLandDetails.setVillageId(null);
                                    farmerLandDetails.setHobliId(null);
                                    farmerLandDetails.setTalukId(null);
                                    farmerLandDetails.setDistrictId(null);
                                    farmerLandDetails.setStateId(null);

                                    getFarmerResponse.setError(true);
                                    getFarmerResponse.setError_description("Village not found, please create village and then continue");
                                } else {
                                    log.info("Village name: "+village.getVillageName()+":hobliId:"+village.getHobliId()+":districtId"+village.getDistrictId()+":talukId:"+village.getTalukId()+":villageId:"+village.getVillageId()+":lgVillage:"+village.getLgVillage());
                                    VillageDTO villageDTO1 = villageRepository.getByVillageIdAndActive(village.getVillageId(), true);
                                    log.info("VillageDTO1: - Village name: "+villageDTO1.getVillageName()+":hobliId:"+villageDTO1.getHobliId()+":districtId"+villageDTO1.getDistrictId()+":talukId:"+villageDTO1.getTalukId()+":villageId:"+villageDTO1.getVillageId());

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
                            }else{
                                log.info("Hobli not found ");
                                farmerLandDetails.setVillageId(null);
                                farmerLandDetails.setHobliId(null);
                                farmerLandDetails.setTalukId(null);
                                farmerLandDetails.setDistrictId(null);
                                farmerLandDetails.setStateId(null);

                                getFarmerResponse.setError(true);
                                getFarmerResponse.setError_description("Hobli not found, please create hobli and then continue");
                            }
                        }else{
                            log.info("Taluk not found: "+farmerLandDetails.getTalukCode());
                            farmerLandDetails.setVillageId(null);
                            farmerLandDetails.setHobliId(null);
                            farmerLandDetails.setTalukId(null);
                            farmerLandDetails.setDistrictId(null);
                            farmerLandDetails.setStateId(null);

                            getFarmerResponse.setError(true);
                            getFarmerResponse.setError_description("Taluk not found, please create taluk and then continue");
                        }
                    }else{
                        log.info("District name not found: "+district.getDistrictName());
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
            }else{
                serialCounter.setFarmerWithoutFruitsAllowedNumber(0L);
            }
            if(farmer.getWithoutFruitsInwardCounter() == null){
                farmer.setWithoutFruitsInwardCounter(0L);
            }
            if(serialCounter.getFarmerWithoutFruitsAllowedNumber() == null){
                serialCounter.setFarmerWithoutFruitsAllowedNumber(0L);
            }
            if(farmer.getWithoutFruitsInwardCounter()> serialCounter.getFarmerWithoutFruitsAllowedNumber() && (farmer.getFruitsId().equals("") || farmer.getFruitsId() == null)){
                getFarmerResponse.setError(true);
                getFarmerResponse.setError_description("Maximum allowance of allotment for farmer is reached. Please come back with fruits id.");
            }else {
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


    @Transactional
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

                    District district = districtRepository.findByDistrictCode(String.valueOf(farmerLandDetails.getDistrictCode()));
                    if(district != null) {

                        Taluk taluk = talukRepository.findByDistrictIdAndTalukCode(district.getDistrictId(), String.valueOf(farmerLandDetails.getTalukCode()));
                        if(taluk != null) {
                            Hobli hobli = hobliRepository.findByTalukIdAndHobliCode(taluk.getTalukId(), String.valueOf(farmerLandDetails.getHobliCode()));
                            if(hobli != null) {

                                Village village = villageRepository.findByHobliIdAndVillageCode(hobli.getHobliId(), String.valueOf(farmerLandDetails.getVillageCode()));
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
                                        farmerLandDetails.setHobliId(villageDTO1.getHobliId());
                                    }
                                    farmerLandDetails.setHobliName(villageDTO1.getHobliName());
                                    farmerLandDetails.setVillageName(villageDTO1.getVillageName());
                                }
                            }else{
                                farmerLandDetails.setVillageId(null);
                                farmerLandDetails.setHobliId(null);
                                farmerLandDetails.setTalukId(null);
                                farmerLandDetails.setDistrictId(null);
                                farmerLandDetails.setStateId(null);

                                getFarmerResponse.setError(true);
                                getFarmerResponse.setError_description("Hobli not found, please create hobli and then continue");
                            }
                        }else{
                            farmerLandDetails.setVillageId(null);
                            farmerLandDetails.setHobliId(null);
                            farmerLandDetails.setTalukId(null);
                            farmerLandDetails.setDistrictId(null);
                            farmerLandDetails.setStateId(null);

                            getFarmerResponse.setError(true);
                            getFarmerResponse.setError_description("Taluk not found, please create taluk and then continue");
                        }
                    }else{
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

        return getFarmerResponse;
    }
    @Transactional
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getPaginatedFarmerDetailsWithJoin(final Pageable pageable) {
        return convertDTOToMapResponse(farmerRepository.getByActiveOrderByFarmerIdAsc(true, pageable));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> getPaginatedFarmerDetailsWithJoinWithFilters(final Pageable pageable, int type, String searchText, int joinColumnType) {
        Page<FarmerDTO> page;
        if (searchText == null || searchText.equals("")) {
            searchText = "%%";
        } else {
            searchText = "%" + searchText + "%";
        }
        String joinColumn = "";

        if(joinColumnType == 0){
            joinColumn = "farmer.farmerNumber";
        }else if(joinColumnType == 1){
            joinColumn = "farmer.fruitsId";
        }else{
            joinColumn = "farmer.mobileNumber";
        }

        if(type == 0){
            page = farmerRepository.getByActiveOrderByFarmerIdAsc(true,joinColumn, searchText,pageable);
        }else if(type == 1) {
            page = farmerRepository.getByActiveOrderByFarmerIdAscForNonKAFarmers(true,joinColumn, searchText, pageable);
        }else if(type == 2){
            page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithFruitsId(true, joinColumn, searchText, pageable);
        }else{
            page = farmerRepository.getByActiveOrderByFarmerIdAscForKAFarmersWithoutFruitsId(true,joinColumn, searchText, pageable);
        }
        return convertDTOToMapResponse(page);
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    @Transactional
    public FarmerResponse insertNonKarnatakaFarmers(NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        Farmer farmer2 = new Farmer();
        Long farmerId;
        FarmerRequest farmer1 = new FarmerRequest();
        farmerRequest.setIsOtherStateFarmer(true);
        FarmerResponse farmerResponse = new FarmerResponse();
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

        LocalDate today = Util.getISTLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String formattedDate = today.format(formatter);
        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
        SerialCounter serialCounter = new SerialCounter();
        if (serialCounters.size() > 0) {
            serialCounter = serialCounters.get(0);
            long counterValue = 1L;
            if (serialCounter.getOtherStateFarmerCounter() != null) {
                counterValue = serialCounter.getOtherStateFarmerCounter() + 1;
            }
            serialCounter.setOtherStateFarmerCounter(counterValue);
        } else {
            serialCounter.setOtherStateFarmerCounter(1L);
        }
        serialCounterRepository.save(serialCounter);
        String formattedNumber = String.format("%05d", serialCounter.getOtherStateFarmerCounter());

        farmer1.setFarmerNumber(formattedNumber);

//        UUID uuid = UUID.randomUUID();
//        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
//        String fileName = "farmer/" + uuid + "_" + extension;
//        s3Controller.uploadFile(multipartFile, fileName);
//        farmer1.setPhotoPath(fileName);

        Farmer farmer = mapper.farmerObjectToEntity(farmer1, Farmer.class);
        farmer.setWithoutFruitsInwardCounter(0L);
        validator.validate(farmer);


        // Check for duplicate Reeler Number
        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Mobile Number already exists");
        } else {
            // If no duplicates found, save the reeler
            farmer2 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer2, FarmerResponse.class);
            farmerResponse.setError(false);
        }

        if(!farmerResponse.getError()) {
            farmerId = farmer2.getFarmerId();

            for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
                farmerAddress.setFarmerId(farmerId);
                farmerAddressRepository.save(farmerAddress);
            }

            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
            if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("FarmerBankAccount number already exist");
            } else {
                farmerRequest.getFarmerBankAccount().setFarmerId(farmerId);
                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
                farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());
            }
        }

        return farmerResponse;
    }

    @Transactional
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



    @Transactional
    public FarmerResponse insertKarnatakaFarmersWithoutFruitsId(NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        Farmer farmer2 = new Farmer();
        Long farmerId;
        FarmerRequest farmer1 = new FarmerRequest();
        farmerRequest.setIsOtherStateFarmer(false);
        FarmerResponse farmerResponse = new FarmerResponse();
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

        farmer1.setFarmerNumber("KSWFID"+formattedNumber);

//        UUID uuid = UUID.randomUUID();
//        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
//        String fileName = "farmer/" + uuid + "_" + extension;
//        s3Controller.uploadFile(multipartFile, fileName);
//        farmer1.setPhotoPath(fileName);

        Farmer farmer = mapper.farmerObjectToEntity(farmer1, Farmer.class);
        farmer.setWithoutFruitsInwardCounter(0L);
        validator.validate(farmer);


        // Check for duplicate Reeler Number
        List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
        if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer Mobile Number already exists");
        } else {
            // If no duplicates found, save the reeler
            farmer2 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer2, FarmerResponse.class);
            farmerResponse.setError(false);
        }

        if(!farmerResponse.getError()) {
            farmerId = farmer2.getFarmerId();

            for (FarmerAddress farmerAddress : farmerRequest.getFarmerAddressList()) {
                farmerAddress.setFarmerId(farmerId);
                farmerAddressRepository.save(farmerAddress);
            }

            List<FarmerBankAccount> farmerBankAccountList = farmerBankAccountRepository.findByFarmerBankAccountNumber(farmerRequest.getFarmerBankAccount().getFarmerBankAccountNumber());
            if (!farmerBankAccountList.isEmpty() && farmerBankAccountList.stream().filter(FarmerBankAccount::getActive).findAny().isPresent()) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("FarmerBankAccount number already exist");
            } else {
                farmerRequest.getFarmerBankAccount().setFarmerId(farmerId);
                FarmerBankAccount farmerBankAccount1 = farmerBankAccountRepository.save(farmerRequest.getFarmerBankAccount());
                farmerResponse.setFarmerBankAccountId(farmerBankAccount1.getFarmerBankAccountId());
            }
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
        }else{
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
        if(farmer == null){
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occured while fetching farmer details");
        }else{
            if(farmer.getWithoutFruitsInwardCounter() == null){
                farmer.setWithoutFruitsInwardCounter(1L);
            }else{
                farmer.setWithoutFruitsInwardCounter(farmer.getWithoutFruitsInwardCounter() + 1L);
            }
            farmerRepository.save(farmer);
            farmerResponse.setError(false);
        }
        return farmerResponse;
    }

    public ResponseEntity<?> totalFarmerCount( ) {

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


    public ResponseEntity<?> districtWiseFarmerCount( ) {

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
                                                  int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryDetailsResponse> primaryDetailsResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;
        Page<Object[]> applicablePage;
        // applicableList = applicationFormRepository.getSubmittedListForDbt(statusList, financialYearId, schemeId, subSchemeId, applicationId, sanctionNo, fruitsId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        applicablePage  = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();


        farmerResponse(primaryDetailsResponseList, applicableList);
        rw.setTotalRecords(totalRecords);
        rw.setContent(primaryDetailsResponseList);
        return ResponseEntity.ok(rw);
    }

    private static void farmerResponse(List<PrimaryDetailsResponse> primaryDetailsResponseList, List<Object[]> applicableList) {
        int serialNumber = 1;
        for (Object[] arr : applicableList) {
            PrimaryDetailsResponse primaryDetailsResponse;
            primaryDetailsResponse = PrimaryDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .farmerId(Util.objectToString(arr[0]))
                    .firstName(Util.objectToString(arr[1]))
                    .middleName(Util.objectToString(arr[2]))
                    .lastName(Util.objectToString(arr[3]))
                    .fruitsId(Util.objectToString(arr[4]))
                    .farmerNumber(Util.objectToString(arr[5]))
                    .fatherName(Util.objectToString(arr[6]))
                    .passbookNumber(Util.objectToString(arr[7]))
                    .epicNumber(Util.objectToString(arr[8]))
                    .rationCardNumber(Util.objectToString(arr[9]))
                    .dob(Util.objectToString(arr[10]))
                    .districtName(Util.objectToString(arr[11]))
                    .talukName(Util.objectToString(arr[12]))
                    .hobliName(Util.objectToString(arr[13]))
                    .villageName(Util.objectToString(arr[14]))
                    .farmerBankName(Util.objectToString(arr[15]))
                    .farmerBankAccountNumber(Util.objectToString(arr[16]))
                    .farmerBankBranchName(Util.objectToString(arr[17]))
                    .farmerBankIfscCode(Util.objectToString(arr[18]))
                    .build();
            primaryDetailsResponseList.add(primaryDetailsResponse);
        }
    }

    public FileInputStream farmerReport(Long districtId,
                                        Long talukId,
                                        Long villageId,
                                        Long tscMasterId) throws Exception {
        List<PrimaryDetailsResponse> primaryDetailsResponseList = new ArrayList<>();


        Page<Object[]> applicablePage;
        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        tscMasterId = (tscMasterId == 0) ? null : tscMasterId;
        Pageable pageable = null;
        applicablePage  = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        farmerResponse(primaryDetailsResponseList, applicableList);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("First Name");
        headerRow.createCell(1).setCellValue("Middle Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("Fruits Id");
        headerRow.createCell(4).setCellValue("Farmer Number");
        headerRow.createCell(5).setCellValue("Father Name");
        headerRow.createCell(6).setCellValue("Passbook Number");
        headerRow.createCell(7).setCellValue("Epic Number");
        headerRow.createCell(8).setCellValue("Ration Card Number");
        headerRow.createCell(9).setCellValue("DOB");
        headerRow.createCell(10).setCellValue("District Name");
        headerRow.createCell(11).setCellValue("Taluk Name");
        headerRow.createCell(12).setCellValue("Hobli Name");
        headerRow.createCell(13).setCellValue("Village Name");
        headerRow.createCell(14).setCellValue("Bank Name");
        headerRow.createCell(15).setCellValue("Bank Account Number");
        headerRow.createCell(16).setCellValue("Branch Name");
        headerRow.createCell(17).setCellValue("IFSC Code");

        //Dynamic data binds here
        //Starting 0th and 1st column cells are hardcoded, So dynamic data column starts from 2nd column
        int dataStartsFrom = 1;
        for(int i=0; i<primaryDetailsResponseList.size(); i++){
            Row contentRow = sheet.createRow(dataStartsFrom);
            PrimaryDetailsResponse primaryDetailsResponse = primaryDetailsResponseList.get(i);
            contentRow.createCell(0).setCellValue(primaryDetailsResponse.getFirstName());
            contentRow.createCell(1).setCellValue(primaryDetailsResponse.getMiddleName());
            contentRow.createCell(2).setCellValue(primaryDetailsResponse.getLastName());
            contentRow.createCell(3).setCellValue(primaryDetailsResponse.getFruitsId());
            contentRow.createCell(4).setCellValue(primaryDetailsResponse.getFruitsId());
            contentRow.createCell(5).setCellValue(primaryDetailsResponse.getFatherName());
            contentRow.createCell(6).setCellValue(primaryDetailsResponse.getPassbookNumber());
            contentRow.createCell(7).setCellValue(primaryDetailsResponse.getEpicNumber());
            contentRow.createCell(8).setCellValue(primaryDetailsResponse.getRationCardNumber());
            contentRow.createCell(9).setCellValue(primaryDetailsResponse.getDob());
            contentRow.createCell(10).setCellValue(primaryDetailsResponse.getDistrictName());
            contentRow.createCell(11).setCellValue(primaryDetailsResponse.getTalukName());
            contentRow.createCell(12).setCellValue(primaryDetailsResponse.getHobliName());
            contentRow.createCell(13).setCellValue(primaryDetailsResponse.getVillageName());
            contentRow.createCell(14).setCellValue(primaryDetailsResponse.getFarmerBankName());
            contentRow.createCell(15).setCellValue(primaryDetailsResponse.getFarmerBankAccountNumber());
            contentRow.createCell(16).setCellValue(primaryDetailsResponse.getFarmerBankBranchName());
            contentRow.createCell(17).setCellValue(primaryDetailsResponse.getFarmerBankIfscCode());
            dataStartsFrom = dataStartsFrom + 1;
        }

        // Auto-size all columns
        for (int columnIndex = 0; columnIndex <= 17; columnIndex++) {
            sheet.autoSizeColumn(columnIndex, true);
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
        Path filePath = directory.resolve("farmers"+Util.getISTLocalDate()+".xlsx");

        // Write the workbook content to the specified file path
        FileOutputStream fileOut = new FileOutputStream(filePath.toString());
        FileInputStream fileIn = new FileInputStream(filePath.toString());
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return fileIn;
    }
}

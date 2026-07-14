package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.BankTransaction.BankTransactionController;
import com.sericulture.registration.BankTransaction.GenericBankTransactionRequest;
import com.sericulture.registration.BankTransaction.GenericCorporateAlertRequest;
import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.helper.FarmerRegistrationHelper;
import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.*;
import com.sericulture.registration.model.api.common.SearchByColumnRequest;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetLandDetailsResponse;
import com.sericulture.registration.model.api.reeler.*;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerSearchDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.dto.village.VillageDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.*;
import io.jsonwebtoken.Jwt;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    UserMasterRepository userMasterRepository;

    @Autowired
    ReelerLicenseTransactionRepository reelerLicenseTransactionRepository;

    @Autowired
    FarmerRegistrationHelper farmerRegistrationHelper;

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

    @Autowired
    BankTransactionController bankTransactionController;

    @Autowired
    InspectionTaskRepository inspectionTaskRepository;

    @Autowired
    RequestInspectionMappingRepository requestInspectionMappingRepository;

    @Transactional
    public ReelerResponse insertReelerDetails(ReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = mapper.reelerObjectToEntity(reelerRequest, Reeler.class);
        validator.validate(reeler);
        List<Reeler> reelerListByLicenseNumber = reelerRepository.findByReelingLicenseNumberAndActive(reeler.getReelingLicenseNumber(), true);
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
            List<Reeler> reelerListByNumber = reelerRepository.findByReelerNumberAndActive(reeler.getReelerNumber(), true);
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
                Reeler savedResponse = reelerRepository.save(reeler);
                reelerResponse = mapper.reelerEntityToObject(savedResponse, ReelerResponse.class);
                reelerResponse.setError(false);

                //Once reeler created, trigger inspection if reeler created
//                if(savedResponse.getReelerId() != null) {
//                    InspectionTask inspectionTask = new InspectionTask();
//                    inspectionTask.setInspectionDate(LocalDate.now());
//                    inspectionTask.setStatus(1); //Open (Newly created)
//                    inspectionTask.setUserMasterId(reelerRequest.getInspectorId());
//                    inspectionTask.setRequestType("REELER_REGISTRATION");
//                    inspectionTask.setRequestTypeId(savedResponse.getReelerId());
//
//                    //To fetch inspection type
//                    RequestInspectionMapping requestInspectionMapping = requestInspectionMappingRepository.findByRequestTypeNameAndActive("REELER_REGISTRATION", true);
//
//                    if(requestInspectionMapping != null){
//                        inspectionTask.setInspectionType(requestInspectionMapping.getInspectionType());
//                        inspectionTaskRepository.save(inspectionTask);
//                        reelerResponse.setError(false);
//                    }else{
//                        reelerResponse.setError(true);
//                        reelerResponse.setError_description("Reeler saved, but inspection not saved");
//                    }
//
//                }else{
//                    reelerResponse.setError(true);
//                    reelerResponse.setError_description("Reeler not saved");
//                }
            }
        }
        return reelerResponse;
    }

    @Transactional
    public ReelerResponse dummyReelerDetails(ReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            reelerRequest.setReelerName("dummy_reeler" + uuid);
            reelerRequest.setPassbookNumber(String.valueOf(uuid));
            reelerRequest.setReelerNumber(String.valueOf(uuid));
            reelerRequest.setLicenseReceiptNumber(String.valueOf(uuid));
            reelerRequest.setReelingLicenseNumber(String.valueOf(uuid));
            reelerRequest.setMobileNumber(String.valueOf(uuid));
            reelerRequest.setBankAccountNumber("dummy_acc" + uuid);
            Reeler reeler = mapper.reelerObjectToEntity(reelerRequest, Reeler.class);
            validator.validate(reeler);
            List<Reeler> reelerListByLicenseNumber = reelerRepository.findByReelingLicenseNumberAndActive(reeler.getReelingLicenseNumber(), true);
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
                List<Reeler> reelerListByNumber = reelerRepository.findByReelerNumberAndActive(reeler.getReelerNumber(), true);
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
    public ReelerResponse insertTransferReelerDetails(ReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = mapper.reelerObjectToEntity(reelerRequest, Reeler.class);
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

        reeler.setArnNumber("TRL/" + formattedDate + "/" + formattedNumber);

        Reeler savedResponse = reelerRepository.save(reeler);
        reelerResponse = mapper.reelerEntityToObject(savedResponse, ReelerResponse.class);
        //Once transfer of license done, trigger inspection if created
        if (savedResponse.getReelerId() != null) {
            InspectionTask inspectionTask = new InspectionTask();
            inspectionTask.setInspectionDate(LocalDate.now());
            inspectionTask.setStatus(1); //Open (Newly created)
            inspectionTask.setUserMasterId(reelerRequest.getInspectorId());
            inspectionTask.setRequestType("REELER_LICENSE_RENEWAL");
            inspectionTask.setRequestTypeId(savedResponse.getReelerId());

            //To fetch inspection type
            RequestInspectionMapping requestInspectionMapping = requestInspectionMappingRepository.findByRequestTypeNameAndActive("REELER_LICENSE_RENEWAL", true);

            if (requestInspectionMapping != null) {
                inspectionTask.setInspectionType(requestInspectionMapping.getInspectionType());
                inspectionTaskRepository.save(inspectionTask);
                reelerResponse.setError(false);
            } else {
                reelerResponse.setError(true);
                reelerResponse.setError_description("Reeler license transferred, but inspection not saved");
            }

        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Reeler license not transferred");
        }
        return reelerResponse;
    }

    public Map<String, Object> getPaginatedReelerDetails(final Pageable pageable) {
        return convertToMapResponse(reelerRepository.findByActiveOrderByReelerIdAsc(true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Reeler> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerEntityToObject(reeler, ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler", reelerResponses);
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

    public ReelerResponse getById(int id) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(id, true);
        if (reeler == null) {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        } else {
            reelerResponse = mapper.reelerEntityToObject(reeler, ReelerResponse.class);
            reelerResponse.setError(false);
        }
        log.info("Entity is ", reeler);
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

        return reelerResponse;
    }

    @Transactional
    public ReelerResponse reelerInitialAmount(HttpHeaders httpHeader, ReelerInitialAmountRequest reelerInitialAmountRequest) {
        log.debug("Reeler initial amount request", reelerInitialAmountRequest);
        if (reelerInitialAmountRequest.getInitialAmount() == null || reelerInitialAmountRequest.getVirtualAccount() == null || reelerInitialAmountRequest.getVirtualAccount().equals("")) {
            throw new ValidationException("Please provide required information");
        }
        log.info("Reeler required data provided");
        ReelerResponse reelerResponse = new ReelerResponse();
        farmerRegistrationHelper.getAuthToken(reelerInitialAmountRequest);
        //Check if reeler exist or not
        Reeler reeler = reelerRepository.findByReelerIdAndActive(reelerInitialAmountRequest.getReelerId(), true);
        if (Objects.nonNull(reeler)) {
            try {
                GenericBankTransactionRequest genericBankTransactionRequest = new GenericBankTransactionRequest();
                List<GenericCorporateAlertRequest> genericCorporateAlertRequests = new ArrayList<>();
                GenericCorporateAlertRequest genericCorporateAlertRequest = new GenericCorporateAlertRequest();
                //Check if reeler had reeler number or reeler license number
                if (reeler.getReelingLicenseNumber().equals("") || reeler.getReelingLicenseNumber() == null || reeler.getReelerNumber().equals("") || reeler.getReelerNumber() == null) {
                    reelerResponse.setError(true);
                    reelerResponse.setError_description("Invalid reeler number or reeling license number");
                } else {
                    genericCorporateAlertRequest.setAlertSequenceNo(reeler.getReelingLicenseNumber() + "_" + reeler.getReelerNumber());

                    genericCorporateAlertRequest.setVirtualAccount(reelerInitialAmountRequest.getVirtualAccount());
                    genericCorporateAlertRequest.setAccountNumber(reelerInitialAmountRequest.getAccountNumber());
                    genericCorporateAlertRequest.setAmount(reelerInitialAmountRequest.getInitialAmount());

                    genericCorporateAlertRequest.setDebitCredit("CREDIT");
                    if (reeler.getReelerName().equals("") || reeler.getReelerName() == null) {
                        genericCorporateAlertRequest.setRemitterName("");
                    } else {
                        genericCorporateAlertRequest.setRemitterName(reeler.getReelerName());
                    }
                    if (reeler.getBankName().equals("") || reeler.getBankName() == null) {
                        genericCorporateAlertRequest.setRemitterBank("");
                    } else {
                        genericCorporateAlertRequest.setRemitterBank(reeler.getBankName());
                    }
                    if (reeler.getBankAccountNumber().equals("") || reeler.getBankAccountNumber() == null) {
                        genericCorporateAlertRequest.setRemitterAccount("");
                    } else {
                        genericCorporateAlertRequest.setRemitterAccount(reeler.getBankAccountNumber());
                    }
                    if (reeler.getIfscCode().equals("") || reeler.getIfscCode() == null) {
                        genericCorporateAlertRequest.setIfscCode("");
                    } else {
                        genericCorporateAlertRequest.setIfscCode(reeler.getIfscCode());
                    }
                    //genericCorporateAlertRequest.setChequeNo("");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedDateTime = LocalDateTime.now().format(formatter);
                    genericCorporateAlertRequest.setTransactionDate(formattedDateTime);
                    UUID uuid = UUID.randomUUID();
                    genericCorporateAlertRequest.setUserReferenceNumber(reeler.getReelerNumber() + "_" + uuid);
                    genericCorporateAlertRequest.setMnemonicCode("NEFT");
                    genericCorporateAlertRequest.setValueDate(LocalDate.now().toString());
                    genericCorporateAlertRequest.setTransactionDescription("Initial amount");

                    genericCorporateAlertRequests.add(genericCorporateAlertRequest);
                    genericBankTransactionRequest.setGenericCorporateAlertRequest(genericCorporateAlertRequests);

                    ObjectMapper mapper1 = new ObjectMapper();
                    JsonNode jsonNode = mapper1.valueToTree(genericBankTransactionRequest);
                    log.debug("Request for reeler inital amount:", jsonNode);
                    bankTransactionController.creditTransaction(httpHeader, jsonNode);

                    reelerResponse.setError(false);
                }
            } catch (Exception ex) {
                log.error(ex.toString());
                ex.printStackTrace();
            }
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse;
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
            reeler.setMahajarDetails(updateReelerLicenseRequest.getMahajarDetails());

            LocalDate today = Util.getISTLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            String formattedDate = today.format(formatter);
            List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
            SerialCounter serialCounter = new SerialCounter();
            if (serialCounters.size() > 0) {
                serialCounter = serialCounters.get(0);
                long counterValue = 1L;
                if (serialCounter.getReelerLicenseRenewalCounterNumber() != null) {
                    counterValue = serialCounter.getReelerLicenseRenewalCounterNumber() + 1;
                }
                serialCounter.setReelerLicenseRenewalCounterNumber(counterValue);
            } else {
                serialCounter.setReelerLicenseRenewalCounterNumber(1L);
            }
            serialCounterRepository.save(serialCounter);
            String formattedNumber = String.format("%05d", serialCounter.getReelerCounterNumber());

            reeler.setArnNumber("RRL/" + formattedDate + "/" + formattedNumber);


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
    public ReelerResponse updateReelerDetails(EditReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        /*List<Reeler> reelerList = reelerRepository.findByReelerName(reelerRequest.getReelerName());
        if(reelerList.size()>0){
            throw new ValidationException("Reeler already exists with this name, duplicates are not allowed.");
        }*/

        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(reelerRequest.getReelerId(), Set.of(true, false));
        if (Objects.nonNull(reeler)) {
            reeler.setReelerName(reelerRequest.getReelerName());
            reeler.setWardNumber(reelerRequest.getWardNumber());
            reeler.setPassbookNumber(reelerRequest.getPassbookNumber());
            reeler.setFatherName(reelerRequest.getFatherName());
            reeler.setEducationId(reelerRequest.getEducationId());
            reeler.setReelingUnitBoundary(reelerRequest.getReelingUnitBoundary());
            reeler.setDob(reelerRequest.getDob());
            reeler.setTscMasterId(reelerRequest.getTscMasterId());
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
            reeler.setAadhaarNumber(reelerRequest.getAadhaarNumber());


            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse;
    }

    @Transactional
    public ReelerResponse updateReelerGPSDetails(EditReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        /*List<Reeler> reelerList = reelerRepository.findByReelerName(reelerRequest.getReelerName());
        if(reelerList.size()>0){
            throw new ValidationException("Reeler already exists with this name, duplicates are not allowed.");
        }*/

        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(reelerRequest.getReelerId(), Set.of(true, false));
        if (Objects.nonNull(reeler)) {
//            reeler.setMahajarDetails(reelerRequest.getMahajarDetails());
            reeler.setGpsLat(reelerRequest.getGpsLat());
            reeler.setGpsLng(reelerRequest.getGpsLng());
            reeler.setInspectionDate(reelerRequest.getInspectionDate());
            //  reeler.setArnNumber(reelerRequest.getArnNumber());
            reeler.setChakbandiLat(reelerRequest.getChakbandiLat());
            reeler.setChakbandiLng(reelerRequest.getChakbandiLng());
            reeler.setIsReelerInspected(1);

            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse;
    }

    @Transactional
    public ReelerResponse updateReelerProfileDetails(EditReelerRequest reelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        /*List<Reeler> reelerList = reelerRepository.findByReelerName(reelerRequest.getReelerName());
        if(reelerList.size()>0){
            throw new ValidationException("Reeler already exists with this name, duplicates are not allowed.");
        }*/

        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(reelerRequest.getReelerId(), Set.of(true, false));
        if (Objects.nonNull(reeler)) {
            reeler.setTscMasterId(reelerRequest.getTscMasterId());
            reeler.setMobileNumber(reelerRequest.getMobileNumber());
            reeler.setMahajarDetails(reelerRequest.getMahajarDetails());
            reeler.setMahajarEast(reelerRequest.getMahajarEast());
            reeler.setMahajarWest(reelerRequest.getMahajarWest());
            reeler.setMahajarNorth(reelerRequest.getMahajarNorth());
            reeler.setMahajarSouth(reelerRequest.getMahajarSouth());
            reeler.setMahajarNorthEast(reelerRequest.getMahajarNorthEast());
            reeler.setMahajarNorthWest(reelerRequest.getMahajarNorthWest());
            reeler.setMahajarSouthEast(reelerRequest.getMahajarSouthEast());
            reeler.setMahajarSouthWest(reelerRequest.getMahajarSouthWest());
            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return reelerResponse;
    }

    public ReelerResponse getByReelingLicenseNumber(String reelingLicenseNumber) {
        ReelerResponse reelerResponse = new ReelerResponse();
        ReelerDTO reeler = reelerRepository.getByReelerLicenseNumberAndActive(reelingLicenseNumber, true);
        if (reeler == null) {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        } else {
            reelerResponse = mapper.reelerDTOToObject(reeler, ReelerResponse.class);
            reelerResponse.setError(false);
        }
        log.info("Entity is ", reeler);
        return reelerResponse;
    }

    public GetReelerResponse getReelerDetails(GetReelerRequest getReelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(getReelerRequest.getReelerId(), true);
        if (reeler == null) {
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

    public GetReelerResponse getReelerDetailsByFruitsId(GetFruitsIdRequest getFruitsIdRequest) {
        Reeler reeler = reelerRepository.findByFruitsIdAndActive(getFruitsIdRequest.getFruitsId(), true);
        if (reeler == null) {
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

    public ReelerResponse getByReelerIdJoin(int reelerId) {
        ReelerResponse reelerResponse = new ReelerResponse();
        ReelerDTO reelerDTO = reelerRepository.getByReelerIdAndActive(reelerId, true);
        if (reelerDTO == null) {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Invalid id");
        } else {
            reelerResponse = mapper.reelerDTOToObject(reelerDTO, ReelerResponse.class);
            reelerResponse.setError(false);
        }
        return reelerResponse;
    }

    public Map<String, Object> getPaginatedReelerDetailsWithJoin(final Pageable pageable) {
        return convertDTOToMapResponse(reelerRepository.getByActiveOrderByReelerIdAsc(true, pageable));
    }


    private Map<String, Object> convertDTOToMapResponse(final Page<ReelerDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerDTOToObject(reeler, ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler", reelerResponses);
        response.put("currentPage", activeReelers.getNumber());
        response.put("totalItems", activeReelers.getTotalElements());
        response.put("totalPages", activeReelers.getTotalPages());
        return response;
    }

    @Transactional
    public ReelerResponse activateReeler(ActivateReelerRequest activateReelerRequest) {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActiveIn(activateReelerRequest.getReelerId(), Set.of(true, false));
        if (Objects.nonNull(reeler)) {
            reeler.setIsActivated(activateReelerRequest.getIsActivated());
            reeler.setActive(true);
            Reeler reeler1 = reelerRepository.save(reeler);
            reelerResponse = mapper.reelerEntityToObject(reeler1, ReelerResponse.class);
            reelerResponse.setError(false);
        } else {
            reelerResponse.setError(true);
            reelerResponse.setError_description("Error occurred while fetching reeler");
        }

        return reelerResponse;
    }

    public Map<String, Object> inactiveReelers() {
        return converListToResponse(reelerRepository.findByActiveAndIsActivatedOrderByReelerNameAsc(true, 0));
    }

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
                .map(reeler -> mapper.reelerEntityToObject(reeler, ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler", reelerResponses);
        response.put("totalItems", inactiveReelers.size());
        return response;
    }

    public Map<String, Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest) {
        if (searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")) {
            searchWithSortRequest.setSearchText("%%");
        } else {
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if (searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")) {
            searchWithSortRequest.setSortColumn("reelerName");
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
        Sort sort;
        if (searchWithSortRequest.getSortOrder().equals("asc")) {
            sort = Sort.by(Sort.Direction.ASC, searchWithSortRequest.getSortColumn());
        } else {
            sort = Sort.by(Sort.Direction.DESC, searchWithSortRequest.getSortColumn());
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(searchWithSortRequest.getPageNumber()), Integer.parseInt(searchWithSortRequest.getPageSize()), sort);
        Page<ReelerDTO> reelerDTOS = reelerRepository.getSortedReelers(searchWithSortRequest.getJoinColumn(), searchWithSortRequest.getSearchText(), true, pageable);
        log.info("Entity is ", reelerDTOS);
        return convertPageableDTOToMapResponse(reelerDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<ReelerDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

        List<ReelerResponse> reelerResponses = activeReelers.getContent().stream()
                .map(reeler -> mapper.reelerDTOToObject(reeler, ReelerResponse.class)).collect(Collectors.toList());
        response.put("reeler", reelerResponses);
        response.put("currentPage", activeReelers.getNumber());
        response.put("totalItems", activeReelers.getTotalElements());
        response.put("totalPages", activeReelers.getTotalPages());

        return response;
    }

    public Map<String, Object> searchByColumn(SearchByColumnRequest searchByColumnRequest) {
        if (searchByColumnRequest.getSearchText() == null || searchByColumnRequest.getSearchText().equals("")) {
            searchByColumnRequest.setSearchText("%%");
        } else {
            searchByColumnRequest.setSearchText("%" + searchByColumnRequest.getSearchText() + "%");
        }
        if (searchByColumnRequest.getSearchColumn() == null || searchByColumnRequest.getSearchColumn().equals("")) {
            searchByColumnRequest.setSearchColumn("reeler.reelerName");
        }
        List<ReelerSearchDTO> reelerDTOList = reelerRepository.getReelerBySearchText(searchByColumnRequest.getSearchText(), searchByColumnRequest.getSearchColumn());
        log.info("Entity is ", reelerDTOList);
        return convertPageableDTOToMapResponse(reelerDTOList);
    }

    public ResponseEntity<?> getReelerDetailsByFruitsIdOrReelerNumberOrMobileNumberOrReelerLicenseNumber(SearchRequest searchRequest) throws Exception {
        log.info("Entered to function");
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<Object[]> objects = new ArrayList<>();
        List<ReelerDetailsResponse> reelerDetailsResponseList = new ArrayList<>();
        if (Objects.equals(searchRequest.getType(), "mobileNumber")) {
            if (searchRequest.getText() != null && !searchRequest.getText().equals("")) {
                if (reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType()).isEmpty()) {
                    throw new ValidationException("Invalid mobile number");
                } else {
                    objects = reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType());
                }
            }
        } else if (Objects.equals(searchRequest.getType(), "reelerNumber")) {
            if (searchRequest.getText() != null && !searchRequest.getText().equals("")) {
                if (reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType()).isEmpty()) {
                    throw new ValidationException("Invalid reeler number");
                } else {
                    objects = reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType());
                }
            }
        } else {
            if (!reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType()).isEmpty()) {
                objects = reelerRepository.getReelerDetails(searchRequest.getText(), searchRequest.getType());
            } else {
                throw new ValidationException("Invalid reeling license number");
            }
        }
        if (objects.size() > 0) {
            for (Object[] arr : objects) {
                ReelerDetailsResponse reelerDetailsResponse;
                reelerDetailsResponse = ReelerDetailsResponse.builder()
                        .reelerId(Util.objectToLong(arr[0]))
                        .name(Util.objectToString(arr[1]))
                        .passbookNumber(Util.objectToString(arr[2]))
                        .fatherName(Util.objectToString(arr[3]))
                        .dob(Util.objectToLocalDate(arr[4]))
                        .gender(Util.objectToString(arr[5]))
                        .casteTitle(Util.objectToString(arr[6]))
                        .mobileNumber(Util.objectToString(arr[7]))
                        .arnNumber(Util.objectToString(arr[8]))
                        .stateName(Util.objectToString(arr[9]))
                        .districtName(Util.objectToString(arr[10]))
                        .talukName(Util.objectToString(arr[11]))
                        .hobliName(Util.objectToString(arr[12]))
                        .villageName(Util.objectToString(arr[13]))
                        .address(Util.objectToString(arr[14]))
                        .pincode(Util.objectToString(arr[15]))
                        .virtualAccountNumber(Util.objectToString(arr[16]))
                        .reelerLicenseNumber(Util.objectToString(arr[17]))
                        .reelerNumber(Util.objectToString(arr[18]))
                        .build();
                reelerDetailsResponseList.add(reelerDetailsResponse);
            }
        }
        rw.setContent(reelerDetailsResponseList);
        log.info("Response is {}", rw.getContent());
        return ResponseEntity.ok(rw);
    }

    public ResponseEntity<?> getReelerDetailsByFruitsIdAndReelingLicenseNumber(SearchRequest searchRequest) throws Exception {
        log.info("Entered to function");
        // Initialize response wrapper and the response list
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<Object[]> objects = new ArrayList<>();
        List<ReelerDetailsResponse> reelerDetailsResponseList = new ArrayList<>();

        // Validate the search text
        if (searchRequest.getText() == null || searchRequest.getText().isEmpty()) {
            throw new ValidationException("Search text cannot be empty");
        }

        // Fetch the data using the repository method
        objects = reelerRepository.getReelerDetailsByFruitsIdAndReelingLicenseNumber(searchRequest.getText());

        // Check if any results were returned
        if (objects.isEmpty()) {
            throw new ValidationException("No reeler details found for the given ID or reeling license number");
        }

        // Map the result to the response object
        for (Object[] arr : objects) {
            ReelerDetailsResponse reelerDetailsResponse = ReelerDetailsResponse.builder()
                    .reelerId(Util.objectToLong(arr[0]))
                    .name(Util.objectToString(arr[1]))
                    .passbookNumber(Util.objectToString(arr[2]))
                    .fatherName(Util.objectToString(arr[3]))
                    .dob(Util.objectToLocalDate(arr[4]))
                    .gender(Util.objectToString(arr[5]))
                    .mobileNumber(Util.objectToString(arr[6]))
                    .arnNumber(Util.objectToString(arr[7]))
                    .stateName(Util.objectToString(arr[8]))
                    .districtName(Util.objectToString(arr[9]))
                    .talukName(Util.objectToString(arr[10]))
                    .hobliName(Util.objectToString(arr[11]))
                    .villageName(Util.objectToString(arr[12]))
                    .address(Util.objectToString(arr[13]))
                    .pincode(Util.objectToString(arr[14]))
                    .virtualAccountNumber(Util.objectToString(arr[15]))
                    .reelerLicenseNumber(Util.objectToString(arr[16]))
                    .reelerNumber(Util.objectToString(arr[17]))
                    .fruitsId(Util.objectToString(arr[18]))
                    .build();
            reelerDetailsResponseList.add(reelerDetailsResponse);
        }

        // Set response content and return it
        rw.setContent(reelerDetailsResponseList);
        log.info("Response is {}", rw.getContent());
        return ResponseEntity.ok(rw);
    }


    private Map<String, Object> convertPageableDTOToMapResponse(final List<ReelerSearchDTO> activeReelers) {
        Map<String, Object> response = new HashMap<>();

//        List<ReelerResponse> reelerResponses = activeReelers.stream()
//                .map(reelerDTO -> mapper.reelerDTOToObject(reelerDTO,ReelerResponse.class)).collect(Collectors.toList());
        response.put("reelers", activeReelers);
        response.put("totalItems", activeReelers.size());
        return response;
    }

    @Transactional
    public ReelerResponse updateMahajarDetailsPath(MultipartFile multipartFile, String reelerId) throws Exception {
        ReelerResponse reelerResponse = new ReelerResponse();
        Reeler reeler = reelerRepository.findByReelerIdAndActive(Long.parseLong(reelerId), true);
        if (Objects.nonNull(reeler)) {
            UUID uuid = UUID.randomUUID();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            String fileName = "reeler/" + reeler.getReelerId() + "_" + reelerId + "_" + uuid + "_" + extension;
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

    public ResponseEntity<?> districtWiseReelerCount() {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<DistrictWiseReelerCountResponse> districtWiseReelerCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = reelerRepository.getDistrictWiseReelerCount();
        for (Object[] arr : applicableList) {
            DistrictWiseReelerCountResponse districtWiseReelerCountResponse;
            districtWiseReelerCountResponse = DistrictWiseReelerCountResponse.builder().
                    districtName(Util.objectToString(arr[0]))
                    .reelerCount(Util.objectToString(arr[1]))
                    .build();
            districtWiseReelerCountResponseList.add(districtWiseReelerCountResponse);
        }
        rw.setContent(districtWiseReelerCountResponseList);

        return ResponseEntity.ok(rw);

    }

    public ResponseEntity<?> talukWiseReelerCount(ApplicationsDetailsDistrictReelerWiseRequest applicationsDetailsDistrictReelerWiseRequest) {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<TalukWiseReelerCountResponse> talukWiseReelerCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = reelerRepository.getTalukWiseReelerCount(applicationsDetailsDistrictReelerWiseRequest.getDistrictId());
        for (Object[] arr : applicableList) {
            TalukWiseReelerCountResponse talukWiseReelerCountResponse;
            talukWiseReelerCountResponse = TalukWiseReelerCountResponse.builder().
                    talukName(Util.objectToString(arr[0]))
                    .reelerCount(Util.objectToString(arr[1]))


                    .build();
            talukWiseReelerCountResponseList.add(talukWiseReelerCountResponse);
        }
        rw.setContent(talukWiseReelerCountResponseList);

        return ResponseEntity.ok(rw);

    }

    public ResponseEntity<?> marketWiseReelerCount(ApplicationsDetailsDistrictReelerWiseRequest applicationsDetailsDistrictReelerWiseRequest) {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);

        List<MarketWiseReelerCountResponse> marketWiseReelerCountResponseList = new ArrayList<>();
        List<Object[]> applicableList = reelerRepository.getMarketWiseReelerCountByMarketId(applicationsDetailsDistrictReelerWiseRequest.getMarketId());
        for (Object[] arr : applicableList) {
            MarketWiseReelerCountResponse marketWiseReelerCountResponse;
            marketWiseReelerCountResponse = MarketWiseReelerCountResponse.builder().
                    marketName(Util.objectToString(arr[0]))
                    .reelerCount(Util.objectToString(arr[1]))


                    .build();
            marketWiseReelerCountResponseList.add(marketWiseReelerCountResponse);
        }
        rw.setContent(marketWiseReelerCountResponseList);

        return ResponseEntity.ok(rw);

    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static String formatDate(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Timestamp) {
            return ((Timestamp) obj).toLocalDateTime().toLocalDate().format(DATE_FORMATTER);
        } else if (obj instanceof LocalDateTime) {
            return ((LocalDateTime) obj).toLocalDate().format(DATE_FORMATTER);
        } else if (obj instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) obj).getTime()).toLocalDate().format(DATE_FORMATTER);
        }
        return obj.toString().split(" ")[0]; // fallback: split timestamp string
    }

    public ResponseEntity<?> primaryReelerForRenewalLicense(Long districtId,
                                                            Long talukId,
                                                            Long villageId,
                                                            Long marketId,
                                                            LocalDate  renewalDate,
                                                            LocalDate  expiryDate,
                                                            int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;
//        Page<Object[]> applicablePage;
//        // applicableList = applicationFormRepository.getSubmittedListForDbt(statusList, financialYearId, schemeId, subSchemeId, applicationId, sanctionNo, fruitsId);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        applicablePage  = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, pageable);
//        List<Object[]> applicableList = applicablePage.getContent();
//        long totalRecords = applicablePage.getTotalElements();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = reelerRepository.getPrimaryReelerForRenewalLicense(districtId, talukId, villageId, marketId,renewalDate, expiryDate, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();


        reelerResponses(primaryReelerDetailsResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(primaryReelerDetailsResponseList);
        return ResponseEntity.ok(rw);
    }

    private static void reelerResponses(List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList, List<Object[]> applicableList, int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (Object[] arr : applicableList) {
            PrimaryReelerDetailsResponse primaryReelerDetailsResponse;
            primaryReelerDetailsResponse = PrimaryReelerDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .reelerId(Util.objectToString(arr[0]))
                    .firstName(Util.objectToString(arr[1]))
                    .fruitsId(Util.objectToString(arr[2]))
                    .reelerLicenseNumber(Util.objectToString(arr[3]))
                    .fatherName(Util.objectToString(arr[4]))
                    .passbookNumber(Util.objectToString(arr[5]))
                    .reelerNumber(Util.objectToString(arr[6]))
                    .rationCardNumber(Util.objectToString(arr[7]))
                    .dob(Util.objectToString(arr[8]))
                    .districtName(Util.objectToString(arr[9]))
                    .talukName(Util.objectToString(arr[10]))
                    .hobliName(Util.objectToString(arr[11]))
                    .villageName(Util.objectToString(arr[12]))
                    .reelerBankName(Util.objectToString(arr[13]))
                    .reelerBankAccountNumber(Util.objectToString(arr[14]))
                    .reelerBankBranchName(Util.objectToString(arr[15]))
                    .reelerBankIfscCode(Util.objectToString(arr[16]))
                    .reelerMobileNumber(Util.objectToLong(arr[17]))
                    .renewalDate(formatDate(arr[18]))
                    .expiryDate(formatDate(arr[19]))
                    .build();
            primaryReelerDetailsResponseList.add(primaryReelerDetailsResponse);
        }
    }

    public ResponseEntity<?> primaryReelerDetails(Long districtId,
                                                  Long talukId,
                                                  Long villageId,
                                                  Long marketId,
                                                  Long casteId,
                                                  int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList = new ArrayList<>();

        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;
        casteId = (casteId == 0) ? null : casteId;

//        Page<Object[]> applicablePage;
//        // applicableList = applicationFormRepository.getSubmittedListForDbt(statusList, financialYearId, schemeId, subSchemeId, applicationId, sanctionNo, fruitsId);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        applicablePage  = farmerRepository.getPrimaryFarmerDetails(districtId, talukId, villageId, tscMasterId, pageable);
//        List<Object[]> applicableList = applicablePage.getContent();
//        long totalRecords = applicablePage.getTotalElements();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = reelerRepository.getPrimaryReelerDetails(districtId, talukId, villageId, marketId, casteId, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();


        reelerResponse(primaryReelerDetailsResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(primaryReelerDetailsResponseList);
        return ResponseEntity.ok(rw);
    }

//    private static void reelerResponse(List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList, List<Object[]> applicableList, int pageNumber, int pageSize) {
//        int serialNumber = pageNumber * pageSize + 1;
//        for (Object[] arr : applicableList) {
//            PrimaryReelerDetailsResponse primaryReelerDetailsResponse;
//            primaryReelerDetailsResponse = PrimaryReelerDetailsResponse.builder()
//                    .serialNumber(serialNumber++)
//                    .reelerId(Util.objectToString(arr[0]))
//                    .firstName(Util.objectToString(arr[1]))
//                    .fruitsId(Util.objectToString(arr[2]))
//                    .reelerLicenseNumber(Util.objectToString(arr[3]))
//                    .fatherName(Util.objectToString(arr[4]))
//                    .passbookNumber(Util.objectToString(arr[5]))
//                    .reelerNumber(Util.objectToString(arr[6]))
//                    .rationCardNumber(Util.objectToString(arr[7]))
//                    .dob(Util.objectToString(arr[8]))
//                    .districtName(Util.objectToString(arr[9]))
//                    .talukName(Util.objectToString(arr[10]))
//                    .hobliName(Util.objectToString(arr[11]))
//                    .villageName(Util.objectToString(arr[12]))
//                    .reelerBankName(Util.objectToString(arr[13]))
//                    .reelerBankAccountNumber(Util.objectToString(arr[14]))
//                    .reelerBankBranchName(Util.objectToString(arr[15]))
//                    .reelerBankIfscCode(Util.objectToString(arr[16]))
//                    .reelerMobileNumber(Util.objectToLong(arr[17]))
//                    .caste(Util.objectToString(arr[18]))
//
//                    .build();
//            primaryReelerDetailsResponseList.add(primaryReelerDetailsResponse);
//        }
//    }

    private static void reelerResponse(List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList,
                                       List<Object[]> applicableList,
                                       int pageNumber,
                                       int pageSize) {

        int serialNumber = pageNumber * pageSize + 1;

        for (Object[] arr : applicableList) {
            PrimaryReelerDetailsResponse primaryReelerDetailsResponse;

            primaryReelerDetailsResponse = PrimaryReelerDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .reelerId(Util.objectToString(arr[0]))
                    .firstName(Util.objectToString(arr[1]))
                    .fruitsId(Util.objectToString(arr[2]))
                    .reelerLicenseNumber(Util.objectToString(arr[3]))
                    .fatherName(Util.objectToString(arr[4]))
                    .passbookNumber(Util.objectToString(arr[5]))
                    .reelerNumber(Util.objectToString(arr[6]))
                    .rationCardNumber(Util.objectToString(arr[7]))
                    .dob(Util.objectToString(arr[8]))
                    .districtName(Util.objectToString(arr[9]))
                    .talukName(Util.objectToString(arr[10]))
                    .hobliName(Util.objectToString(arr[11]))
                    .villageName(Util.objectToString(arr[12]))
                    .reelerBankName(Util.objectToString(arr[13]))
                    .reelerBankAccountNumber(Util.objectToString(arr[14]))
                    .reelerBankBranchName(Util.objectToString(arr[15]))
                    .reelerBankIfscCode(Util.objectToString(arr[16]))
                    // ✅ Use safeLong() instead of Util.objectToLong()
                    .reelerMobileNumber(safeLong(arr[17]))
                    .caste(Util.objectToString(arr[18]))
                    .build();

            primaryReelerDetailsResponseList.add(primaryReelerDetailsResponse);
        }
    }

    /**
     * ✅ Safe Long parser that ignores non-numeric characters.
     * Prevents NumberFormatException for inputs like "5931011 99"
     */
    private static Long safeLong(Object obj) {
        if (obj == null) return null;

        String value = obj.toString().trim();

        // Remove all characters except digits
        value = value.replaceAll("[^0-9]", "");

        if (value.isEmpty()) return null;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Invalid number found: '" + obj + "' — returning null instead.");
            return null;
        }
    }


    public ResponseEntity<?> primaryReelerForExpiredLicense(
            Long districtId,
            Long talukId,
            Long villageId,
            Long marketId,
            LocalDate renewalDate,
            LocalDate expiryDate,
            int pageNumber, int pageSize) {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();

        // convert zeroes to null
        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = reelerRepository.getPrimaryReelerForRenewalLicenseList(
                districtId, talukId, villageId, marketId, renewalDate, expiryDate, pageable);

        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();

        reelerResponses(responseList, applicableList, pageNumber, pageSize);

        rw.setTotalRecords(totalRecords);
        rw.setContent(responseList);
        return ResponseEntity.ok(rw);
    }

    public ResponseEntity<?> getPendingLicenseDetailsOfReeler() {

        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();

        // ✅ Step 1: Get logged-in user's ID from JWT
        Long userMasterId = Util.getUserId(Util.getTokenValues());

        // ✅ Step 2: Get tscMasterId for that user
        Long tscMasterId = userMasterRepository.findTscMasterIdByUserMasterIdAndActive(userMasterId, true);

        // ✅ Step 3: Fetch all expiring reeler licenses for that TSC
        List<Object[]> applicableList = reelerRepository.getPendingLicenseDetailsOfReeler(tscMasterId);

        // ✅ Step 4: Convert to response
        reelerResponses(responseList, applicableList, 0, applicableList.size());

        rw.setTotalRecords((long) applicableList.size());
        rw.setContent(responseList);
        return ResponseEntity.ok(rw);
    }


    public FileInputStream expiredReelerReport(Long districtId,
                                               Long talukId,
                                               Long villageId,
                                               Long marketId,
                                               LocalDate renewalDate,
                                               LocalDate expiryDate,
                                               int pageNumber,
                                               int pageSize) throws Exception {

        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();

        // convert 0 → null
        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;

        // ✅ Fetch all records (no pagination)
        Pageable pageable = null;
        Page<Object[]> applicablePage =
                reelerRepository.getAllExpiredReelersForReport(districtId, talukId, villageId, marketId, renewalDate, expiryDate, pageable);

        List<Object[]> applicableList = applicablePage.getContent();

        // map results
        reelerResponses(responseList, applicableList, pageNumber, pageSize);

        // ── Styled Excel (SXSSFWorkbook) ─────────────────────────────────────
        String[] hdrLabels = { "Sl.No", "First Name", "Father Name", "Fruits Id", "Reeler License Number",
                //"Passbook Number",
                "Reeler Number","Mobile Number","Caste",
                //"Ration Card Number",
                "DOB", "District Name",
                "Taluk Name",
                //"Hobli Name",
                //"Village Name",
                //"Bank Name", "Bank Account Number",
               // "Branch Name", "IFSC Code",
                "License Renewal Date", "License Expiry Date" };
        final int TOTAL_COLS = hdrLabels.length;
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Expired Licenses");
        sheet.createFreezePane(0, 4);

        // ── Colours ──────────────────────────────────────────────────────────
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)28,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)13,  (byte)51,  (byte)90},  null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)45,  (byte)55,  (byte)72},  null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor black       = new XSSFColor(new byte[]{(byte)0,   (byte)0,   (byte)0},   null);

        // ── Fonts ────────────────────────────────────────────────────────────
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true); titleFont.setFontHeightInPoints((short)16); titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setBold(false); subFont.setFontHeightInPoints((short)11); subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setBold(true); hdrFont.setFontHeightInPoints((short)11); hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontHeightInPoints((short)10); dataFont.setColor(darkText);

        // ── Styles ───────────────────────────────────────────────────────────
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFillForegroundColor(darkNavy); titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER); titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setBorderTop(BorderStyle.THIN);    titleStyle.setTopBorderColor(black);
        titleStyle.setBorderBottom(BorderStyle.THIN); titleStyle.setBottomBorderColor(black);
        titleStyle.setBorderLeft(BorderStyle.THIN);   titleStyle.setLeftBorderColor(black);
        titleStyle.setBorderRight(BorderStyle.THIN);  titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFillForegroundColor(primaryBlue); subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER); subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setFont(subFont);
        subStyle.setBorderTop(BorderStyle.THIN);    subStyle.setTopBorderColor(black);
        subStyle.setBorderBottom(BorderStyle.THIN); subStyle.setBottomBorderColor(black);
        subStyle.setBorderLeft(BorderStyle.THIN);   subStyle.setLeftBorderColor(black);
        subStyle.setBorderRight(BorderStyle.THIN);  subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFillForegroundColor(primaryBlue); hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER); hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setFont(hdrFont); hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN);    hdrStyle.setTopBorderColor(black);
        hdrStyle.setBorderBottom(BorderStyle.THIN); hdrStyle.setBottomBorderColor(black);
        hdrStyle.setBorderLeft(BorderStyle.THIN);   hdrStyle.setLeftBorderColor(black);
        hdrStyle.setBorderRight(BorderStyle.THIN);  hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFillForegroundColor(white); dataWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataWhite.setFont(dataFont); dataWhite.setWrapText(false);
        dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setBorderTop(BorderStyle.THIN);    dataWhite.setTopBorderColor(black);
        dataWhite.setBorderBottom(BorderStyle.THIN); dataWhite.setBottomBorderColor(black);
        dataWhite.setBorderLeft(BorderStyle.THIN);   dataWhite.setLeftBorderColor(black);
        dataWhite.setBorderRight(BorderStyle.THIN);  dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.setFillForegroundColor(altRow); dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataAlt.setFont(dataFont); dataAlt.setWrapText(false);
        dataAlt.setAlignment(HorizontalAlignment.CENTER);
        dataAlt.setBorderTop(BorderStyle.THIN);    dataAlt.setTopBorderColor(black);
        dataAlt.setBorderBottom(BorderStyle.THIN); dataAlt.setBottomBorderColor(black);
        dataAlt.setBorderLeft(BorderStyle.THIN);   dataAlt.setLeftBorderColor(black);
        dataAlt.setBorderRight(BorderStyle.THIN);  dataAlt.setRightBorderColor(black);

        // ── Row 0: Title ─────────────────────────────────────────────────────
        Row titleRow = sheet.createRow(0); titleRow.setHeightInPoints(36);
        titleRow.createCell(0).setCellValue("Department of Sericulture, Government of Karnataka");
        titleRow.getCell(0).setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) titleRow.createCell(c).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        // ── Row 1: Report name ───────────────────────────────────────────────
        Row reportRow = sheet.createRow(1); reportRow.setHeightInPoints(28);
        reportRow.createCell(0).setCellValue("EXPIRED REELER LICENSE REPORT");
        reportRow.getCell(0).setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) reportRow.createCell(c).setCellStyle(subStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        // ── Row 2: Generated On ──────────────────────────────────────────────
        Row genRow = sheet.createRow(2); genRow.setHeightInPoints(22);
        genRow.createCell(0).setCellValue("Generated On: " + Util.getISTLocalDate());
        genRow.getCell(0).setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) genRow.createCell(c).setCellStyle(subStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLS - 1));

        // ── Row 3: Column headers ─────────────────────────────────────────────
        Row hdrRow = sheet.createRow(3);
        hdrRow.setHeightInPoints(30);
        for (int c = 0; c < TOTAL_COLS; c++) {
            Cell cell = hdrRow.createCell(c);
            cell.setCellValue(hdrLabels[c]);
            cell.setCellStyle(hdrStyle);
        }

        // ── Data rows ─────────────────────────────────────────────────────────
        int rowIdx = 4;
        int slNo = 1;
        for (PrimaryReelerDetailsResponse reeler : responseList) {
            Row row = sheet.createRow(rowIdx);
            XSSFCellStyle rowStyle = (rowIdx % 2 == 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(slNo++), reeler.getFirstName(), reeler.getFatherName(),
                reeler.getFruitsId(), reeler.getReelerLicenseNumber(), reeler.getReelerNumber(),
                reeler.getReelerMobileNumber() != null ? String.valueOf(reeler.getReelerMobileNumber()) : "",
                reeler.getCaste(), reeler.getDob(), reeler.getDistrictName(), reeler.getTalukName(),
                reeler.getRenewalDate(),
                reeler.getExpiryDate()
            };
            for (int c = 0; c < values.length; c++) {
                Cell dataCell = row.createCell(c);
                dataCell.setCellValue(values[c] != null ? values[c] : "");
                dataCell.setCellStyle(rowStyle);
            }
            rowIdx++;
        }

        sheet.createFreezePane(0, 4);
        for (int c = 0; c < TOTAL_COLS; c++) {
            sheet.setColumnWidth(c, 20 * 256);
        }

        String userHome = System.getProperty("user.home");
        Path directoryPath = Paths.get(userHome, "Downloads");
        Files.createDirectories(directoryPath);
        Path filePath = directoryPath.resolve("expired_reeler_report_" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();
        workbook.dispose();

        return new FileInputStream(filePath.toString());
    }


//    public FileInputStream reelerReport(Long districtId,
//                                           Long talukId,
//                                           Long villageId,
//                                           Long marketId,
//                                           Long casteId,
//                                        int pageNumber,
//                                           int pageSize) throws Exception {
//        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();
//
//        districtId = (districtId != null && districtId == 0) ? null : districtId;
//        talukId = (talukId != null && talukId == 0) ? null : talukId;
//        villageId = (villageId != null && villageId == 0) ? null : villageId;
//        marketId = (marketId != null && marketId == 0) ? null : marketId;
//        casteId = (casteId != null && casteId == 0) ? null : casteId;
//
//
//        Pageable pageable = null; // fetch all records
//        Page<Object[]> applicablePage =
//                reelerRepository.getPrimaryReelerDetails(districtId, talukId, villageId, marketId, casteId, pageable);
//
//        reelerResponse(responseList, applicablePage.getContent(), pageNumber, pageSize);
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Reeler Report");
//
//        // Header row
//        Row headerRow = sheet.createRow(0);
//        headerRow.createCell(0).setCellValue("Sl.No");
//        headerRow.createCell(1).setCellValue("First Name");
//        headerRow.createCell(2).setCellValue("Fruits Id");
//        headerRow.createCell(3).setCellValue("Reeler License Number");
//        headerRow.createCell(4).setCellValue("Father Name");
//        headerRow.createCell(5).setCellValue("Passbook Number");
//        headerRow.createCell(6).setCellValue("Reeler Number");
//        headerRow.createCell(7).setCellValue("Ration Card Number");
//        headerRow.createCell(8).setCellValue("DOB");
//        headerRow.createCell(9).setCellValue("District Name");
//        headerRow.createCell(10).setCellValue("Taluk Name");
//        headerRow.createCell(11).setCellValue("Hobli Name");
//        headerRow.createCell(12).setCellValue("Village Name");
//        headerRow.createCell(13).setCellValue("Bank Name");
//        headerRow.createCell(14).setCellValue("Bank Account Number");
//        headerRow.createCell(15).setCellValue("Branch Name");
//        headerRow.createCell(16).setCellValue("IFSC Code");
//        headerRow.createCell(17).setCellValue("Mobile Number");
//        headerRow.createCell(18).setCellValue("Caste");
//
//
//        // Data rows
//        int dataRow = 1;
//        for (PrimaryReelerDetailsResponse r : responseList) {
//            Row row = sheet.createRow(dataRow++);
//            row.createCell(0).setCellValue(r.getSerialNumber());
//            row.createCell(1).setCellValue(r.getFirstName());
//            row.createCell(2).setCellValue(r.getFruitsId());
//            row.createCell(3).setCellValue(r.getReelerLicenseNumber());
//            row.createCell(4).setCellValue(r.getFatherName());
//            row.createCell(5).setCellValue(r.getPassbookNumber());
//            row.createCell(6).setCellValue(r.getReelerNumber());
//            row.createCell(7).setCellValue(r.getRationCardNumber());
//            row.createCell(8).setCellValue(r.getDob());
//            row.createCell(9).setCellValue(r.getDistrictName());
//            row.createCell(10).setCellValue(r.getTalukName());
//            row.createCell(11).setCellValue(r.getHobliName());
//            row.createCell(12).setCellValue(r.getVillageName());
//            row.createCell(13).setCellValue(r.getReelerBankName());
//            row.createCell(14).setCellValue(r.getReelerBankAccountNumber());
//            row.createCell(15).setCellValue(r.getReelerBankBranchName());
//            row.createCell(16).setCellValue(r.getReelerBankIfscCode());
//            row.createCell(17).setCellValue(r.getReelerMobileNumber());
//            row.createCell(18).setCellValue(r.getCaste());
//
//        }
//
//        // Auto-size
//        for (int col = 0; col <= 18; col++) {
//            sheet.autoSizeColumn(col, true);
//        }
//
//        String userHome = System.getProperty("user.home");
//        String directoryPath = Paths.get(userHome, "Downloads").toString();
//        Files.createDirectories(Paths.get(directoryPath));
//        Path filePath = Paths.get(directoryPath, "reeler_report" + Util.getISTLocalDate() + ".xlsx");
//
//        FileOutputStream fileOut = new FileOutputStream(filePath.toString());
//        FileInputStream fileIn = new FileInputStream(filePath.toString());
//        workbook.write(fileOut);
//        fileOut.close();
//        workbook.close();
//        return fileIn;
//    }

    public static Long objectToLong(Object obj) {
        if (obj == null) return null;

        String value = obj.toString().trim();

        // Remove everything except digits and optional minus sign
        value = value.replaceAll("[^0-9-]", "");

        if (value.isEmpty()) return null;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Invalid numeric value for Long: " + obj);
            return null;
        }
    }


    public FileInputStream reelerReport(Long districtId,
                                        Long talukId,
                                        Long villageId,
                                        Long marketId,
                                        Long casteId,
                                        int pageNumber,
                                        int pageSize) throws Exception {

        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();

        // ✅ Handle zero and null safely
        districtId = (districtId == null || districtId == 0) ? null : districtId;
        talukId = (talukId == null || talukId == 0) ? null : talukId;
        villageId = (villageId == null || villageId == 0) ? null : villageId;
        marketId = (marketId == null || marketId == 0) ? null : marketId;
        casteId = (casteId == null || casteId == 0) ? null : casteId;

        // ✅ Pageable set to unpaged to fetch all records when filters are empty
        Pageable pageable = Pageable.unpaged();

        Page<Object[]> applicablePage =
                reelerRepository.getPrimaryReelerDetails(districtId, talukId, villageId, marketId, casteId, pageable);

        // ✅ Avoid NPE and NumberFormatException
        try {
            reelerResponse(responseList, applicablePage.getContent(), pageNumber, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while parsing reeler data: " + e.getMessage());
            throw new RuntimeException("Data parsing error. Please check FRUITS ID or numeric fields.");
        }

        String[] headerLabels = {
            "Sl.No", "First Name", "Fruits Id", "Reeler License Number", "Father Name",
            // "Passbook Number",
            "Reeler Number", "Mobile Number",
            // "Ration Card Number",
            "DOB",
            "District Name", "Taluk Name",
            // "Hobli Name",
               // "Village Name",
            // "Bank Name", "Bank Account Number", "Branch Name", "IFSC Code",
               "Caste"
        };
        final int TOTAL_COLS = headerLabels.length;

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Reeler Registration Report");

        // Colors
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)26,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)12,  (byte)74,  (byte)158}, null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)30,  (byte)58,  (byte)95},  null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor black       = new XSSFColor(new byte[]{(byte)0,   (byte)0,   (byte)0},   null);

        // Fonts (created once)
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setFontHeightInPoints((short) 11);
        subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setBold(true);
        hdrFont.setFontHeightInPoints((short) 11);
        hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontHeightInPoints((short) 10);
        dataFont.setColor(darkText);

        // Styles (created once before loop)
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(darkNavy);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN);    titleStyle.setTopBorderColor(black);
        titleStyle.setBorderBottom(BorderStyle.THIN); titleStyle.setBottomBorderColor(black);
        titleStyle.setBorderLeft(BorderStyle.THIN);   titleStyle.setLeftBorderColor(black);
        titleStyle.setBorderRight(BorderStyle.THIN);  titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFont(subFont);
        subStyle.setFillForegroundColor(primaryBlue);
        subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER);
        subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setBorderTop(BorderStyle.THIN);    subStyle.setTopBorderColor(black);
        subStyle.setBorderBottom(BorderStyle.THIN); subStyle.setBottomBorderColor(black);
        subStyle.setBorderLeft(BorderStyle.THIN);   subStyle.setLeftBorderColor(black);
        subStyle.setBorderRight(BorderStyle.THIN);  subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFont(hdrFont);
        hdrStyle.setFillForegroundColor(primaryBlue);
        hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER);
        hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN);    hdrStyle.setTopBorderColor(black);
        hdrStyle.setBorderBottom(BorderStyle.THIN); hdrStyle.setBottomBorderColor(black);
        hdrStyle.setBorderLeft(BorderStyle.THIN);   hdrStyle.setLeftBorderColor(black);
        hdrStyle.setBorderRight(BorderStyle.THIN);  hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFont(dataFont);
        dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setVerticalAlignment(VerticalAlignment.CENTER);
        dataWhite.setBorderTop(BorderStyle.THIN);    dataWhite.setTopBorderColor(black);
        dataWhite.setBorderBottom(BorderStyle.THIN); dataWhite.setBottomBorderColor(black);
        dataWhite.setBorderLeft(BorderStyle.THIN);   dataWhite.setLeftBorderColor(black);
        dataWhite.setBorderRight(BorderStyle.THIN);  dataWhite.setRightBorderColor(black);

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
        reportCell.setCellValue("REELER REGISTRATION REPORT");
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

        // ── Rows 4+: Data ─────────────────────────────────────────────────────
        int dataStartsFrom = 4;
        for (int i = 0; i < responseList.size(); i++) {
            Row contentRow = sheet.createRow(dataStartsFrom);
            PrimaryReelerDetailsResponse r = responseList.get(i);
            XSSFCellStyle rowStyle = (i % 2 != 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(r.getSerialNumber()),
                safeString(r.getFirstName()),
                safeString(r.getFruitsId()),
                safeString(r.getReelerLicenseNumber()),
                safeString(r.getFatherName()),
                // safeString(r.getPassbookNumber()),
                safeString(r.getReelerNumber()),
                    safeString(r.getReelerMobileNumber()),
                // safeString(r.getRationCardNumber()),
                safeString(r.getDob()),
                safeString(r.getDistrictName()),
                safeString(r.getTalukName()),
                // safeString(r.getHobliName()),
                // safeString(r.getVillageName()),
                // safeString(r.getReelerBankName()),
                // safeString(r.getReelerBankAccountNumber()),
                // safeString(r.getReelerBankBranchName()),
                // safeString(r.getReelerBankIfscCode()),

                safeString(r.getCaste())
            };
            for (int c = 0; c < values.length; c++) {
                Cell dataCell = contentRow.createCell(c);
                dataCell.setCellValue(values[c]);
                dataCell.setCellStyle(rowStyle);
            }
            dataStartsFrom++;
        }

        sheet.createFreezePane(0, 4);

        for (int c = 0; c < TOTAL_COLS; c++) {
            sheet.setColumnWidth(c, 20 * 256);
        }

        String userHome = System.getProperty("user.home");
        String directoryPath = Paths.get(userHome, "Downloads").toString();
        Files.createDirectories(Paths.get(directoryPath));

        Path filePath = Paths.get(directoryPath, "reeler_report_" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();
        workbook.dispose();

        return new FileInputStream(filePath.toString());
    }

    /**
     * Utility method to avoid null pointer & trim string values.
     */
    private String safeString(Object value) {
        return value == null ? "" : value.toString().trim();
    }



    public FileInputStream renewalReelerReport(Long districtId,
                                               Long talukId,
                                               Long villageId,
                                               Long marketId,
                                               LocalDate renewalDate,
                                               LocalDate expiryDate,
                                               int pageNumber,
                                               int pageSize) throws Exception {

        List<PrimaryReelerDetailsResponse> responseList = new ArrayList<>();

        // convert 0 → null
        districtId = (districtId == 0) ? null : districtId;
        talukId = (talukId == 0) ? null : talukId;
        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;

        // ✅ Fetch all records (no pagination)
        Pageable pageable = null;
        Page<Object[]> applicablePage =
                reelerRepository.getPrimaryReelerForRenewalLicense(
                        districtId, talukId, villageId, marketId, renewalDate, expiryDate, pageable);

        List<Object[]> applicableList = applicablePage.getContent();

        // map results
        reelerResponses(responseList, applicableList, pageNumber, pageSize);

        // ── Styled Excel (SXSSFWorkbook) ─────────────────────────────────────
        String[] hdrLabels = { "Sl.No", "First Name", "Fruits Id", "Reeler License Number", "Father Name",
               // "Passbook Number",
               "Reeler Number",
                // "Ration Card Number",
                "DOB", "District Name",
                "Taluk Name", "Hobli Name", "Village Name",
                //"Bank Name", "Bank Account Number",
               // "Branch Name", "IFSC Code",
                "Mobile Number", "License Renewal Date", "License Expiry Date" };
        final int TOTAL_COLS = hdrLabels.length;
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Renewal Licenses");
        sheet.createFreezePane(0, 4);

        // ── Colours ──────────────────────────────────────────────────────────
        XSSFColor primaryBlue = new XSSFColor(new byte[]{(byte)28,  (byte)95,  (byte)158}, null);
        XSSFColor darkNavy    = new XSSFColor(new byte[]{(byte)13,  (byte)51,  (byte)90},  null);
        XSSFColor altRow      = new XSSFColor(new byte[]{(byte)247, (byte)250, (byte)253}, null);
        XSSFColor darkText    = new XSSFColor(new byte[]{(byte)45,  (byte)55,  (byte)72},  null);
        XSSFColor white       = new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null);
        XSSFColor black       = new XSSFColor(new byte[]{(byte)0,   (byte)0,   (byte)0},   null);

        // ── Fonts ────────────────────────────────────────────────────────────
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true); titleFont.setFontHeightInPoints((short)16); titleFont.setColor(white);

        XSSFFont subFont = (XSSFFont) workbook.createFont();
        subFont.setBold(false); subFont.setFontHeightInPoints((short)11); subFont.setColor(white);

        XSSFFont hdrFont = (XSSFFont) workbook.createFont();
        hdrFont.setBold(true); hdrFont.setFontHeightInPoints((short)11); hdrFont.setColor(white);

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontHeightInPoints((short)10); dataFont.setColor(darkText);

        // ── Styles ───────────────────────────────────────────────────────────
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFillForegroundColor(darkNavy); titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER); titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setBorderTop(BorderStyle.THIN);    titleStyle.setTopBorderColor(black);
        titleStyle.setBorderBottom(BorderStyle.THIN); titleStyle.setBottomBorderColor(black);
        titleStyle.setBorderLeft(BorderStyle.THIN);   titleStyle.setLeftBorderColor(black);
        titleStyle.setBorderRight(BorderStyle.THIN);  titleStyle.setRightBorderColor(black);

        XSSFCellStyle subStyle = (XSSFCellStyle) workbook.createCellStyle();
        subStyle.setFillForegroundColor(primaryBlue); subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subStyle.setAlignment(HorizontalAlignment.CENTER); subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        subStyle.setFont(subFont);
        subStyle.setBorderTop(BorderStyle.THIN);    subStyle.setTopBorderColor(black);
        subStyle.setBorderBottom(BorderStyle.THIN); subStyle.setBottomBorderColor(black);
        subStyle.setBorderLeft(BorderStyle.THIN);   subStyle.setLeftBorderColor(black);
        subStyle.setBorderRight(BorderStyle.THIN);  subStyle.setRightBorderColor(black);

        XSSFCellStyle hdrStyle = (XSSFCellStyle) workbook.createCellStyle();
        hdrStyle.setFillForegroundColor(primaryBlue); hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        hdrStyle.setAlignment(HorizontalAlignment.CENTER); hdrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        hdrStyle.setFont(hdrFont); hdrStyle.setWrapText(true);
        hdrStyle.setBorderTop(BorderStyle.THIN);    hdrStyle.setTopBorderColor(black);
        hdrStyle.setBorderBottom(BorderStyle.THIN); hdrStyle.setBottomBorderColor(black);
        hdrStyle.setBorderLeft(BorderStyle.THIN);   hdrStyle.setLeftBorderColor(black);
        hdrStyle.setBorderRight(BorderStyle.THIN);  hdrStyle.setRightBorderColor(black);

        XSSFCellStyle dataWhite = (XSSFCellStyle) workbook.createCellStyle();
        dataWhite.setFillForegroundColor(white); dataWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataWhite.setAlignment(HorizontalAlignment.CENTER); dataWhite.setVerticalAlignment(VerticalAlignment.CENTER);
        dataWhite.setFont(dataFont);
        dataWhite.setBorderTop(BorderStyle.THIN);    dataWhite.setTopBorderColor(black);
        dataWhite.setBorderBottom(BorderStyle.THIN); dataWhite.setBottomBorderColor(black);
        dataWhite.setBorderLeft(BorderStyle.THIN);   dataWhite.setLeftBorderColor(black);
        dataWhite.setBorderRight(BorderStyle.THIN);  dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.setFillForegroundColor(altRow); dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataAlt.setAlignment(HorizontalAlignment.CENTER); dataAlt.setVerticalAlignment(VerticalAlignment.CENTER);
        dataAlt.setFont(dataFont);
        dataAlt.setBorderTop(BorderStyle.THIN);    dataAlt.setTopBorderColor(black);
        dataAlt.setBorderBottom(BorderStyle.THIN); dataAlt.setBottomBorderColor(black);
        dataAlt.setBorderLeft(BorderStyle.THIN);   dataAlt.setLeftBorderColor(black);
        dataAlt.setBorderRight(BorderStyle.THIN);  dataAlt.setRightBorderColor(black);

        // ── Title rows (0-2) ─────────────────────────────────────────────────
        Row titleRow = sheet.createRow(0); titleRow.setHeightInPoints(36);
        titleRow.createCell(0).setCellValue("Department of Sericulture, Government of Karnataka");
        titleRow.getCell(0).setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { titleRow.createCell(c).setCellStyle(titleStyle); }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        Row reportRow = sheet.createRow(1); reportRow.setHeightInPoints(28);
        reportRow.createCell(0).setCellValue("RENEWAL OF REELER LICENSE REPORT");
        reportRow.getCell(0).setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { reportRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        Row genRow = sheet.createRow(2); genRow.setHeightInPoints(22);
        genRow.createCell(0).setCellValue("Generated On: " + Util.getISTLocalDate());
        genRow.getCell(0).setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) { genRow.createCell(c).setCellStyle(subStyle); }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLS - 1));

        // ── Header row (3) ───────────────────────────────────────────────────
        Row hdrRow = sheet.createRow(3); hdrRow.setHeightInPoints(32);
        for (int c = 0; c < TOTAL_COLS; c++) {
            Cell hc = hdrRow.createCell(c);
            hc.setCellValue(hdrLabels[c]);
            hc.setCellStyle(hdrStyle);
            sheet.setColumnWidth(c, 22 * 256);
        }

        // ── Data rows (4+) ───────────────────────────────────────────────────
        int rowIdx = 4;
        int slNo = 1;
        for (PrimaryReelerDetailsResponse reeler : responseList) {
            Row row = sheet.createRow(rowIdx);
            XSSFCellStyle rowStyle = (rowIdx % 2 == 0) ? dataWhite : dataAlt;
            String[] values = {
                String.valueOf(slNo++),
                reeler.getFirstName(),
                reeler.getFruitsId(),
                reeler.getReelerLicenseNumber(),
                reeler.getFatherName(),
                //reeler.getPassbookNumber(),
                reeler.getReelerNumber(),
                //reeler.getRationCardNumber(),
                reeler.getDob(),
                reeler.getDistrictName(),
                reeler.getTalukName(),
                reeler.getHobliName(),
                reeler.getVillageName(),
//                reeler.getReelerBankName(),
//               reeler.getReelerBankAccountNumber(),
//               reeler.getReelerBankBranchName(),
//                reeler.getReelerBankIfscCode(),
                reeler.getReelerMobileNumber() != null ? String.valueOf(reeler.getReelerMobileNumber()) : "",
                reeler.getRenewalDate(),
                reeler.getExpiryDate()
            };
            for (int c = 0; c < values.length; c++) {
                Cell dataCell = row.createCell(c);
                dataCell.setCellValue(values[c] != null ? values[c] : "");
                dataCell.setCellStyle(rowStyle);
            }
            rowIdx++;
        }

        String userHome = System.getProperty("user.home");
        Path directoryPath = Paths.get(userHome, "Downloads");
        Files.createDirectories(directoryPath);
        Path filePath = directoryPath.resolve("renewalReelers" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();
        workbook.dispose();

        return new FileInputStream(filePath.toString());
    }


    public ResponseEntity<?> primaryReelerMarketDetails(
            Long marketId,
            int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList = new ArrayList<>();

//        districtId = (districtId == 0) ? null : districtId;
//        talukId = (talukId == 0) ? null : talukId;
//        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Object[]> applicablePage = reelerRepository.getPrimaryReelerDetailsByMarket(marketId, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();


        reelerMarketResponse(primaryReelerDetailsResponseList, applicableList, pageNumber, pageSize);
        rw.setTotalRecords(totalRecords);
        rw.setContent(primaryReelerDetailsResponseList);
        return ResponseEntity.ok(rw);
    }

    private static void reelerMarketResponse(List<PrimaryReelerDetailsResponse> primaryReelerDetailsResponseList, List<Object[]> applicableList, int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (Object[] arr : applicableList) {
            PrimaryReelerDetailsResponse primaryReelerDetailsResponse;
            primaryReelerDetailsResponse = PrimaryReelerDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .reelerId(Util.objectToString(arr[0]))
                    .firstName(Util.objectToString(arr[1]))
                    .fruitsId(Util.objectToString(arr[2]))
                    .reelerLicenseNumber(Util.objectToString(arr[3]))
                    .fatherName(Util.objectToString(arr[4]))
                    .passbookNumber(Util.objectToString(arr[5]))
                    .reelerNumber(Util.objectToString(arr[6]))
                    .rationCardNumber(Util.objectToString(arr[7]))
                    .dob(Util.objectToString(arr[8]))
                    .districtName(Util.objectToString(arr[9]))
                    .talukName(Util.objectToString(arr[10]))
                    .hobliName(Util.objectToString(arr[11]))
                    .villageName(Util.objectToString(arr[12]))
                    .reelerBankName(Util.objectToString(arr[13]))
                    .reelerBankAccountNumber(Util.objectToString(arr[14]))
                    .reelerBankBranchName(Util.objectToString(arr[15]))
                    .reelerBankIfscCode(Util.objectToString(arr[16]))
                    .build();
            primaryReelerDetailsResponseList.add(primaryReelerDetailsResponse);
        }
    }

    public FileInputStream reelerMarketReport(
            Long marketId,
            int pageNumber, int pageSize) throws Exception {
        List<PrimaryReelerDetailsResponse> primaryDetailsResponseList = new ArrayList<>();


        Page<Object[]> applicablePage;
//        districtId = (districtId == 0) ? null : districtId;
//        talukId = (talukId == 0) ? null : talukId;
//        villageId = (villageId == 0) ? null : villageId;
        marketId = (marketId == 0) ? null : marketId;
        Pageable pageable = null;
        applicablePage = reelerRepository.getPrimaryReelerDetailsByMarket(marketId, pageable);
        List<Object[]> applicableList = applicablePage.getContent();
        reelerResponse(primaryDetailsResponseList, applicableList, pageNumber, pageSize);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("First Name");
        headerRow.createCell(1).setCellValue("Fruits Id");
        headerRow.createCell(2).setCellValue("Reeler License Number");
        headerRow.createCell(3).setCellValue("Father Name");
        headerRow.createCell(4).setCellValue("Passbook Number");
        headerRow.createCell(5).setCellValue("Reeler Number");
        headerRow.createCell(6).setCellValue("Ration Card Number");
        headerRow.createCell(7).setCellValue("DOB");
        headerRow.createCell(8).setCellValue("District Name");
        headerRow.createCell(9).setCellValue("Taluk Name");
        headerRow.createCell(10).setCellValue("Hobli Name");
        headerRow.createCell(11).setCellValue("Village Name");
        headerRow.createCell(12).setCellValue("Bank Name");
        headerRow.createCell(13).setCellValue("Bank Account Number");
        headerRow.createCell(14).setCellValue("Branch Name");
        headerRow.createCell(15).setCellValue("IFSC Code");

        //Dynamic data binds here
        //Starting 0th and 1st column cells are hardcoded, So dynamic data column starts from 2nd column
        int dataStartsFrom = 1;
        for (int i = 0; i < primaryDetailsResponseList.size(); i++) {
            Row contentRow = sheet.createRow(dataStartsFrom);
            PrimaryReelerDetailsResponse primaryDetailsResponse = primaryDetailsResponseList.get(i);
            contentRow.createCell(0).setCellValue(primaryDetailsResponse.getFirstName());
            contentRow.createCell(1).setCellValue(primaryDetailsResponse.getFruitsId());
            contentRow.createCell(2).setCellValue(primaryDetailsResponse.getReelerLicenseNumber());
            contentRow.createCell(3).setCellValue(primaryDetailsResponse.getFatherName());
            contentRow.createCell(4).setCellValue(primaryDetailsResponse.getPassbookNumber());
            contentRow.createCell(5).setCellValue(primaryDetailsResponse.getReelerNumber());
            contentRow.createCell(6).setCellValue(primaryDetailsResponse.getRationCardNumber());
            contentRow.createCell(7).setCellValue(primaryDetailsResponse.getDob());
            contentRow.createCell(8).setCellValue(primaryDetailsResponse.getDistrictName());
            contentRow.createCell(9).setCellValue(primaryDetailsResponse.getTalukName());
            contentRow.createCell(10).setCellValue(primaryDetailsResponse.getHobliName());
            contentRow.createCell(11).setCellValue(primaryDetailsResponse.getVillageName());
            contentRow.createCell(12).setCellValue(primaryDetailsResponse.getReelerBankName());
            contentRow.createCell(13).setCellValue(primaryDetailsResponse.getReelerBankAccountNumber());
            contentRow.createCell(14).setCellValue(primaryDetailsResponse.getReelerBankBranchName());
            contentRow.createCell(15).setCellValue(primaryDetailsResponse.getReelerBankIfscCode());
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
        Path filePath = directory.resolve("reelers" + Util.getISTLocalDate() + ".xlsx");

        // Write the workbook content to the specified file path
        FileOutputStream fileOut = new FileOutputStream(filePath.toString());
        FileInputStream fileIn = new FileInputStream(filePath.toString());
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        return fileIn;
    }

    public List<ReelerDetailsResponse> getReelerDetailsByUserMasterId(Long userId) {
        List<Object[]> chowkiDetails = reelerRepository.getReelerDetailsByUserMasterId(userId);
        List<ReelerDetailsResponse> responses = new ArrayList<>();

        for (Object[] arr : chowkiDetails) {
            ReelerDetailsResponse response = ReelerDetailsResponse.builder()
                    .reelerId(Util.objectToLong(arr[0]))
                    .name(Util.objectToString(arr[1]))
                    .passbookNumber(Util.objectToString(arr[2]))
                    .fatherName(Util.objectToString(arr[3]))
                    .dob(Util.objectToLocalDate(arr[4]))
                    .gender(Util.objectToString(arr[5]))
                    .mobileNumber(Util.objectToString(arr[6]))
                    .arnNumber(Util.objectToString(arr[7]))
                    .stateName(Util.objectToString(arr[8]))
                    .districtName(Util.objectToString(arr[9]))
                    .talukName(Util.objectToString(arr[10]))
                    .hobliName(Util.objectToString(arr[11]))
                    .villageName(Util.objectToString(arr[12]))
                    .address(Util.objectToString(arr[13]))
                    .pincode(Util.objectToString(arr[14]))
                    .reelerLicenseNumber(Util.objectToString(arr[15]))
                    .reelerNumber(Util.objectToString(arr[16]))
                    .fruitsId(Util.objectToString(arr[17]))
                    .build();

            responses.add(response);
        }

        return responses;
    }
}
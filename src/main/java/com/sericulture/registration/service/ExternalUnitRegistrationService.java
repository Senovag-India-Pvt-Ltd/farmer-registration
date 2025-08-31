package com.sericulture.registration.service;

import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.*;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseResponse;
import com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.ExternalUnitRegistration;
import com.sericulture.registration.model.entity.SerialCounter;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.ExternalUnitRegistrationRepository;
import com.sericulture.registration.repository.SerialCounterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class ExternalUnitRegistrationService {

    @Autowired
    ExternalUnitRegistrationRepository externalUnitRegistrationRepository;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    SerialCounterRepository serialCounterRepository;

//    @Transactional
//    public ExternalUnitRegistrationResponse insertExternalUnitRegistrationDetails(ExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
//        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
//
//        // Map request to entity
//        ExternalUnitRegistration externalUnitRegistration = mapper.externalUnitRegistrationObjectToEntity(externalUnitRegistrationRequest, ExternalUnitRegistration.class);
//
//        // Retrieve userMasterId from JWT token and set it on the entity
//        externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));
//
//        // Validate the entity
//        validator.validate(externalUnitRegistration);
//
//        // Save and map the response
//        return mapper.externalUnitRegistrationEntityToObject(
//                externalUnitRegistrationRepository.save(externalUnitRegistration),
//                ExternalUnitRegistrationResponse.class
//        );
//    }
//    @Transactional
//    public ExternalUnitRegistrationResponse insertExternalUnitRegistrationDetails(ExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
//        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
//
//        // Map request to entity
//        ExternalUnitRegistration externalUnitRegistration = mapper.externalUnitRegistrationObjectToEntity(externalUnitRegistrationRequest, ExternalUnitRegistration.class);
//
//        // Retrieve userMasterId from JWT token and set it on the entity
//        externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));
//
//        // Validate the entity
//        validator.validate(externalUnitRegistration);
//
//        // Generate ARN Number
//        LocalDate today = Util.getISTLocalDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
//        String formattedDate = today.format(formatter);
//
//        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
//        SerialCounter serialCounter = new SerialCounter();
//
//        if (!serialCounters.isEmpty()) {
//            serialCounter = serialCounters.get(0);
//            long counterValue = (serialCounter.getExternalCounterNumber() != null)
//                    ? serialCounter.getExternalCounterNumber() + 1
//                    : 1L;
//            serialCounter.setExternalCounterNumber(counterValue);
//        } else {
//            serialCounter.setExternalCounterNumber(1L);
//        }
//
//        serialCounterRepository.save(serialCounter);
//        String formattedNumber = String.format("%05d", serialCounter.getExternalCounterNumber());
//
//        externalUnitRegistration.setExternalUnitNumber("EUN/" + formattedDate + "/" + formattedNumber);
//
//        // Save and map the response
//        return mapper.externalUnitRegistrationEntityToObject(
//                externalUnitRegistrationRepository.save(externalUnitRegistration),
//                ExternalUnitRegistrationResponse.class
//        );
//    }

    @Transactional
    public ExternalUnitRegistrationResponse insertExternalUnitRegistrationDetails(ExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
        ExternalUnitRegistrationResponse response = new ExternalUnitRegistrationResponse();
        List<Long> externalUnitRegistrationIds = new ArrayList<>();

        // Validate if ExternalUnitRegistrationDetailsRequest is empty
        if (externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests() == null
                || externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests().isEmpty()) {
            response.setError(true);
            response.setError_description("Fill the Virtual Bank details");
            return response;
        }

        // Generate a single External Unit Number
        LocalDate today = Util.getISTLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String formattedDate = today.format(formatter);

        List<SerialCounter> serialCounters = serialCounterRepository.findByActive(true);
        SerialCounter serialCounter = serialCounters.isEmpty() ? new SerialCounter() : serialCounters.get(0);

        long counterValue = (serialCounter.getExternalCounterNumber() != null) ? serialCounter.getExternalCounterNumber() + 1 : 1L;
        serialCounter.setExternalCounterNumber(counterValue);
        serialCounterRepository.save(serialCounter);

        String formattedNumber = String.format("%05d", serialCounter.getExternalCounterNumber());
        String externalUnitNumber = "EUN/" + formattedDate + "/" + formattedNumber;

        // Loop through details and save each entry with the same External Unit Number
        for (ExternalUnitRegistrationDetailsRequest details : externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()) {
            ExternalUnitRegistration externalUnitRegistration = mapper.externalUnitRegistrationObjectToEntity(externalUnitRegistrationRequest, ExternalUnitRegistration.class);

            // Set userMasterId from JWT token
            externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));

            // Set additional fields
            externalUnitRegistration.setVirtualAccountNumber(details.getVirtualAccountNumber());
            externalUnitRegistration.setBranchName(details.getBranchName());
            externalUnitRegistration.setIfscCode(details.getIfscCode());
            externalUnitRegistration.setMarketMasterId(details.getMarketMasterId());

            // Assign the same External Unit Number
            externalUnitRegistration.setExternalUnitNumber(externalUnitNumber);

            // Validate entity
            validator.validate(externalUnitRegistration);

            // Save entity and store its ID
            externalUnitRegistration = externalUnitRegistrationRepository.save(externalUnitRegistration);
            externalUnitRegistrationIds.add(externalUnitRegistration.getExternalUnitRegistrationId());
        }

        // Set response data
        response.setExternalUnitRegistrationIds(externalUnitRegistrationIds);
        response.setExternalUnitNumber(externalUnitNumber);
        response.setError(false);

        return response;
    }

    public Map<String, Object> getPaginatedExternalUnitRegistrationDetails(final Pageable pageable) {
        return convertToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByExternalUnitRegistrationIdAsc(true, pageable));
    }

    public Map<String, Object> getAllByActive(boolean isActive) {
        return convertListToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByExternalUnitRegistrationIdAsc(isActive));


    }



    private Map<String, Object> convertToMapResponse(final Page<ExternalUnitRegistration> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration, ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration", externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());

        return response;
    }

    public Map<String,Object> getPaginatedExternalUnitRegistrationDetailsWithJoin(final Pageable pageable){
        return convertDTOToMapResponse(externalUnitRegistrationRepository.getByActiveOrderByExternalUnitRegistrationIdAsc( true, pageable));
    }

    public ResponseEntity<?> externalUnitList(Long raceMasterId,
                                              Long externalUnitTypeId,
                                              int pageNumber,
                                              int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<ExternalUnitRegistrationResponse> responseList = new ArrayList<>();

        // Convert 0 → null
        raceMasterId = (raceMasterId != null && raceMasterId == 0) ? null : raceMasterId;
        externalUnitTypeId = (externalUnitTypeId != null && externalUnitTypeId == 0) ? null : externalUnitTypeId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<ExternalUnitRegistrationDTO> page = externalUnitRegistrationRepository.getByActiveAndFilters(
                true, raceMasterId, externalUnitTypeId, pageable);

        List<ExternalUnitRegistrationDTO> list = page.getContent();
        long totalRecords = page.getTotalElements();

        // Mapping
        externalUnitResponses(responseList, list, pageNumber, pageSize);

        rw.setTotalRecords(totalRecords);
        rw.setContent(responseList);
        return ResponseEntity.ok(rw);
    }

    private static void externalUnitResponses(List<ExternalUnitRegistrationResponse> responseList,
                                              List<ExternalUnitRegistrationDTO> dtoList,
                                              int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (ExternalUnitRegistrationDTO dto : dtoList) {
            ExternalUnitRegistrationResponse response = ExternalUnitRegistrationResponse.builder()
                    .serialNumber(serialNumber++)
                    .externalUnitRegistrationId(dto.getExternalUnitRegistrationId())
                    .externalUnitTypeId(dto.getExternalUnitTypeId())
                    .externalUnitTypeName(dto.getExternalUnitTypeName())
                    .name(dto.getName())
                    .address(dto.getAddress())
                    .licenseNumber(dto.getLicenseNumber())
                    .externalUnitNumber(dto.getExternalUnitNumber())
                    .organisationName(dto.getOrganisationName())
                    .raceMasterId(dto.getRaceMasterId())
                    .raceMasterName(dto.getRaceMasterName())
                    .capacity(dto.getCapacity())
                    .virtualAccountNumber(dto.getVirtualAccountNumber())
                    .ifscCode(dto.getIfscCode())
                    .branchName(dto.getBranchName())
                    .marketMasterName(dto.getMarketMasterName())
                    .lotNumberNomenclature(dto.getLotNumberNomenclature())
                    .build();
            responseList.add(response);
        }
    }

    public FileInputStream externalUnitReport(
            boolean isActive,
            Long raceMasterId,
            Long externalUnitTypeId,
            int pageNumber,
            int pageSize) throws Exception {

        raceMasterId = (raceMasterId != null && raceMasterId == 0) ? null : raceMasterId;
        externalUnitTypeId = (externalUnitTypeId != null && externalUnitTypeId == 0) ? null : externalUnitTypeId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ExternalUnitRegistrationDTO> page = externalUnitRegistrationRepository.getByActiveAndFilters(
                isActive, raceMasterId, externalUnitTypeId, pageable);

        List<ExternalUnitRegistrationDTO> units = page.getContent();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("External Units");

        // Header Row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("S.No");
        headerRow.createCell(1).setCellValue("Unit Name");
        headerRow.createCell(2).setCellValue("Unit Type");
        headerRow.createCell(3).setCellValue("Race");
        headerRow.createCell(4).setCellValue("License Number");
        headerRow.createCell(5).setCellValue("Organisation");
        headerRow.createCell(6).setCellValue("Market");
        headerRow.createCell(7).setCellValue("Lot Number Nomenclature");
        headerRow.createCell(8).setCellValue("Virtual Account");
        headerRow.createCell(9).setCellValue("IFSC Code");
        headerRow.createCell(10).setCellValue("Branch Name");

        // Data Rows
        int rowIdx = 1;
        int serialNo = 1;
        for (ExternalUnitRegistrationDTO dto : units) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(serialNo++);
            row.createCell(1).setCellValue(dto.getName());
            row.createCell(2).setCellValue(dto.getExternalUnitTypeName());
            row.createCell(3).setCellValue(dto.getRaceMasterName());
            row.createCell(4).setCellValue(dto.getLicenseNumber());
            row.createCell(5).setCellValue(dto.getOrganisationName());
            row.createCell(6).setCellValue(dto.getMarketMasterName());
            row.createCell(7).setCellValue(dto.getLotNumberNomenclature());
            row.createCell(8).setCellValue(dto.getVirtualAccountNumber());
            row.createCell(9).setCellValue(dto.getIfscCode());
            row.createCell(10).setCellValue(dto.getBranchName());
        }

        for (int i = 0; i <= 11; i++) {
            sheet.autoSizeColumn(i);
        }

        String userHome = System.getProperty("user.home");
        Path directory = Paths.get(userHome, "Downloads");
        Files.createDirectories(directory);
        Path filePath = directory.resolve("external_unit_report_" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();

        return new FileInputStream(filePath.toString());
    }

    private Map<String, Object> convertDTOToMapResponse(final Page<ExternalUnitRegistrationDTO> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationDTOToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());
        return response;
    }

    public Map<String,Object> getExternalUnitRegistrationByExternalUnitId(Long externalUnitTypeId){
        return convertListToMapResponse(externalUnitRegistrationRepository.findByExternalUnitTypeIdAndActive( externalUnitTypeId,true));
    }

    private Map<String, Object> convertListToMapResponse(final List<ExternalUnitRegistration> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("totalItems", activeExternalUnitRegistrations.size());
        return response;
    }

    @Transactional
    public ExternalUnitRegistrationResponse deleteExternalUnitRegistrationDetails(long id) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActive(id, true);
        if (Objects.nonNull(externalUnitRegistration)) {
            externalUnitRegistration.setActive(false);
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistrationRepository.save(externalUnitRegistration), ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        } else {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return externalUnitRegistrationResponse;
    }

    public ExternalUnitRegistrationResponse getById(int id) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActive(id, true);
        if (externalUnitRegistration == null) {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid id");
        } else {
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        }
        log.info("Entity is ", externalUnitRegistration);
        return externalUnitRegistrationResponse;
    }

    public ExternalUnitRegistrationResponse getByIdJoin(int id){
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
        ExternalUnitRegistrationDTO externalUnitRegistrationDTO = externalUnitRegistrationRepository.getByExternalUnitRegistrationIdAndActive(id,true);
        if(externalUnitRegistrationDTO == null){
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Invalid id");
        } else {
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationDTOToObject(externalUnitRegistrationDTO, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        }
        log.info("Entity is ", externalUnitRegistrationDTO);
        return externalUnitRegistrationResponse;
    }

    @Transactional
    public ExternalUnitRegistrationResponse updateExternalUnitRegistrationDetails(EditExternalUnitRegistrationRequest externalUnitRegistrationRequest) {
        ExternalUnitRegistrationResponse externalUnitRegistrationResponse = new ExternalUnitRegistrationResponse();
      /*  List<ExternalUnitRegistration> externalUnitRegistrationList = externalUnitRegistrationRepository.findByExternalUnitRegistrationName(externalUnitRegistrationRequest.getExternalUnitRegistrationName());
        if(externalUnitRegistrationList.size()>0){
            throw new ValidationException("ExternalUnitRegistration already exists with this name, duplicates are not allowed.");
        }*/

        ExternalUnitRegistration externalUnitRegistration = externalUnitRegistrationRepository.findByExternalUnitRegistrationIdAndActiveIn(externalUnitRegistrationRequest.getExternalUnitRegistrationId(), Set.of(true, false));
        if (Objects.nonNull(externalUnitRegistration)) {
            externalUnitRegistration.setAddress(externalUnitRegistrationRequest.getAddress());
            externalUnitRegistration.setName(externalUnitRegistrationRequest.getName());
            externalUnitRegistration.setLicenseNumber(externalUnitRegistrationRequest.getLicenseNumber());
            externalUnitRegistration.setExternalUnitNumber(externalUnitRegistrationRequest.getExternalUnitNumber());
            externalUnitRegistration.setExternalUnitTypeId(externalUnitRegistrationRequest.getExternalUnitTypeId());
            externalUnitRegistration.setOrganisationName(externalUnitRegistrationRequest.getOrganisationName());
            externalUnitRegistration.setRaceMasterId(externalUnitRegistrationRequest.getRaceMasterId());
            externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));
            externalUnitRegistration.setCapacity(externalUnitRegistrationRequest.getCapacity());
            externalUnitRegistration.setVirtualAccountNumber(externalUnitRegistrationRequest.getVirtualAccountNumber());
            externalUnitRegistration.setBranchName(externalUnitRegistrationRequest.getBranchName());
            externalUnitRegistration.setIfscCode(externalUnitRegistrationRequest.getIfscCode());
            externalUnitRegistration.setMarketMasterId(externalUnitRegistrationRequest.getMarketMasterId());
            externalUnitRegistration.setLotNumberNomenclature(externalUnitRegistrationRequest.getLotNumberNomenclature());
            externalUnitRegistration.setActive(true);
            ExternalUnitRegistration externalUnitRegistration1 = externalUnitRegistrationRepository.save(externalUnitRegistration);
            externalUnitRegistrationResponse = mapper.externalUnitRegistrationEntityToObject(externalUnitRegistration1, ExternalUnitRegistrationResponse.class);
            externalUnitRegistrationResponse.setError(false);
        } else {
            externalUnitRegistrationResponse.setError(true);
            externalUnitRegistrationResponse.setError_description("Error occurred while fetching externalUnitRegistration");
            // throw new ValidationException("Error occurred while fetching village");
        }
        return externalUnitRegistrationResponse;
    }

    public Map<String,Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest){
        if(searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")){
            searchWithSortRequest.setSearchText("%%");
        }else{
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if(searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")){
            searchWithSortRequest.setSortColumn("externalUnitType.externalUnitTypeName");
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
        Page<ExternalUnitRegistrationDTO> externalUnitRegistrationDTOS = externalUnitRegistrationRepository.getSortedExternalUnitRegistration(searchWithSortRequest.getJoinColumn(),searchWithSortRequest.getSearchText(),true, pageable);
        log.info("Entity is ",externalUnitRegistrationDTOS);
        return convertPageableDTOToMapResponse(externalUnitRegistrationDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<ExternalUnitRegistrationDTO> activeExternalUnitRegistrations) {
        Map<String, Object> response = new HashMap<>();

        List<ExternalUnitRegistrationResponse> externalUnitRegistrationResponses = activeExternalUnitRegistrations.getContent().stream()
                .map(externalUnitRegistration -> mapper.externalUnitRegistrationDTOToObject(externalUnitRegistration,ExternalUnitRegistrationResponse.class)).collect(Collectors.toList());
        response.put("externalUnitRegistration",externalUnitRegistrationResponses);
        response.put("currentPage", activeExternalUnitRegistrations.getNumber());
        response.put("totalItems", activeExternalUnitRegistrations.getTotalElements());
        response.put("totalPages", activeExternalUnitRegistrations.getTotalPages());

        return response;
    }


}
package com.sericulture.registration.service;

import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.*;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseResponse;
import com.sericulture.registration.model.dto.externalUnitRegistration.ExternalUnitRegistrationDTO;
import com.sericulture.registration.model.dto.traderLicense.TraderLicenseDTO;
import com.sericulture.registration.model.entity.EuVirtualBankAccount;
import com.sericulture.registration.model.entity.ExternalUnitRegistration;
import com.sericulture.registration.model.entity.MarketMaster;
import com.sericulture.registration.model.entity.SerialCounter;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.EuVirtualBankAccountRepository;
import com.sericulture.registration.repository.ExternalUnitRegistrationRepository;
import com.sericulture.registration.repository.MarketMasterRepository;
import com.sericulture.registration.repository.SerialCounterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
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

    @Autowired
    EuVirtualBankAccountRepository euVirtualBankAccountRepository;

    @Autowired
    MarketMasterRepository marketMasterRepository;

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
//
//        // Validate if ExternalUnitRegistrationDetailsRequest is empty
//        if (externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests() == null
//                || externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests().isEmpty()) {
//            response.setError(true);
//            response.setError_description("Fill the Virtual Bank details");
//            return response;
//        }

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
//        for (ExternalUnitRegistrationDetailsRequest details : externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()) {
//            long count = externalUnitRegistrationRepository
//                    .existsByVirtualAccountNumberAndDifferentMarket(
//                            details.getVirtualAccountNumber(),
//                            details.getMarketMasterId()
//                    );
//
//            if (count > 0) {
//                response.setError(true);
//                response.setError_description("Virtual Account Number already exists in another market");
//                return response;
//            }
          ExternalUnitRegistration externalUnitRegistration = mapper.externalUnitRegistrationObjectToEntity(externalUnitRegistrationRequest, ExternalUnitRegistration.class);

            // Set userMasterId from JWT token
            externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));

        externalUnitRegistration.setDistrictId(externalUnitRegistrationRequest.getDistrictId());
        externalUnitRegistration.setTalukId(externalUnitRegistrationRequest.getTalukId());
        externalUnitRegistration.setTscMasterId(externalUnitRegistrationRequest.getTscMasterId());
        externalUnitRegistration.setNameKan(externalUnitRegistrationRequest.getNameKan());
        externalUnitRegistration.setBankName(externalUnitRegistrationRequest.getBankName());
        externalUnitRegistration.setBankAccountNumber(externalUnitRegistrationRequest.getBankAccountNumber());
        externalUnitRegistration.setBankBranchName(externalUnitRegistrationRequest.getBankBranchName());
        externalUnitRegistration.setBankIfscCode(externalUnitRegistrationRequest.getBankIfscCode());

//            // Set additional fields
//            externalUnitRegistration.setVirtualAccountNumber(details.getVirtualAccountNumber());
//            externalUnitRegistration.setBranchName(details.getBranchName());
//            externalUnitRegistration.setIfscCode(details.getIfscCode());
//            externalUnitRegistration.setMarketMasterId(details.getMarketMasterId());

            // Assign the same External Unit Number
            externalUnitRegistration.setExternalUnitNumber(externalUnitNumber);

            // Validate entity
            validator.validate(externalUnitRegistration);

            // Save entity and store its ID
            externalUnitRegistration = externalUnitRegistrationRepository.save(externalUnitRegistration);
            externalUnitRegistrationIds.add(externalUnitRegistration.getExternalUnitRegistrationId());
// ================== ADD FROM HERE ==================
        if (externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests() != null
                && !externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests().isEmpty()) {

            for (ExternalUnitRegistrationDetailsRequest details :
                    externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()) {
                if (Boolean.TRUE.equals(details.getDeleted())) {
                    continue;
                }
// Skip if virtual account is empty
                if (details.getVirtualAccountNumber() == null ||
                        details.getVirtualAccountNumber().trim().isEmpty()) {
                    continue;
                }

                // Duplicate check
                List<EuVirtualBankAccount> existing =
                        euVirtualBankAccountRepository
                                .findByVirtualAccountNumber(details.getVirtualAccountNumber());

                if (!existing.isEmpty()) {
                    log.warn("Virtual Account already exists: {}", details.getVirtualAccountNumber());
                    continue; // don't fail
                }

                // Create object
                EuVirtualBankAccount euVirtualBankAccount = new EuVirtualBankAccount();

                // Set values
                euVirtualBankAccount.setEuId(externalUnitRegistration.getExternalUnitRegistrationId());
                euVirtualBankAccount.setVirtualAccountNumber(details.getVirtualAccountNumber());
                euVirtualBankAccount.setBranchName(details.getBranchName());
                euVirtualBankAccount.setIfscCode(details.getIfscCode());
                euVirtualBankAccount.setMarketMasterId(details.getMarketMasterId());
                euVirtualBankAccount.setLock(
                        details.getLock() != null ? details.getLock() : false
                );
                euVirtualBankAccount.setActive(true);

                // Save
                euVirtualBankAccountRepository.save(euVirtualBankAccount);
            }
        }

// ================== ADD TILL HERE ==================

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
        return convertListToMapResponse(externalUnitRegistrationRepository.findByActiveOrderByNameAsc(isActive));


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
//                    .virtualAccountNumber(dto.getVirtualAccountNumber())
//                    .ifscCode(dto.getIfscCode())
//                    .branchName(dto.getBranchName())
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

        // Convert 0 → null
        raceMasterId = (raceMasterId != null && raceMasterId == 0) ? null : raceMasterId;
        externalUnitTypeId = (externalUnitTypeId != null && externalUnitTypeId == 0) ? null : externalUnitTypeId;

        // ✅ fetch ALL records (ignore pagination)
        Pageable pageable = null;
        Page<ExternalUnitRegistrationDTO> page =
                externalUnitRegistrationRepository.getByActiveAndFilters(isActive, raceMasterId, externalUnitTypeId, pageable);

        List<ExternalUnitRegistrationDTO> units = page.getContent();

        // ── Styled Excel (SXSSFWorkbook) ─────────────────────────────────────
        String[] hdrLabels = { "S.No", "Unit Name", "Address", "License Number", "External Unit Number",
                "Organisation Name", "Capacity", "Unit Type Name",
                //"Virtual Account", "IFSC Code", "Branch Name",
                "Market",
                //"Lot Number Nomenclature",
                "Race" };
        final int TOTAL_COLS = hdrLabels.length;
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("External Units");
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
        dataWhite.setFont(dataFont); dataWhite.setAlignment(HorizontalAlignment.CENTER);
        dataWhite.setBorderTop(BorderStyle.THIN);    dataWhite.setTopBorderColor(black);
        dataWhite.setBorderBottom(BorderStyle.THIN); dataWhite.setBottomBorderColor(black);
        dataWhite.setBorderLeft(BorderStyle.THIN);   dataWhite.setLeftBorderColor(black);
        dataWhite.setBorderRight(BorderStyle.THIN);  dataWhite.setRightBorderColor(black);

        XSSFCellStyle dataAlt = (XSSFCellStyle) workbook.createCellStyle();
        dataAlt.setFillForegroundColor(altRow); dataAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataAlt.setFont(dataFont); dataAlt.setAlignment(HorizontalAlignment.CENTER);
        dataAlt.setBorderTop(BorderStyle.THIN);    dataAlt.setTopBorderColor(black);
        dataAlt.setBorderBottom(BorderStyle.THIN); dataAlt.setBottomBorderColor(black);
        dataAlt.setBorderLeft(BorderStyle.THIN);   dataAlt.setLeftBorderColor(black);
        dataAlt.setBorderRight(BorderStyle.THIN);  dataAlt.setRightBorderColor(black);

        // ── Row 0: Title ──────────────────────────────────────────────────────
        Row titleRow = sheet.createRow(0); titleRow.setHeightInPoints(36);
        titleRow.createCell(0).setCellValue("Department of Sericulture, Government of Karnataka");
        titleRow.getCell(0).setCellStyle(titleStyle);
        for (int c = 1; c < TOTAL_COLS; c++) titleRow.createCell(c).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, TOTAL_COLS - 1));

        // ── Row 1: Report name ────────────────────────────────────────────────
        Row reportRow = sheet.createRow(1); reportRow.setHeightInPoints(28);
        reportRow.createCell(0).setCellValue("EXTERNAL UNIT REGISTRATION REPORT");
        reportRow.getCell(0).setCellStyle(subStyle);
        for (int c = 1; c < TOTAL_COLS; c++) reportRow.createCell(c).setCellStyle(subStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLS - 1));

        // ── Row 2: Generated On ───────────────────────────────────────────────
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
        int serialNo = 1;
        for (ExternalUnitRegistrationDTO dto : units) {
            Row row = sheet.createRow(rowIdx);
            XSSFCellStyle rowStyle = (rowIdx % 2 == 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(serialNo++),
                dto.getName()                   != null ? dto.getName()                   : "",
                dto.getAddress()                != null ? dto.getAddress()                : "",
                dto.getLicenseNumber()          != null ? dto.getLicenseNumber()          : "",
                dto.getExternalUnitNumber()     != null ? dto.getExternalUnitNumber()     : "",
                dto.getOrganisationName()       != null ? dto.getOrganisationName()       : "",
                dto.getCapacity()               != null ? dto.getCapacity()               : "",
                dto.getExternalUnitTypeName()   != null ? dto.getExternalUnitTypeName()   : "",
                //dto.getBankAccountNumber()      != null ? dto.getBankAccountNumber()      : "",
                //dto.getBankIfscCode()           != null ? dto.getBankIfscCode()           : "",
                //dto.getBankBranchName()         != null ? dto.getBankBranchName()         : "",
                dto.getMarketMasterName()       != null ? dto.getMarketMasterName()       : "",
                //dto.getLotNumberNomenclature()  != null ? dto.getLotNumberNomenclature()  : "",
                dto.getRaceMasterName()         != null ? dto.getRaceMasterName()         : ""
            };
            for (int c = 0; c < values.length; c++) {
                Cell dataCell = row.createCell(c);
                dataCell.setCellValue(values[c]);
                dataCell.setCellStyle(rowStyle);
            }
            rowIdx++;
        }

        sheet.createFreezePane(0, 4);
        for (int c = 0; c < TOTAL_COLS; c++) {
            sheet.setColumnWidth(c, 20 * 256);
        }

        // Save to file
        String userHome = System.getProperty("user.home");
        Path directory = Paths.get(userHome, "Downloads");
        Files.createDirectories(directory);
        Path filePath = directory.resolve("external_unit_report_" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();
        workbook.dispose();

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
            if (Boolean.TRUE.equals(externalUnitRegistration.getLock())) {
                externalUnitRegistrationResponse.setError(true);
                externalUnitRegistrationResponse.setError_description("Account is locked. Delete not allowed.");
                return externalUnitRegistrationResponse;
            }
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
            // ===== ADD THIS =====

            List<EuVirtualBankAccount> vbList =
                    euVirtualBankAccountRepository.findByEuIdAndActiveTrue(
                            externalUnitRegistration.getExternalUnitRegistrationId()
                    );

            List<ExternalUnitRegistrationDetailsRequest> vbResponse = new ArrayList<>();

            for (EuVirtualBankAccount vb : vbList) {

                ExternalUnitRegistrationDetailsRequest item =
                        new ExternalUnitRegistrationDetailsRequest();
                item.setId(vb.getId());   // ✅ VERY IMPORTANT
                item.setVirtualAccountNumber(vb.getVirtualAccountNumber());
                item.setBranchName(vb.getBranchName());
                item.setIfscCode(vb.getIfscCode());
                item.setMarketMasterId(vb.getMarketMasterId());
                item.setLock(vb.getLock());
                MarketMaster market =
                        marketMasterRepository.findByMarketMasterIdAndActive(
                                vb.getMarketMasterId(), true
                        );

                if (market != null) {
                    item.setMarketMasterName(market.getMarketMasterName());
                }

                vbResponse.add(item);
            }

            externalUnitRegistrationResponse.setExternalUnitRegistrationDetailsRequests(vbResponse);

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
                    // ✅ MAP FIRST
                    externalUnitRegistrationResponse =
                            mapper.externalUnitRegistrationDTOToObject(
                                    externalUnitRegistrationDTO,
                                    ExternalUnitRegistrationResponse.class
                            );

                    // ✅ FETCH VB ONLY IF NOT NULL
                    List<EuVirtualBankAccount> vbList =
                            euVirtualBankAccountRepository.findByEuId(
                                    externalUnitRegistrationDTO.getExternalUnitRegistrationId()
                            );

                    List<ExternalUnitRegistrationDetailsRequest> vbResponse = new ArrayList<>();

                    for (EuVirtualBankAccount vb : vbList) {
                        ExternalUnitRegistrationDetailsRequest item = new ExternalUnitRegistrationDetailsRequest();

                        item.setVirtualAccountNumber(vb.getVirtualAccountNumber());
                        item.setBranchName(vb.getBranchName());
                        item.setIfscCode(vb.getIfscCode());
                        item.setMarketMasterId(vb.getMarketMasterId());
                        item.setLock(vb.getLock());

                        vbResponse.add(item);
                    }

                    externalUnitRegistrationResponse.setExternalUnitRegistrationDetailsRequests(vbResponse);

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
            if (Boolean.TRUE.equals(externalUnitRegistration.getLock())) {
                externalUnitRegistrationResponse.setError(true);
                externalUnitRegistrationResponse.setError_description("Account is locked , Editing not allowed.");
                return externalUnitRegistrationResponse;
            }
            externalUnitRegistration.setAddress(externalUnitRegistrationRequest.getAddress());
            externalUnitRegistration.setName(externalUnitRegistrationRequest.getName());
            externalUnitRegistration.setLicenseNumber(externalUnitRegistrationRequest.getLicenseNumber());
            externalUnitRegistration.setExternalUnitNumber(externalUnitRegistrationRequest.getExternalUnitNumber());
//            externalUnitRegistration.setExternalUnitTypeId(externalUnitRegistrationRequest.getExternalUnitTypeId());
            if (externalUnitRegistrationRequest.getExternalUnitTypeId() != null) {
                externalUnitRegistration.setExternalUnitTypeId(
                        externalUnitRegistrationRequest.getExternalUnitTypeId()
                );
            }
            externalUnitRegistration.setOrganisationName(externalUnitRegistrationRequest.getOrganisationName());
            externalUnitRegistration.setRaceMasterId(externalUnitRegistrationRequest.getRaceMasterId());
            externalUnitRegistration.setUserMasterId(Util.getUserId(Util.getTokenValues()));
            externalUnitRegistration.setCapacity(externalUnitRegistrationRequest.getCapacity());
//            externalUnitRegistration.setVirtualAccountNumber(externalUnitRegistrationRequest.getVirtualAccountNumber());
//            externalUnitRegistration.setBranchName(externalUnitRegistrationRequest.getBranchName());
//            externalUnitRegistration.setIfscCode(externalUnitRegistrationRequest.getIfscCode());
            externalUnitRegistration.setMarketMasterId(externalUnitRegistrationRequest.getMarketMasterId());
            externalUnitRegistration.setLotNumberNomenclature(externalUnitRegistrationRequest.getLotNumberNomenclature());
            externalUnitRegistration.setDistrictId(externalUnitRegistrationRequest.getDistrictId());
            externalUnitRegistration.setTalukId(externalUnitRegistrationRequest.getTalukId());
            externalUnitRegistration.setTscMasterId(externalUnitRegistrationRequest.getTscMasterId());
            externalUnitRegistration.setNameKan(externalUnitRegistrationRequest.getNameKan());
            externalUnitRegistration.setBankName(externalUnitRegistrationRequest.getBankName());
            externalUnitRegistration.setBankAccountNumber(externalUnitRegistrationRequest.getBankAccountNumber());
            externalUnitRegistration.setBankBranchName(externalUnitRegistrationRequest.getBankBranchName());
            externalUnitRegistration.setBankIfscCode(externalUnitRegistrationRequest.getBankIfscCode());
            externalUnitRegistration.setActive(true);
            ExternalUnitRegistration externalUnitRegistration1 = externalUnitRegistrationRepository.save(externalUnitRegistration);

            if (externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests() != null
                    && !externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests().isEmpty()) {

                // STEP 1: VALIDATE FIRST
                for (ExternalUnitRegistrationDetailsRequest details :
                        externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()) {
                    if (details.getVirtualAccountNumber() == null ||
                            details.getVirtualAccountNumber().trim().isEmpty()) {
                        continue;
                    }
                    List<EuVirtualBankAccount> existing =
                            euVirtualBankAccountRepository
                                    .findByVirtualAccountNumberAndEuIdNot(
                                            details.getVirtualAccountNumber(),
                                            externalUnitRegistration.getExternalUnitRegistrationId()
                                    );
//                    if (!existing.isEmpty()) {
//                        log.warn("Virtual Account already exists: {}", details.getVirtualAccountNumber());
//                        continue; // don't fail
//                    }

                }
            }

// STEP 2: DELETE
            List<EuVirtualBankAccount> existingList =
                    euVirtualBankAccountRepository.findByEuIdAndActiveTrue(
                            externalUnitRegistration.getExternalUnitRegistrationId()
                    );

            for (EuVirtualBankAccount existing : existingList) {

                Optional<ExternalUnitRegistrationDetailsRequest> match =
                        externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()
                                .stream()
                                .filter(req ->
                                                req.getId() != null &&
                                                        req.getId().equals(existing.getId())
                                        )
                                .findFirst();

                if (match.isPresent() && Boolean.TRUE.equals(match.get().getDeleted())) {

                    if (Boolean.TRUE.equals(existing.getLock())) {
                        throw new RuntimeException("Locked account cannot be deleted");
                    }

                    existing.setActive(false); // ✅ SOFT DELETE
                    euVirtualBankAccountRepository.save(existing);
                }

            }

// STEP 3: INSERT
            if (externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests() != null
                    && !externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests().isEmpty()) {
                for (ExternalUnitRegistrationDetailsRequest details :
                        externalUnitRegistrationRequest.getExternalUnitRegistrationDetailsRequests()) {
                    if (details.getVirtualAccountNumber() == null ||
                            details.getVirtualAccountNumber().trim().isEmpty()) {
                        continue;
                    }
                    if (Boolean.TRUE.equals(details.getDeleted())) {
                        continue;
                    }
                    Optional<EuVirtualBankAccount> existing =
                            existingList.stream()
                                    .filter(e -> details.getId() != null &&
                                            e.getId().equals(details.getId()))
                                    .findFirst();

// 🔥 FIX: COMMON DUPLICATE CHECK (MUST BE HERE)
                    List<EuVirtualBankAccount> duplicate =
                            euVirtualBankAccountRepository
                                    .findByVirtualAccountNumber(details.getVirtualAccountNumber());

                    boolean existsDuplicate = duplicate.stream()
                            .anyMatch(e ->
                                    details.getId() == null ||   // NEW record
                                            !e.getId().equals(details.getId()) // DIFFERENT record
                            );

                    if (existsDuplicate) {
                        externalUnitRegistrationResponse.setError(true);
                        externalUnitRegistrationResponse.setError_description("Virtual Account Number already exists");
                        return externalUnitRegistrationResponse;
                    }

                    if (existing.isPresent()) {

                        // ✅ UPDATE
                        EuVirtualBankAccount e = existing.get();

                        if (Boolean.TRUE.equals(e.getLock())) {
                            continue;
                        }

                        e.setVirtualAccountNumber(details.getVirtualAccountNumber());
                        e.setBranchName(details.getBranchName());
                        e.setIfscCode(details.getIfscCode());
                        e.setMarketMasterId(details.getMarketMasterId());

                        euVirtualBankAccountRepository.save(e);

                    } else {

                        // ✅ INSERT
                        EuVirtualBankAccount e = new EuVirtualBankAccount();

                        e.setEuId(externalUnitRegistration.getExternalUnitRegistrationId());
                        e.setVirtualAccountNumber(details.getVirtualAccountNumber());
                        e.setBranchName(details.getBranchName());
                        e.setIfscCode(details.getIfscCode());
                        e.setMarketMasterId(details.getMarketMasterId());
                        e.setLock(details.getLock() != null ? details.getLock() : false);
                        e.setActive(true);

                        euVirtualBankAccountRepository.save(e);
                    }
                    }


            }
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
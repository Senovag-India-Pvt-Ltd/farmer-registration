package com.sericulture.registration.service;

import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.api.traderLicense.*;
import com.sericulture.registration.model.api.village.PrimaryTraderLicenseDetailsResponse;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.checkerframework.checker.units.qual.A;
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

    public ResponseEntity<?> traderLicenseList(Long districtId,
                                               Long traderTypeMasterId,
                                               String silkType,
                                               int pageNumber, int pageSize) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(List.class);
        List<PrimaryTraderLicenseDetailsResponse> traderLicenseResponseList = new ArrayList<>();

        // convert 0 → null for optional filters
        districtId = (districtId != null && districtId == 0) ? null : districtId;
        traderTypeMasterId = (traderTypeMasterId != null && traderTypeMasterId == 0) ? null : traderTypeMasterId;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // ✅ Corrected repository call with isActive
        Page<TraderLicenseDTO> applicablePage = traderLicenseRepository.getByActiveAndFilters(
                true, districtId, silkType, traderTypeMasterId, pageable);

        // ✅ Correct type
        List<TraderLicenseDTO> applicableList = applicablePage.getContent();
        long totalRecords = applicablePage.getTotalElements();

        // mapping
        traderLicenseResponses(traderLicenseResponseList, applicableList, pageNumber, pageSize);

        rw.setTotalRecords(totalRecords);
        rw.setContent(traderLicenseResponseList);
        return ResponseEntity.ok(rw);
    }



    private static void traderLicenseResponses(List<PrimaryTraderLicenseDetailsResponse> traderLicenseResponseList,
                                               List<TraderLicenseDTO> applicableList,
                                               int pageNumber, int pageSize) {
        int serialNumber = pageNumber * pageSize + 1;
        for (TraderLicenseDTO dto : applicableList) {
            PrimaryTraderLicenseDetailsResponse response = PrimaryTraderLicenseDetailsResponse.builder()
                    .serialNumber(serialNumber++)
                    .traderLicenseId(dto.getTraderLicenseId())
                    .arnNumber(dto.getArnNumber())
                    .traderTypeMasterId(dto.getTraderTypeMasterId())
                    .firstName(dto.getFirstName())
                    .middleName(dto.getMiddleName())
                    .lastName(dto.getLastName())
                    .fatherName(dto.getFatherName())
                    .stateId(dto.getStateId())
                    .districtId(dto.getDistrictId())
                    .districtName(dto.getDistrictName())
                    .address(dto.getAddress())
                    .premisesDescription(dto.getPremisesDescription())
                    .applicationDate(dto.getApplicationDate())
                    .applicationNumber(dto.getApplicationNumber())
                    .traderLicenseNumber(dto.getTraderLicenseNumber())
                    .representativeDetails(dto.getRepresentativeDetails())
                    .licenseFee(dto.getLicenseFee())
                    .silkType(dto.getSilkType())
                    .licenseChallanNumber(dto.getLicenseChallanNumber())
                    .godownDetails(dto.getGodownDetails())
                    .silkExchangeMahajar(dto.getSilkExchangeMahajar())
                    .licenseNumberSequence(dto.getLicenseNumberSequence())
                    .traderTypeMasterName(dto.getTraderTypeMasterName())
                    .stateName(dto.getStateName())
                    .marketMasterName(dto.getMarketMasterName())
                    .marketMasterId(dto.getMarketMasterId())
                    .walletAmount(dto.getWalletAmount())
                    .mobileNumber(dto.getMobileNumber())
                    .virtualAccountNumber(dto.getVirtualAccountNumber())
                    .ifscCode(dto.getIfscCode())
                    .branchName(dto.getBranchName())
                    .build();
            traderLicenseResponseList.add(response);
        }
    }


    public FileInputStream traderLicenseReport(
            boolean isActive,
            Long districtId,
            String silkType,
            Long traderTypeMasterId,
            int pageNumber,
            int pageSize) throws Exception {

        // Convert 0 or "" to null
        districtId = (districtId != null && districtId == 0) ? null : districtId;
        traderTypeMasterId = (traderTypeMasterId != null && traderTypeMasterId == 0) ? null : traderTypeMasterId;
        silkType = (silkType != null && silkType.isEmpty()) ? null : silkType;

        // ✅ fetch ALL records (no paging)
        Pageable pageable = null;
        Page<TraderLicenseDTO> applicablePage =
                traderLicenseRepository.getByActiveAndFilters(isActive, districtId, silkType, traderTypeMasterId, pageable);

        List<TraderLicenseDTO> licenses = applicablePage.getContent();

        // ── Styled Excel (SXSSFWorkbook) ─────────────────────────────────────
        String[] hdrLabels = { "S.No", "ARN Number", "Trader Type", "First Name",
                //"Middle Name",
                //"Last Name",
                "Father Name", "District",
                //"State",
                "Market", "Silk Type", "Mobile Number"
                //"Wallet Amount", "Virtual Account", "IFSC Code", "Branch Name"
        };
        final int TOTAL_COLS = hdrLabels.length;
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        workbook.setCompressTempFiles(true);
        Sheet sheet = workbook.createSheet("Trader Licenses");
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
        reportRow.createCell(0).setCellValue("TRADER LICENSE REPORT");
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
        for (TraderLicenseDTO dto : licenses) {
            Row row = sheet.createRow(rowIdx);
            XSSFCellStyle rowStyle = (rowIdx % 2 == 0) ? dataAlt : dataWhite;
            String[] values = {
                String.valueOf(serialNo++),
                safeStr(dto.getArnNumber()),
                safeStr(dto.getTraderTypeMasterName()),
                safeStr(dto.getFirstName()),
                //safeStr(dto.getMiddleName()),
                //safeStr(dto.getLastName()),
                safeStr(dto.getFatherName()),
                safeStr(dto.getDistrictName()),
                //safeStr(dto.getStateName()),
                safeStr(dto.getMarketMasterName()),
                safeStr(dto.getSilkType()),
                safeStr(dto.getMobileNumber())
//                dto.getWalletAmount() != null ? String.valueOf(dto.getWalletAmount()) : "",
//                safeStr(dto.getVirtualAccountNumber()),
//                safeStr(dto.getIfscCode()),
//                safeStr(dto.getBranchName())
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
        Path filePath = directory.resolve("trader_license_report_" + Util.getISTLocalDate() + ".xlsx");

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
        }
        workbook.close();
        workbook.dispose();

        return new FileInputStream(filePath.toString());
    }

    private String safeStr(Object val) {
        return val == null ? "" : val.toString().trim();
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
            traderLicense.setGstNumber(traderLicenseRequest.getGstNumber());

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

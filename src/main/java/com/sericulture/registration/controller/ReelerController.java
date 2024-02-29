package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchByColumnRequest;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.FarmerResponse;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmer.GetFarmerResponse;
import com.sericulture.registration.model.api.reeler.*;
import com.sericulture.registration.service.ReelerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/reeler")
public class ReelerController {

    @Autowired
    ReelerService reelerService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response.put("validationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Operation(summary = "Insert Reeler Details", description = "Creates Reeler Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Reeler name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addReelerDetails(@Valid @RequestBody ReelerRequest reelerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.insertReelerDetails(reelerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Reeler Details", description = "Creates Reeler Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Reeler name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/transfer-reeler-license")
    public ResponseEntity<?> transferReelerLicense(@Valid @RequestBody ReelerRequest reelerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.insertTransferReelerDetails(reelerRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":1,\"reeler\":[{\"id\":1,\"reelerName\":\"\",\"relationShipId\":\"1\",\"farmerId\":\"1\"},{\"id\":2,\"reelerName\":\"ReelerName 1\",\"farmerId\":\"2\", \"relatioshipId\":\"2\"}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    public ResponseEntity<?> getPaginatedList(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "5") final Integer size
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerService.getPaginatedReelerDetails(PageRequest.of(pageNumber, size)));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\n" +
                                            "  \"content\": {\n" +
                                            "    \"reeler\": [\n" +
                                            "      {\n" +
                                            "        \"reelerId\": 2,\n" +
                                            "        \"name\": \"Test222222222222\",\n" +
                                            "        \"wardNumber\": \"Test\",\n" +
                                            "        \"passbookNumber\": \"Test\",\n" +
                                            "        \"fatherName\": \"Test\",\n" +
                                            "        \"educationId\": 1,\n" +
                                            "        \"reelingUnitBoundary\": \"Test\",\n" +
                                            "        \"dob\": \"2023-11-08T06:07:09.237+00:00\",\n" +
                                            "        \"rationCard\": \"Test\",\n" +
                                            "        \"machineTypeId\": 1,\n" +
                                            "        \"gender\": 1,\n" +
                                            "        \"dateOfMachineInstallation\": \"2023-11-08T06:07:09.237+00:00\",\n" +
                                            "        \"electricityRrNumber\": \"Test\",\n" +
                                            "        \"casteId\": 1,\n" +
                                            "        \"revenueDocument\": \"Test\",\n" +
                                            "        \"numberOfBasins\": 1,\n" +
                                            "        \"mobileNumber\": \"675687787\",\n" +
                                            "        \"recipientId\": \"675687787\",\n" +
                                            "        \"mahajarDetails\": \"Test\",\n" +
                                            "        \"emailId\": \"Test\",\n" +
                                            "        \"representativeNameAddress\": null,\n" +
                                            "        \"loanDetails\": \"Test\",\n" +
                                            "        \"assignToInspectId\": 1,\n" +
                                            "        \"gpsLat\": \"123.454\",\n" +
                                            "        \"gpsLng\": \"34.565\",\n" +
                                            "        \"inspectionDate\": \"2023-11-08T06:07:09.237+00:00\",\n" +
                                            "        \"arnNumber\": \"34\",\n" +
                                            "        \"chakbandiLat\": \"123.454\",\n" +
                                            "        \"chakbandiLng\": \"34.565\",\n" +
                                            "        \"address\": \"Test\",\n" +
                                            "        \"pincode\": \"324735\",\n" +
                                            "        \"stateId\": 1,\n" +
                                            "        \"districtId\": 1,\n" +
                                            "        \"talukId\": 3,\n" +
                                            "        \"hobliId\": 3,\n" +
                                            "        \"villageId\": 3,\n" +
                                            "        \"licenseReceiptNumber\": \"34534\",\n" +
                                            "        \"licenseExpiryDate\": \"2023-11-08T06:07:09.237+00:00\",\n" +
                                            "        \"receiptDate\": \"2023-11-08T06:07:09.237+00:00\",\n" +
                                            "        \"functionOfUnit\": 1,\n" +
                                            "        \"reelingLicenseNumber\": \"34534\",\n" +
                                            "        \"feeAmount\": 15.67,\n" +
                                            "        \"memberLoanDetails\": \"34534\",\n" +
                                            "        \"mahajarEast\": \"34534\",\n" +
                                            "        \"mahajarWest\": \"34534\",\n" +
                                            "        \"mahajarNorth\": \"34534\",\n" +
                                            "        \"mahajarSouth\": \"34534\",\n" +
                                            "        \"mahajarNorthEast\": \"34534\",\n" +
                                            "        \"mahajarNorthWest\": \"34534\",\n" +
                                            "        \"mahajarSouthEast\": \"34534\",\n" +
                                            "        \"mahajarSouthWest\": \"34534\",\n" +
                                            "        \"bankName\": \"SBI\",\n" +
                                            "        \"bankAccountNumber\": \"34575464534\",\n" +
                                            "        \"branchName\": \"Bengaluru\",\n" +
                                            "        \"ifscCode\": \"SBI3748\"\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"reelerId\": 3,\n" +
                                            "        \"name\": \"Test 1\",\n" +
                                            "        \"wardNumber\": \"Test\",\n" +
                                            "        \"passbookNumber\": \"Test\",\n" +
                                            "        \"fatherName\": \"Test\",\n" +
                                            "        \"educationId\": 1,\n" +
                                            "        \"reelingUnitBoundary\": \"Test\",\n" +
                                            "        \"dob\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"rationCard\": \"Test\",\n" +
                                            "        \"machineTypeId\": 1,\n" +
                                            "        \"gender\": 1,\n" +
                                            "        \"dateOfMachineInstallation\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"electricityRrNumber\": \"Test\",\n" +
                                            "        \"casteId\": 1,\n" +
                                            "        \"revenueDocument\": \"Test\",\n" +
                                            "        \"numberOfBasins\": 1,\n" +
                                            "        \"mobileNumber\": \"675687787\",\n" +
                                            "        \"recipientId\": \"675687787\",\n" +
                                            "        \"mahajarDetails\": \"Test\",\n" +
                                            "        \"emailId\": \"Test\",\n" +
                                            "        \"representativeNameAddress\": null,\n" +
                                            "        \"loanDetails\": \"Test\",\n" +
                                            "        \"assignToInspectId\": 1,\n" +
                                            "        \"gpsLat\": \"123.454\",\n" +
                                            "        \"gpsLng\": \"34.565\",\n" +
                                            "        \"inspectionDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"arnNumber\": \"34\",\n" +
                                            "        \"chakbandiLat\": \"123.454\",\n" +
                                            "        \"chakbandiLng\": \"34.565\",\n" +
                                            "        \"address\": \"Test\",\n" +
                                            "        \"pincode\": \"324735\",\n" +
                                            "        \"stateId\": 1,\n" +
                                            "        \"districtId\": 1,\n" +
                                            "        \"talukId\": 3,\n" +
                                            "        \"hobliId\": 3,\n" +
                                            "        \"villageId\": 3,\n" +
                                            "        \"licenseReceiptNumber\": \"34534\",\n" +
                                            "        \"licenseExpiryDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"receiptDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"functionOfUnit\": 1,\n" +
                                            "        \"reelingLicenseNumber\": \"34534\",\n" +
                                            "        \"feeAmount\": 15.67,\n" +
                                            "        \"memberLoanDetails\": \"34534\",\n" +
                                            "        \"mahajarEast\": \"34534\",\n" +
                                            "        \"mahajarWest\": \"34534\",\n" +
                                            "        \"mahajarNorth\": \"34534\",\n" +
                                            "        \"mahajarSouth\": \"34534\",\n" +
                                            "        \"mahajarNorthEast\": \"34534\",\n" +
                                            "        \"mahajarNorthWest\": \"34534\",\n" +
                                            "        \"mahajarSouthEast\": \"34534\",\n" +
                                            "        \"mahajarSouthWest\": \"34534\",\n" +
                                            "        \"bankName\": \"SBI\",\n" +
                                            "        \"bankAccountNumber\": \"34575464534\",\n" +
                                            "        \"branchName\": \"Bengaluru\",\n" +
                                            "        \"ifscCode\": \"SBI3748\"\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"reelerId\": 4,\n" +
                                            "        \"name\": \"Test 2\",\n" +
                                            "        \"wardNumber\": \"Test\",\n" +
                                            "        \"passbookNumber\": \"Test\",\n" +
                                            "        \"fatherName\": \"Test\",\n" +
                                            "        \"educationId\": 1,\n" +
                                            "        \"reelingUnitBoundary\": \"Test\",\n" +
                                            "        \"dob\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"rationCard\": \"Test\",\n" +
                                            "        \"machineTypeId\": 1,\n" +
                                            "        \"gender\": 1,\n" +
                                            "        \"dateOfMachineInstallation\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"electricityRrNumber\": \"Test\",\n" +
                                            "        \"casteId\": 1,\n" +
                                            "        \"revenueDocument\": \"Test\",\n" +
                                            "        \"numberOfBasins\": 1,\n" +
                                            "        \"mobileNumber\": \"675687787\",\n" +
                                            "        \"recipientId\": \"675687787\",\n" +
                                            "        \"mahajarDetails\": \"Test\",\n" +
                                            "        \"emailId\": \"Test\",\n" +
                                            "        \"representativeNameAddress\": null,\n" +
                                            "        \"loanDetails\": \"Test\",\n" +
                                            "        \"assignToInspectId\": 1,\n" +
                                            "        \"gpsLat\": \"123.454\",\n" +
                                            "        \"gpsLng\": \"34.565\",\n" +
                                            "        \"inspectionDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"arnNumber\": \"34\",\n" +
                                            "        \"chakbandiLat\": \"123.454\",\n" +
                                            "        \"chakbandiLng\": \"34.565\",\n" +
                                            "        \"address\": \"Test\",\n" +
                                            "        \"pincode\": \"324735\",\n" +
                                            "        \"stateId\": 1,\n" +
                                            "        \"districtId\": 1,\n" +
                                            "        \"talukId\": 3,\n" +
                                            "        \"hobliId\": 3,\n" +
                                            "        \"villageId\": 3,\n" +
                                            "        \"licenseReceiptNumber\": \"34534\",\n" +
                                            "        \"licenseExpiryDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"receiptDate\": \"2023-11-08T06:06:16.517+00:00\",\n" +
                                            "        \"functionOfUnit\": 1,\n" +
                                            "        \"reelingLicenseNumber\": \"34534\",\n" +
                                            "        \"feeAmount\": 15.67,\n" +
                                            "        \"memberLoanDetails\": \"34534\",\n" +
                                            "        \"mahajarEast\": \"34534\",\n" +
                                            "        \"mahajarWest\": \"34534\",\n" +
                                            "        \"mahajarNorth\": \"34534\",\n" +
                                            "        \"mahajarSouth\": \"34534\",\n" +
                                            "        \"mahajarNorthEast\": \"34534\",\n" +
                                            "        \"mahajarNorthWest\": \"34534\",\n" +
                                            "        \"mahajarSouthEast\": \"34534\",\n" +
                                            "        \"mahajarSouthWest\": \"34534\",\n" +
                                            "        \"bankName\": \"SBI\",\n" +
                                            "        \"bankAccountNumber\": \"34575464534\",\n" +
                                            "        \"branchName\": \"Bengaluru\",\n" +
                                            "        \"ifscCode\": \"SBI3748\"\n" +
                                            "      }\n" +
                                            "    ],\n" +
                                            "    \"totalItems\": 3,\n" +
                                            "    \"totalPages\": 1,\n" +
                                            "    \"currentPage\": 0\n" +
                                            "  },\n" +
                                            "  \"errorMessages\": []\n" +
                                            "}\n" +
                                            "Response headers"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReelerDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerService.deleteReelerDetails(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object saved details"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/edit")
    public ResponseEntity<?> editReelerDetails(
            @Valid @RequestBody final EditReelerRequest editReelerRequest
    ) {
        ResponseWrapper<ReelerResponse> rw = ResponseWrapper.createWrapper(ReelerResponse.class);
        rw.setContent(reelerService.updateReelerDetails(editReelerRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.getById(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/update-status")
    public ResponseEntity<?> updateReelerStatus(
            @RequestBody final UpdateReelerStatusRequest updateReelerStatusRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.updateReelerStatus(updateReelerStatusRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/update-reeler-license")
    public ResponseEntity<?> updateReelerLicense(
            @RequestBody final UpdateReelerLicenseRequest updateReelerLicenseRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.updateReelerLicense(updateReelerLicenseRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-by-reeling-license-number/{reelingLicenseNumber}")
    public ResponseEntity<?> getByReelingLicenseNumber(
            @PathVariable final String reelingLicenseNumber
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.getByReelingLicenseNumber(reelingLicenseNumber));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-reeler-details")
    public ResponseEntity<?> getReelerDetails(
            @RequestBody GetReelerRequest getReelerRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(reelerService.getReelerDetails(getReelerRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-reeler-details-by-fruits-id")
    public ResponseEntity<?> getReelerDetailsByFruitsId(
            @Valid @RequestBody GetFruitsIdRequest getFruitsIdRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(reelerService.getReelerDetailsByFruitsId(getFruitsIdRequest));
        return ResponseEntity.ok(rw);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-by-reeler-id-join/{id}")
    public ResponseEntity<?> getByFarmerIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.getByReelerIdJoin(id));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list-with-join")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"reeler\":[{\"id\":10,\"reelerName\":\"\",\"reelerId\":1,},{\"id\":11,\"reelerName\":\"Shimoga\",\"reelerId\":1,},{\"id\":13,\"reelerName\":\"Hubli\",\"reelerId\":1,}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    public ResponseEntity<?> getPaginatedListWithJoin(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "5") final Integer size
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerService.getPaginatedReelerDetailsWithJoin(PageRequest.of(pageNumber, size)));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/activate-reeler")
    public ResponseEntity<?> activateReeler(
            @RequestBody final ActivateReelerRequest activateReelerRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.activateReeler(activateReelerRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"reeler\":[{\"id\":10,\"reelerName\":\"\"},{\"id\":11,\"reelerName\":\"Byndoor\"},{\"id\":13,\"reelerName\":\"Uppunda\"}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    public ResponseEntity<?> getAllByActive(
            @RequestParam(defaultValue = "true") boolean isActive
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerService.getAllByActive(isActive));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/inactive-reelers")
    public ResponseEntity<?> inactiveReelers(

    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.inactiveReelers());
        return ResponseEntity.ok(rw);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object saved details"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/search")
    public ResponseEntity<?> search(
            @Valid @RequestBody final SearchWithSortRequest searchWithSortRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerService.searchByColumnAndSort(searchWithSortRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/search-reeler")
    public ResponseEntity<?> searchReeler(
            @Valid @RequestBody final SearchByColumnRequest searchByColumnRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.searchByColumn(searchByColumnRequest));
        return ResponseEntity.ok(rw);
    }
}

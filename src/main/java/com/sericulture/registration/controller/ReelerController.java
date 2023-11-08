package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.reeler.EditReelerRequest;
import com.sericulture.registration.model.api.reeler.ReelerRequest;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.api.reeler.UpdateReelerStatusRequest;
import com.sericulture.registration.service.ReelerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/reeler")
public class ReelerController {

    @Autowired
    ReelerService reelerService;

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
    public ResponseEntity<?> addReelerDetails(@RequestBody ReelerRequest reelerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.insertReelerDetails(reelerRequest));
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
        reelerService.deleteReelerDetails(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
            @RequestBody final EditReelerRequest editReelerRequest
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
//            @PathVariable final Integer id,
//            @PathVariable final Integer status
            @RequestBody final UpdateReelerStatusRequest updateReelerStatusRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerResponse.class);

        rw.setContent(reelerService.updateReelerStatus(updateReelerStatusRequest));
        return ResponseEntity.ok(rw);
    }
}

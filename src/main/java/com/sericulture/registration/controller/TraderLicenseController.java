package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.model.api.traderLicense.EditTraderLicenseRequest;
import com.sericulture.registration.model.api.traderLicense.GetTraderLicenseRequest;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseRequest;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseResponse;
import com.sericulture.registration.service.TraderLicenseService;
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
@RequestMapping("/v1/trader-license")
public class TraderLicenseController {

    @Autowired
    TraderLicenseService traderLicenseService;

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

    @Operation(summary = "Insert TraderLicense Details", description = "Creates TraderLicense Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"TraderLicense name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addTraderLicenseDetails(@Valid @RequestBody TraderLicenseRequest traderLicenseRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);

        rw.setContent(traderLicenseService.insertTraderLicenseDetails(traderLicenseRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\n" +
                                    "  \"content\": {\n" +
                                    "    \"traderLicense\": [\n" +
                                    "      {\n" +
                                    "        \"traderLicenseId\": 2,\n" +
                                    "        \"arnNumber\": \"167676\",\n" +
                                    "        \"traderTypeId\": 3,\n" +
                                    "        \"firstName\": \"test\",\n" +
                                    "        \"middleName\": \"test\",\n" +
                                    "        \"lastName\": \"test\",\n" +
                                    "        \"fatherName\": \"Test\",\n" +
                                    "        \"districtId\": 1,\n" +
                                    "        \"stateId\": 3,\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"premisesDescription\": \"Test\",\n" +
                                    "        \"applicationDate\": \"2023-11-09T12:59:58.303+00:00\",\n" +
                                    "        \"applicationNumber\": \"4561\",\n" +
                                    "        \"traderLicenseNumber\": \"34541\",\n" +
                                    "        \"representativeDetails\": \"Test\",\n" +
                                    "        \"licenseFee\": 14.56,\n" +
                                    "        \"licenseChallanNumber\": \"Test\",\n" +
                                    "        \"godownDetails\": \"Test\",\n" +
                                    "        \"silkExchangeMahajar\": \"Test\",\n" +
                                    "        \"licenseNumberSequence\": 1\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"traderLicenseId\": 3,\n" +
                                    "        \"arnNumber\": \"333333333\",\n" +
                                    "        \"traderTypeId\": 3,\n" +
                                    "        \"firstName\": \"test\",\n" +
                                    "        \"middleName\": \"test\",\n" +
                                    "        \"lastName\": \"test\",\n" +
                                    "        \"fatherName\": \"Test\",\n" +
                                    "        \"districtId\": 1,\n" +
                                    "        \"stateId\": 3,\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"premisesDescription\": \"Test\",\n" +
                                    "        \"applicationDate\": \"2023-11-09T13:00:57.120+00:00\",\n" +
                                    "        \"applicationNumber\": \"4561\",\n" +
                                    "        \"traderLicenseNumber\": \"Test\",\n" +
                                    "        \"representativeDetails\": \"Test\",\n" +
                                    "        \"licenseFee\": 14.56,\n" +
                                    "        \"licenseChallanNumber\": \"Test\",\n" +
                                    "        \"godownDetails\": \"Test\",\n" +
                                    "        \"silkExchangeMahajar\": \"Test\",\n" +
                                    "        \"licenseNumberSequence\": 1\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"traderLicenseId\": 4,\n" +
                                    "        \"arnNumber\": \"1676763534354\",\n" +
                                    "        \"traderTypeId\": 3,\n" +
                                    "        \"firstName\": \"test\",\n" +
                                    "        \"middleName\": \"test\",\n" +
                                    "        \"lastName\": \"test\",\n" +
                                    "        \"fatherName\": \"Test\",\n" +
                                    "        \"districtId\": 1,\n" +
                                    "        \"stateId\": 3,\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"premisesDescription\": \"Test\",\n" +
                                    "        \"applicationDate\": \"2023-11-09T12:59:58.303+00:00\",\n" +
                                    "        \"applicationNumber\": \"4561\",\n" +
                                    "        \"traderLicenseNumber\": \"34541\",\n" +
                                    "        \"representativeDetails\": \"Test\",\n" +
                                    "        \"licenseFee\": 14.56,\n" +
                                    "        \"licenseChallanNumber\": \"Test\",\n" +
                                    "        \"godownDetails\": \"Test\",\n" +
                                    "        \"silkExchangeMahajar\": \"Test\",\n" +
                                    "        \"licenseNumberSequence\": 1\n" +
                                    "      }\n" +
                                    "    ],\n" +
                                    "    \"totalItems\": 3,\n" +
                                    "    \"totalPages\": 1,\n" +
                                    "    \"currentPage\": 0\n" +
                                    "  },\n" +
                                    "  \"errorMessages\": []\n" +
                                    "}"))
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
        rw.setContent(traderLicenseService.getPaginatedTraderLicenseDetails(PageRequest.of(pageNumber, size)));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTraderLicenseDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(traderLicenseService.deleteTraderLicenseDetails(id));
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
    public ResponseEntity<?> editTraderLicenseDetails(
           @Valid @RequestBody final EditTraderLicenseRequest editTraderLicenseRequest
    ) {
        ResponseWrapper<TraderLicenseResponse> rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);
        rw.setContent(traderLicenseService.updateTraderLicenseDetails(editTraderLicenseRequest));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);

        rw.setContent(traderLicenseService.getById(id));
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
    @GetMapping("/get-join/{id}")
    public ResponseEntity<?> getByIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);

        rw.setContent(traderLicenseService.getByIdJoin(id));
        return ResponseEntity.ok(rw);
    }
    @GetMapping("/list-with-join")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"traderLicense\":[{\"id\":10,\"traderLicenseId\":\"\",\"traderLicenseId\":1,},{\"id\":11,\"traderLicenseId\":\"Shimoga\",\"traderLicenseId\":1,},{\"id\":13,\"traderLicenseId\":\"Hubli\",\"traderLicenseId\":1,}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
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
        rw.setContent(traderLicenseService.getPaginatedTraderLicenseDetailsWithJoin(PageRequest.of(pageNumber, size)));
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
        rw.setContent(traderLicenseService.searchByColumnAndSort(searchWithSortRequest));
        return ResponseEntity.ok(rw);
    }
    @GetMapping("/get-by-trader-license-number/{traderLicenseNumber}")
    public ResponseEntity<?> getByTraderLicenseNumber(
            @PathVariable final String traderLicenseNumber
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);

        rw.setContent(traderLicenseService.getByTraderLicenseNumber(traderLicenseNumber));
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
    @PostMapping("/get-trader-details-by-trader-number-or-mobile-number")
    public ResponseEntity<?> getTraderDetailsByMobileOrReelerNumber(
            @Valid @RequestBody GetTraderLicenseRequest getTraderLicenseRequest
    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);
        rw.setContent(traderLicenseService.getTraderDetailsByMobileOrReelerNumber(getTraderLicenseRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/get-traders-by-market-id/{marketId}")
    public ResponseEntity<?> getTradersByMarketId(
            @PathVariable final Long marketId
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(TraderLicenseResponse.class);

        rw.setContent(traderLicenseService.getTradersByMarketId(marketId));
        return ResponseEntity.ok(rw);
    }

}
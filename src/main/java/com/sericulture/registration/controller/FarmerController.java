package com.sericulture.registration.controller;

import com.sericulture.registration.helper.Util;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.ApplicationsDetailsDistrictWiseRequest;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.service.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/farmer")
public class FarmerController {

    @Autowired
    FarmerService farmerService;

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

    @Operation(summary = "Insert Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addFarmerDetails(@Valid @RequestBody FarmerRequest farmerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.insertFarmerDetails(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/save-complete-farmer-details")
    public ResponseEntity<?> saveCompleteFarmerDetails(@Valid @RequestBody FarmerSaveRequest farmerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.insertCompleteFarmerDetails(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\n" +
                                    "  \"content\": {\n" +
                                    "    \"totalItems\": 2,\n" +
                                    "    \"totalPages\": 1,\n" +
                                    "    \"farmer\": [\n" +
                                    "      {\n" +
                                    "        \"farmerId\": 1,\n" +
                                    "        \"farmerNumber\": \"12345fggv\",\n" +
                                    "        \"fruitsId\": \"1\",\n" +
                                    "        \"firstName\": \"First name\",\n" +
                                    "        \"middleName\": \"Middle name\",\n" +
                                    "        \"lastName\": \"Last name\",\n" +
                                    "        \"dob\": \"2023-11-07T11:29:33.397+00:00\",\n" +
                                    "        \"genderId\": 1,\n" +
                                    "        \"casteId\": 1,\n" +
                                    "        \"differentlyAbled\": false,\n" +
                                    "        \"email\": \"example@xyz.com\",\n" +
                                    "        \"mobileNumber\": \"9876543210\",\n" +
                                    "        \"aadhaarNumber\": \"111122223333\",\n" +
                                    "        \"epicNumber\": \"1\",\n" +
                                    "        \"rationCardNumber\": \"12345\",\n" +
                                    "        \"totalLandHolding\": \"100\",\n" +
                                    "        \"passbookNumber\": \"1001001000100\",\n" +
                                    "        \"landHoldingCategoryId\": 1,\n" +
                                    "        \"educationId\": 1,\n" +
                                    "        \"representativeId\": 1,\n" +
                                    "        \"khazaneRecipientId\": \"1\",\n" +
                                    "        \"photoPath\": \"/example.jpg\"\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"farmerId\": 2,\n" +
                                    "        \"farmerNumber\": \"123457\",\n" +
                                    "        \"fruitsId\": \"1\",\n" +
                                    "        \"firstName\": \"First name\",\n" +
                                    "        \"middleName\": \"Middle name\",\n" +
                                    "        \"lastName\": \"Last name\",\n" +
                                    "        \"dob\": \"2023-11-07T11:58:15.537+00:00\",\n" +
                                    "        \"genderId\": 1,\n" +
                                    "        \"casteId\": 1,\n" +
                                    "        \"differentlyAbled\": false,\n" +
                                    "        \"email\": \"example@xyz.com\",\n" +
                                    "        \"mobileNumber\": \"9876543210\",\n" +
                                    "        \"aadhaarNumber\": \"111122223333\",\n" +
                                    "        \"epicNumber\": \"1\",\n" +
                                    "        \"rationCardNumber\": \"12345\",\n" +
                                    "        \"totalLandHolding\": \"100\",\n" +
                                    "        \"passbookNumber\": \"1001001000100\",\n" +
                                    "        \"landHoldingCategoryId\": 1,\n" +
                                    "        \"educationId\": 1,\n" +
                                    "        \"representativeId\": 1,\n" +
                                    "        \"khazaneRecipientId\": \"1\",\n" +
                                    "        \"photoPath\": \"/example.jpg\"\n" +
                                    "      }\n" +
                                    "    ],\n" +
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
        rw.setContent(farmerService.getPaginatedFarmerDetails(PageRequest.of(pageNumber, size)));
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
    public ResponseEntity<?> deleteFarmerDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ExternalUnitRegistrationResponse.class);

        rw.setContent(farmerService.deleteFarmerDetails(id));
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
    public ResponseEntity<?> editFarmerDetails(
            @Valid @RequestBody final EditFarmerRequest editFarmerRequest
    ) {
        ResponseWrapper<FarmerResponse> rw = ResponseWrapper.createWrapper(FarmerResponse.class);
        rw.setContent(farmerService.updateFarmerDetails(editFarmerRequest));
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
    @PostMapping("/edit-complete-farmer-details")
    public ResponseEntity<?> editCompleteFarmerDetails(
            @Valid @RequestBody final EditCompleteFarmerRequest editFarmerRequest
    ) {
        ResponseWrapper<FarmerResponse> rw = ResponseWrapper.createWrapper(FarmerResponse.class);
        rw.setContent(farmerService.editCompleteFarmerDetails(editFarmerRequest));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.getById(id));
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
    @PostMapping("/get-farmer-details")
    public ResponseEntity<?> getFarmerDetails(
           @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.getFarmerDetails(getFarmerRequest));
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
    @PostMapping("/get-farmer-details-by-fruits-id")
    public ResponseEntity<?> getFarmerDetailsByFruitsId(
            @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.getFarmerDetailsByFruitsId(getFarmerRequest));
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
    @PostMapping("/get-farmer-details-by-fruits-id-or-farmer-number-or-mobile-number")
    public ResponseEntity<?> getFarmerDetailsByFruitsIdOrFarmerNumber(
            @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.getFarmerDetailsByFruitsIdOrFarmerNumberOrMobileNumber(getFarmerRequest));
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

    @PostMapping("/get-details-by-fruits-id")
    public ResponseEntity<?> getDetailsByFruitsId(
            @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.getDetailsByFruitsId(getFarmerRequest));
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
    @PostMapping("/get-farmer-details-by-fruits-id-test")
    public ResponseEntity<?> getFarmerDetailsByFruitsIdTest(
           @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.getFarmerDetailsByFruitsIdTest(getFarmerRequest));
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
    @GetMapping("/test")
    public ResponseEntity<?> test(

    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(GetFarmerResponse.class);
        rw.setContent(farmerService.test());
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
    @GetMapping("/get-by-farmer-id-join/{id}")
    public ResponseEntity<?> getByFarmerIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.getByFarmerIdJoin(id));
        return ResponseEntity.ok(rw);
    }
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Ok Response"),
//            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
//                    content =
//                            {
//                                    @Content(mediaType = "application/json", schema =
//                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
//                            }),
//            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
//    })
//    @GetMapping("/get-join/{id}")
//    public ResponseEntity<?> getByIdJoin(
//            @PathVariable final Integer id
//    ) {
//        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);
//
//        rw.setContent(farmerService.getByIdJoin(id));
//        return ResponseEntity.ok(rw);
//    }
    @GetMapping("/list-with-join")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"farmer\":[{\"id\":10,\"farmerName\":\"\",\"farmerId\":1,},{\"id\":11,\"farmerName\":\"Shimoga\",\"farmerId\":1,},{\"id\":13,\"farmerName\":\"Hubli\",\"farmerId\":1,}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
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
        rw.setContent(farmerService.getPaginatedFarmerDetailsWithJoin(PageRequest.of(pageNumber, size)));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list-with-join-with-filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"farmer\":[{\"id\":10,\"farmerName\":\"\",\"farmerId\":1,},{\"id\":11,\"farmerName\":\"Shimoga\",\"farmerId\":1,},{\"id\":13,\"farmerName\":\"Hubli\",\"farmerId\":1,}],\"totalPages\":1,\"currentPage\":0},\"errorMessages\":[]}"))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    public ResponseEntity<?> getPaginatedListWithJoinWIthFilters(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "5") final Integer size,
            @RequestParam(defaultValue = "0") final Integer type,
            @RequestParam(defaultValue = "") final String searchText,
            @RequestParam(defaultValue = "0") final Integer joinColumnType
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(farmerService.getPaginatedFarmerDetailsWithJoinWithFilters(PageRequest.of(pageNumber, size), type, searchText, joinColumnType));
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
        rw.setContent(farmerService.searchByColumnAndSort(searchWithSortRequest));
        return ResponseEntity.ok(rw);
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<ResponseWrapper> upload(@RequestParam("multipartFile") MultipartFile multipartFile,
                                                  @RequestParam("farmerId") String farmerId) throws Exception {

        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerBankAccountResponse.class);

        rw.setContent(farmerService.updatePhotoPath(multipartFile, farmerId));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add-non-karnataka-farmer")
    public ResponseEntity<?> addNonKarnatakaFarmerDetails(@Valid @RequestBody NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.insertNonKarnatakaFarmers(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/edit-non-karnataka-farmer")
    public ResponseEntity<?> editNonKarnatakaFarmerDetails(@Valid @RequestBody EditNonKarnatakaFarmerRequest editNonKarnatakaFarmerRequest) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.updateNonKarnatakaFarmer(editNonKarnatakaFarmerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add-karnataka-farmer-without-fruits-id")
    public ResponseEntity<?> addKarnatakaFarmerWithoutFruitsIdDetails(@Valid @RequestBody NonKarnatakaFarmerRequest farmerRequest) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.insertKarnatakaFarmersWithoutFruitsId(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/configure-fruits-id-allowed-no-counter")
    public ResponseEntity<?> configureFruitsIdAllowedNoCounter(@Valid @RequestBody FruitsIdAllowedCounterRequest farmerRequest) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.configureFruitsIdAllowedNoCounter(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-inward-counter")
    public ResponseEntity<?> getInwardCounter() throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.getConfiguredInward());
        return ResponseEntity.ok(rw);
    }

    @Operation(summary = "Insert Non Karnataka Farmer Details", description = "Creates Farmer Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/update-farmer-without-fruits-id-counter")
    public ResponseEntity<?> updateFarmerWithoutFruitsIdCounter(@Valid @RequestBody UpdateFruitsIdAllowedCounter farmerRequest) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.updateFarmerWithoutFruitsIdCounter(farmerRequest));
        return ResponseEntity.ok(rw);
    }

    @PostMapping("/totalFarmerCount")
    public ResponseEntity<?> totalFarmerCount(  ){
        return farmerService.totalFarmerCount();

    }

    @PostMapping("/districtWiseFarmerCount")
    public ResponseEntity<?> districtWiseFarmerCount(  ){
        return farmerService.districtWiseFarmerCount();

    }

    @PostMapping("/talukWise")
    public ResponseEntity<?> talukWise(@RequestBody ApplicationsDetailsDistrictWiseRequest applicationsDetailsDistrictWiseRequest){
        return farmerService.talukWise(applicationsDetailsDistrictWiseRequest);

    }

//    @PostMapping("/primaryFarmerDetails")
//    public ResponseEntity<?> primaryFarmerDetails( @RequestParam(defaultValue = "0") Long districtId,
//                                                   @RequestParam(defaultValue = "0") Long talukId,
//                                                   @RequestParam(defaultValue = "0") Long villageId,
//                                                   @RequestParam(defaultValue = "0") Long tscMasterId,
//                                                   @RequestParam(defaultValue = "0") int pageNumber,
//                                                   @RequestParam(defaultValue = "50") int pageSize ){
//        return farmerService.primaryFarmerDetails(districtId,talukId,villageId,tscMasterId, pageNumber, pageSize);
//
//    }
@PostMapping("/primaryFarmerDetails")
public ResponseEntity<?> primaryFarmerDetails(
        @RequestParam(required = false) Long districtId,
        @RequestParam(required = false) Long talukId,
        @RequestParam(required = false) Long villageId,
        @RequestParam(required = false) Long tscMasterId,
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "50") int pageSize) {
    return farmerService.primaryFarmerDetails(districtId, talukId, villageId, tscMasterId, pageNumber, pageSize);
}
    @PostMapping("/farmer-report")
    public ResponseEntity<?> farmerReport(@RequestParam(required = false) Long districtId,
                                          @RequestParam(required = false) Long talukId,
                                          @RequestParam(required = false) Long villageId,
                                          @RequestParam(required = false) Long tscMasterId) {
        try {
            System.out.println("enter to farmer report");
            FileInputStream fileInputStream = farmerService.farmerReport(districtId, talukId, villageId, tscMasterId);

            InputStreamResource resource = new InputStreamResource(fileInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=farmer_report" + Util.getISTLocalDate() + ".csv");
            headers.setContentType(MediaType.parseMediaType("text/csv"));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<>(ex.getMessage().getBytes(StandardCharsets.UTF_8), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping("/farmer-report")
//    public ResponseEntity<?> farmerReport( @RequestParam(required = false) Long districtId,
//                                           @RequestParam(required = false) Long talukId,
//                                           @RequestParam(required = false) Long villageId,
//                                           @RequestParam(required = false) Long tscMasterId){
//        try {
//            System.out.println("enter to farmer report");
//            FileInputStream fileInputStream = farmerService.farmerReport(districtId, talukId, villageId, tscMasterId);
//
//            InputStreamResource resource = new InputStreamResource(fileInputStream);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.xlsx");
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +"farmer_report"+ Util.getISTLocalDate()+".xlsx")
//                    .contentType(MediaType.parseMediaType("application/csv"))
//                    .body(resource);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println(ex.getMessage());
//            HttpHeaders headers = new HttpHeaders();
//            return new ResponseEntity<>(ex.getMessage().getBytes(StandardCharsets.UTF_8), org.springframework.http.HttpStatus.OK);
//        }
//    }

}

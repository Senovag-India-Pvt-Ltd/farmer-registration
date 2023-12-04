package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.service.FarmerService;
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
@RequestMapping("/v1/farmer")
public class FarmerController {

    @Autowired
    FarmerService farmerService;

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
    public ResponseEntity<?> addFarmerDetails(@RequestBody FarmerRequest farmerRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerResponse.class);

        rw.setContent(farmerService.insertFarmerDetails(farmerRequest));
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
        farmerService.deleteFarmerDetails(id);
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
    public ResponseEntity<?> editFarmerDetails(
            @RequestBody final EditFarmerRequest editFarmerRequest
    ) {
        ResponseWrapper<FarmerResponse> rw = ResponseWrapper.createWrapper(FarmerResponse.class);
        rw.setContent(farmerService.updateFarmerDetails(editFarmerRequest));
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
           @RequestBody GetFarmerRequest getFarmerRequest
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
            @RequestBody GetFarmerRequest getFarmerRequest
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
    @PostMapping("/get-farmer-details-by-fruits-id-test")
    public ResponseEntity<?> getFarmerDetailsByFruitsIdTest(
            @RequestBody GetFarmerRequest getFarmerRequest
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


}

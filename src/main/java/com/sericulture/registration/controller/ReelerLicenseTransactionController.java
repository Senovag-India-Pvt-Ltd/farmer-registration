package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.reelerLicenseTransaction.EditReelerLicenseTransactionRequest;
import com.sericulture.registration.model.api.reelerLicenseTransaction.ReelerLicenseTransactionRequest;
import com.sericulture.registration.model.api.reelerLicenseTransaction.ReelerLicenseTransactionResponse;
import com.sericulture.registration.service.ReelerLicenseTransactionService;
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
@RequestMapping("/v1/reeler-license-transaction")
public class ReelerLicenseTransactionController {

    @Autowired
    ReelerLicenseTransactionService reelerLicenseTransactionService;

    @Operation(summary = "Insert ReelerLicenseTransaction Details", description = "Creates ReelerLicenseTransaction Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"ReelerLicenseTransaction name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addReelerLicenseTransactionDetails(@RequestBody ReelerLicenseTransactionRequest reelerLicenseTransactionRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerLicenseTransactionResponse.class);

        rw.setContent(reelerLicenseTransactionService.insertReelerLicenseTransactionDetails(reelerLicenseTransactionRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\n" +
                                    "  \"content\": {\n" +
                                    "    \"reelerLicenseTransaction\": [\n" +
                                    "      {\n" +
                                    "        \"reelerLicenseTransactionId\": 1,\n" +
                                    "        \"reelerId\": 3,\n" +
                                    "        \"feeAmount\": 13.56,\n" +
                                    "        \"renewedDate\": \"2023-11-08T10:47:35.203+00:00\",\n" +
                                    "        \"expirationDate\": \"2023-11-08T10:47:35.203+00:00\"\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"reelerLicenseTransactionId\": 2,\n" +
                                    "        \"reelerId\": 3,\n" +
                                    "        \"feeAmount\": 13.5634,\n" +
                                    "        \"renewedDate\": \"2023-11-08T10:47:35.203+00:00\",\n" +
                                    "        \"expirationDate\": \"2023-11-08T10:47:35.203+00:00\"\n" +
                                    "      }\n" +
                                    "    ],\n" +
                                    "    \"totalItems\": 2,\n" +
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
        rw.setContent(reelerLicenseTransactionService.getPaginatedReelerLicenseTransactionDetails(PageRequest.of(pageNumber, size)));
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
    public ResponseEntity<?> deleteReelerLicenseTransactionDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerLicenseTransactionService.deleteReelerLicenseTransactionDetails(id));
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
    public ResponseEntity<?> editReelerLicenseTransactionDetails(
            @RequestBody final EditReelerLicenseTransactionRequest editReelerLicenseTransactionRequest
    ) {
        ResponseWrapper<ReelerLicenseTransactionResponse> rw = ResponseWrapper.createWrapper(ReelerLicenseTransactionResponse.class);
        rw.setContent(reelerLicenseTransactionService.updateReelerLicenseTransactionDetails(editReelerLicenseTransactionRequest));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerLicenseTransactionResponse.class);

        rw.setContent(reelerLicenseTransactionService.getById(id));
        return ResponseEntity.ok(rw);
    }
}

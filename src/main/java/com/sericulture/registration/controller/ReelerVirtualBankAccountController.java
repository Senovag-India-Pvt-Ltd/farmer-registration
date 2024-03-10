package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.EditReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountResponse;
import com.sericulture.registration.service.ReelerVirtualBankAccountService;
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
@RequestMapping("/v1/reeler-virtual-bank-account")
public class ReelerVirtualBankAccountController {

    @Autowired
    ReelerVirtualBankAccountService reelerVirtualBankAccountService;

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

    @Operation(summary = "Insert ReelerVirtualBankAccount Details", description = "Creates ReelerVirtualBankAccount Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"ReelerVirtualBankAccount name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addReelerVirtualBankAccountDetails(@Valid @RequestBody ReelerVirtualBankAccountRequest reelerVirtualBankAccountRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.insertReelerVirtualBankAccountDetails(reelerVirtualBankAccountRequest));
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
                                    "    \"reelerVirtualBankAccount\": [\n" +
                                    "      {\n" +
                                    "        \"reelerVirtualBankAccountId\": 2,\n" +
                                    "        \"reelerId\": 3,\n" +
                                    "        \"virtualAccountNumber\": \"3654643675\",\n" +
                                    "        \"branchName\": \"Bengaluru\",\n" +
                                    "        \"ifscCode\": \"SBI00457\",\n" +
                                    "        \"marketMasterId\": 1\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"reelerVirtualBankAccountId\": 4,\n" +
                                    "        \"reelerId\": 3,\n" +
                                    "        \"virtualAccountNumber\": \"36546436744589636574655\",\n" +
                                    "        \"branchName\": \"Bengaluru\",\n" +
                                    "        \"ifscCode\": \"SBI00457\",\n" +
                                    "        \"marketMasterId\": 1\n" +
                                    "      }\n" +
                                    "    ],\n" +
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
        rw.setContent(reelerVirtualBankAccountService.getPaginatedReelerVirtualBankAccountDetails(PageRequest.of(pageNumber, size)));
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
    public ResponseEntity<?> deleteReelerVirtualBankAccountDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(reelerVirtualBankAccountService.deleteReelerVirtualBankAccountDetails(id));
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
    public ResponseEntity<?> editReelerVirtualBankAccountDetails(
            @Valid @RequestBody final EditReelerVirtualBankAccountRequest editReelerVirtualBankAccountRequest
    ) {
        ResponseWrapper<ReelerVirtualBankAccountResponse> rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);
        rw.setContent(reelerVirtualBankAccountService.updateReelerVirtualBankAccountDetails(editReelerVirtualBankAccountRequest));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.getById(id));
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
    @GetMapping("/get-reelers-by-market-id/{marketId}")
    public ResponseEntity<?> getReelersByMarketId(
            @PathVariable final Long marketId
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.getReelersByMarketId(marketId));
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
    @GetMapping("/get-by-reeler-id/{id}")
    public ResponseEntity<?> getByReelerId(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.getByReelerId(id));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.getByIdJoin(id));
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
    public ResponseEntity<?> getByReelerIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(ReelerVirtualBankAccountResponse.class);

        rw.setContent(reelerVirtualBankAccountService.getByReelerIdJoin(id));
        return ResponseEntity.ok(rw);
    }


}

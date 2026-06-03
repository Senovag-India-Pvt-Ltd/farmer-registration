package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmer.GetFarmerRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.EditFarmerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.FarmerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.farmerVirtualBankAccount.FarmerVirtualBankAccountResponse;
import com.sericulture.registration.service.FarmerVirtualBankAccountService;
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
@RequestMapping("/v1/farmer-virtual-bank-account")
public class FarmerVirtualBankAccountController {

    @Autowired
    FarmerVirtualBankAccountService farmerVirtualBankAccountService;

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

    @Operation(summary = "Insert FarmerVirtualBankAccount Details", description = "Creates FarmerVirtualBankAccount Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"FarmerVirtualBankAccount name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addFarmerVirtualBankAccountDetails(@Valid @RequestBody FarmerVirtualBankAccountRequest farmerVirtualBankAccountRequest) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.insertFarmerVirtualBankAccountDetails(farmerVirtualBankAccountRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
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
        rw.setContent(farmerVirtualBankAccountService.getPaginatedFarmerVirtualBankAccountDetails(PageRequest.of(pageNumber, size)));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFarmerVirtualBankAccountDetails(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(Map.class);
        rw.setContent(farmerVirtualBankAccountService.deleteFarmerVirtualBankAccountDetails(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Object saved details"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/edit")
    public ResponseEntity<?> editFarmerVirtualBankAccountDetails(
            @Valid @RequestBody final EditFarmerVirtualBankAccountRequest editFarmerVirtualBankAccountRequest
    ) {
        ResponseWrapper<FarmerVirtualBankAccountResponse> rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.updateFarmerVirtualBankAccountDetails(editFarmerVirtualBankAccountRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getById(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-farmers-by-market-id/{marketId}")
    public ResponseEntity<?> getFarmersByMarketId(
            @PathVariable final Long marketId
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getFarmersByMarketId(marketId));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-farmer-details-by-farmer-number-or-mobile-number")
    public ResponseEntity<?> getFarmerDetailsByFarmerNumberOrMobileNumber(
            @Valid @RequestBody GetFarmerRequest getFarmerRequest
    ) throws Exception {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getFarmerDetailsByFarmerNumberOrMobileNumber(getFarmerRequest));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-by-farmer-id/{id}")
    public ResponseEntity<?> getByFarmerId(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getByFarmerId(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-join/{id}")
    public ResponseEntity<?> getByIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getByIdJoin(id));
        return ResponseEntity.ok(rw);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":null,\"errorMessages\":[{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Invalid Id\",\"label\":\"NON_LABEL_MESSAGE\",\"locale\":null}]}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/get-by-farmer-id-join/{id}")
    public ResponseEntity<?> getByFarmerIdJoin(
            @PathVariable final Integer id
    ) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(FarmerVirtualBankAccountResponse.class);
        rw.setContent(farmerVirtualBankAccountService.getByFarmerIdJoin(id));
        return ResponseEntity.ok(rw);
    }
}
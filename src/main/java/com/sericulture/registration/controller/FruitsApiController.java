package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.fruitsApi.GetCropSurveyResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsTokenResponse;
import com.sericulture.registration.model.dto.fruitsApi.CropSurveyDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsAadharDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsTokenDTO;
import com.sericulture.registration.service.FruitsApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("fuits-api")
@Slf4j
public class FruitsApiController {

    @Autowired
    FruitsApiService fruitsApiService;

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

    @Operation(summary = "Get Fruits Token", description = "Get Token From Fruits API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"error\":\"0\",\"error_description\":\" Username or password is incorrect \"}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-token")
    public GetFruitsTokenResponse getToken(@Valid @RequestBody FruitsTokenDTO body) {
        GetFruitsTokenResponse getFruitsTokenResponse = new GetFruitsTokenResponse();
        try {
            return fruitsApiService.getToken(body);
        } catch (Exception e) {
            log.error("FruitsApiController.getToken FAILED - error: {}", e.getMessage(), e);
            getFruitsTokenResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            getFruitsTokenResponse.setError_description(e.getMessage());
            return getFruitsTokenResponse;
        }
    }

    @Operation(summary = "Get Farmer Details By Fruit ID", description = "Get Farmer Details From Fruits API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"error\":\"0\",\"error_description\":\" Username or password is incorrect \"}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-farmer-by-fid")
    public ResponseEntity<?> getFarmerByFid(@Valid @RequestBody FruitsFarmerDTO body) {
        try {
            log.info("FruitsApiController.getFarmerByFid - request FarmerId: {}", body != null ? body.getFarmerId() : null);
            ResponseEntity<?> response = fruitsApiService.getFarmerByFruitsId(body);
            log.info("FruitsApiController.getFarmerByFid - response status: {}", response.getStatusCodeValue());
            return response;
        } catch (Exception e) {
            log.error("FruitsApiController.getFarmerByFid FAILED - FarmerId: {} | error: {}",
                    body != null ? body.getFarmerId() : null, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Farmer Details By Aadhaar Hash", description = "Get Farmer Details From Fruits API using the SHA-256 hash of the Aadhaar number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"error\":\"0\",\"error_description\":\" Username or password is incorrect \"}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-farmer-by-aadhar-hash")
    public ResponseEntity<?> getFarmerByAadharHash(@Valid @RequestBody FruitsAadharDTO body) {
        try {
            log.info("FruitsApiController.getFarmerByAadharHash - request received (aadhaar masked)");
            ResponseEntity<?> response = fruitsApiService.getFarmerByAadharHash(body);
            log.info("FruitsApiController.getFarmerByAadharHash - response status: {}", response.getStatusCodeValue());
            return response;
        } catch (Exception e) {
            log.error("FruitsApiController.getFarmerByAadharHash FAILED - error: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Crop Survey Data By Year", description = "Get Farmer Crop Survey Details From Fruits API by FarmerID, YearCode and SeasonCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"error\":\"0\",\"error_description\":\" Username or password is incorrect \"}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/get-crop-survey-data")
    public ResponseEntity<?> getCropSurveyData(@Valid @RequestBody CropSurveyDTO body) {
        try {
            log.info("FruitsApiController.getCropSurveyData - request FarmerId: {} | YearCode: {} | SeasonCode: {}",
                    body != null ? body.getFarmerId() : null,
                    body != null ? body.getYearCode() : null,
                    body != null ? body.getSeasonCode() : null);
            GetCropSurveyResponse response = fruitsApiService.getCropSurveyDataByYear(body);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("FruitsApiController.getCropSurveyData FAILED - FarmerId: {} | error: {}",
                    body != null ? body.getFarmerId() : null, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

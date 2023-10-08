package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.EducationRequest;
import com.sericulture.registration.model.api.EducationRequestByCode;
import com.sericulture.registration.model.api.EducationResponse;
import com.sericulture.registration.model.api.ErrorResponse;
import com.sericulture.registration.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/education")
public class EducationController {

    @Autowired
    EducationService educationService;
    @Operation(summary = "Get a Education details by code", description = "Returns a education details as per the code passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = EducationResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid Code supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping("/code")
    public ResponseEntity<?> getEducationDetails(@RequestBody EducationRequestByCode educationRequestByCode){
        EducationResponse educationDetails = educationService.getEducationDetails(educationRequestByCode.getCode());
        return ResponseEntity.status(HttpStatus.OK).body(educationDetails);
    }
    @Operation(summary = "Insert Education Details", description = "Creates Education Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully"),
            @ApiResponse(responseCode = "404", description = "Not Found - The product was not found")
    })
    @PostMapping("/create")
    public void getEducationDetails(@RequestBody EducationRequest educationRequest){
        educationService.insertEducationDetails(educationRequest);
    }
}

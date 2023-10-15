package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.EducationRequest;
import com.sericulture.registration.service.EducationService;
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

@RestController("/v1/education")
public class EducationController {

    @Autowired
    EducationService educationService;

    @Operation(summary = "Insert Education Details", description = "Creates Education Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> getEducationDetails(@RequestBody EducationRequest educationRequest){
        educationService.insertEducationDetails(educationRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"totalItems\":2,\"education\":[{\"id\":1,\"name\":\"Bachelor of Engineering\",\"code\":\"BE\"},{\"id\":2,\"name\":\"Bachelor of Arts\",\"code\":\"BA\"}],\"totalPages\":1,\"currentPage\":0}"))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    public ResponseEntity<?> getPaginatedEducationDetails(
            @RequestParam(defaultValue = "0") final Integer pageNumber,
            @RequestParam(defaultValue = "5") final Integer size
    ) {
        return ResponseEntity.ok(educationService.getPaginatedEducationDetails(PageRequest.of(pageNumber, size)));
    }

}

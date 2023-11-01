package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.education.EditEducationRequest;
import com.sericulture.registration.model.api.education.EducationRequest;
import com.sericulture.registration.model.api.education.EducationResponse;
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

import java.util.Map;

@RestController
@RequestMapping("education")
public class EducationController {

    @Autowired
    EducationService educationService;

    @Operation(summary = "Insert Education Details", description = "Creates Education Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> getEducationDetails(@RequestBody EducationRequest educationRequest) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(EducationResponse.class);

        rw.setContent(educationService.insertEducationDetails(educationRequest));
        return ResponseEntity.ok(rw);
    }


    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully", content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\"content\":{\"totalItems\":6,\"education\":[{\"id\":10,\"name\":\"\",\"code\":null},{\"id\":11,\"name\":\"SA\",\"code\":null},{\"id\":13,\"name\":\"SAC\",\"code\":null},{\"id\":14,\"name\":\"Bachelor of Engineeringsssssssssssssssss\",\"code\":null},{\"id\":15,\"name\":\"Bachelor of Engg\",\"code\":null}],\"totalPages\":2,\"currentPage\":0},\"errorMessages\":[]}"))
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
        rw.setContent(educationService.getPaginatedEducationDetails(PageRequest.of(pageNumber, size)));
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
    public ResponseEntity<?> deleteEducationDetails(
            @PathVariable final Integer id
    ) {
        educationService.deleteEducationDetails(id);
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
    public ResponseEntity<?> editEducationDetails(
            @RequestBody final EditEducationRequest educationRequest
    ) {
        ResponseWrapper<EducationResponse> rw = ResponseWrapper.createWrapper(EducationResponse.class);
        rw.setContent(educationService.updateEducationDetails(educationRequest));
        return ResponseEntity.ok(rw);
    }
}

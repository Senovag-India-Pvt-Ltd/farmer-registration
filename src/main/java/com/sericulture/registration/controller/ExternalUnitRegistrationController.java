package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.externalUnitRegistration.EditExternalUnitRegistrationRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationResponse;
import com.sericulture.registration.service.ExternalUnitRegistrationService;
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
@RequestMapping("/v1/external-unit-registration")
public class ExternalUnitRegistrationController {

    @Autowired
    ExternalUnitRegistrationService externalUnitRegistrationService;

    @Operation(summary = "Insert ExternalUnitRegistration Details", description = "Creates ExternalUnitRegistration Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"ExternalUnitRegistration name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addExternalUnitRegistrationDetails(@RequestBody ExternalUnitRegistrationRequest externalUnitRegistrationRequest){
        ResponseWrapper rw = ResponseWrapper.createWrapper(ExternalUnitRegistrationResponse.class);

        rw.setContent(externalUnitRegistrationService.insertExternalUnitRegistrationDetails(externalUnitRegistrationRequest));
        return ResponseEntity.ok(rw);
    }

    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content - inserted successfully",content =
                    {
                            @Content(mediaType = "application/json", schema =
                            @Schema(example = "{\n" +
                                    "  \"content\": {\n" +
                                    "    \"totalItems\": 3,\n" +
                                    "    \"totalPages\": 1,\n" +
                                    "    \"externalUnitRegistration\": [\n" +
                                    "      {\n" +
                                    "        \"externalUnitRegistrationId\": 1,\n" +
                                    "        \"externalUnitTypeId\": 1,\n" +
                                    "        \"name\": \"Test\",\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"licenseNumber\": \"435435\",\n" +
                                    "        \"externalUnitNumber\": \"S5346\"\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"externalUnitRegistrationId\": 2,\n" +
                                    "        \"externalUnitTypeId\": 1,\n" +
                                    "        \"name\": \"22222222\",\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"licenseNumber\": \"435435\",\n" +
                                    "        \"externalUnitNumber\": \"S5346\"\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"externalUnitRegistrationId\": 3,\n" +
                                    "        \"externalUnitTypeId\": 1,\n" +
                                    "        \"name\": \"Test 5445 56\",\n" +
                                    "        \"address\": \"Test\",\n" +
                                    "        \"licenseNumber\": \"435435\",\n" +
                                    "        \"externalUnitNumber\": \"S5346\"\n" +
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
        rw.setContent(externalUnitRegistrationService.getPaginatedExternalUnitRegistrationDetails(PageRequest.of(pageNumber, size)));
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
    public ResponseEntity<?> deleteExternalUnitRegistrationDetails(
            @PathVariable final Integer id
    ) {
        externalUnitRegistrationService.deleteExternalUnitRegistrationDetails(id);
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
    public ResponseEntity<?> editExternalUnitRegistrationDetails(
            @RequestBody final EditExternalUnitRegistrationRequest editExternalUnitRegistrationRequest
    ) {
        ResponseWrapper<ExternalUnitRegistrationResponse> rw = ResponseWrapper.createWrapper(ExternalUnitRegistrationResponse.class);
        rw.setContent(externalUnitRegistrationService.updateExternalUnitRegistrationDetails(editExternalUnitRegistrationRequest));
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
        ResponseWrapper rw = ResponseWrapper.createWrapper(ExternalUnitRegistrationResponse.class);

        rw.setContent(externalUnitRegistrationService.getById(id));
        return ResponseEntity.ok(rw);
    }
}

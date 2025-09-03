package com.sericulture.registration.controller;


import com.sericulture.registration.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;


    @Operation(summary = "Insert Farmer Dashboard Details", description = "Creates Farmer Dashboard Details in to DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok Response"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Has validation errors",
                    content =
                            {
                                    @Content(mediaType = "application/json", schema =
                                    @Schema(example = "{\"errorType\":\"VALIDATION\",\"message\":[{\"message\":\"Farmer Dashboard name should be more than 1 characters.\",\"label\":\"name\",\"locale\":null}]}"))
                            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error occurred while processing the request.")
    })
    @GetMapping("/farmerDetails")
    public ResponseEntity<?> getFarmerDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getFarmerDetails();
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "2", "message", "No records found"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "500", "error", e.getMessage()));
        }
    }

    @GetMapping("/reelerDetails")
    public ResponseEntity<?> getReelerDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getReelerDetails();
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "2", "message", "No records found"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "500", "error", e.getMessage()));
        }
    }

    @GetMapping("/traderLicenseDetails")
    public ResponseEntity<?> getTraderLicenseDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getTraderLicenseDetails();
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "2", "message", "No records found"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "500", "error", e.getMessage()));
        }
    }

    @GetMapping("/externalUnitDetails")
    public ResponseEntity<?> getExternalUnitDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getExternalUnitDetails();
            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "2", "message", "No records found"));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "500", "error", e.getMessage()));
        }
    }

}

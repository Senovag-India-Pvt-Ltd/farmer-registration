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

    @GetMapping("/chawkiManagementDetails")
    public ResponseEntity<?> chawkiManagementDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getChawkiManagementDetails();
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

    @GetMapping("/getHelpDeskDetails")
    public ResponseEntity<?> getHelpDeskDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getHelpDeskDetails();
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

    @GetMapping("/getTrainerDetails")
    public ResponseEntity<?> getTrainerDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getTrainerDetails();
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

    @GetMapping("/getTraineeDetails")
    public ResponseEntity<?> getTraineeDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getTraineeDetails();
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

    @GetMapping("/getDBTDetails")
    public ResponseEntity<?> getDBTDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getDBTDetails();
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

    @GetMapping("/getSeedMarketDetails")
    public ResponseEntity<?> getSeedMarketDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSeedMarketDetails();
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

    @GetMapping("/getMaintenanceAndSaleOfNurseryDetails")
    public ResponseEntity<?> getMaintenanceAndSaleOfNurseryDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getMaintenanceAndSaleOfNurseryDetails();
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

    @GetMapping("/getMaintenanceOfMulberryGardenDetails")
    public ResponseEntity<?> getMaintenanceOfMulberryGardenDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getMaintenanceOfMulberryGardenDetails();
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

    @GetMapping("/getSeedCuttingBankDetails")
    public ResponseEntity<?> getSeedCuttingBankDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSeedCuttingBankDetails();
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

    @GetMapping("/getSupplyOfCocoonsDetails")
    public ResponseEntity<?> getSupplyOfCocoonsDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSupplyOfCocoonsDetails();
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

    @GetMapping("/getChawkiDistributionDetails")
    public ResponseEntity<?> getChawkiDistributionDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getChawkiDistributionDetails();
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

    @GetMapping("/getCropInspectionDetails")
    public ResponseEntity<?> getCropInspectionDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getCropInspectionDetails();
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

    @GetMapping("/getFitnessCertificateDetails")
    public ResponseEntity<?> getFitnessCertificateDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getFitnessCertificateDetails();
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

    @GetMapping("/getFarmerMulberryExtensionDetails")
    public ResponseEntity<?> getFarmerMulberryExtensionDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getFarmerMulberryExtensionDetails();
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

    @GetMapping("/getSupplyOfDisinfectantDetails")
    public ResponseEntity<?> getSupplyOfDisinfectantDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSupplyOfDisinfectantDetails();
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

    @GetMapping("/getMulberryTargetDetails")
    public ResponseEntity<?> getMulberryTargetDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getMulberryTargetDetails();
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

    @GetMapping("/getProductionTargetDetails")
    public ResponseEntity<?> getProductionTargetDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getProductionTargetDetails();
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

    @GetMapping("/getSchemeTargetDetails")
    public ResponseEntity<?> getSchemeTargetDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSchemeTargetDetails();
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


    @GetMapping("/getTargetDetails")
    public ResponseEntity<?> getTargetDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getTargetDetails();
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

    @GetMapping("/getSeedAndDFLMulberryFarmDetails")
    public ResponseEntity<?> getSeedAndDFLMulberryFarmDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSeedAndDFLMulberryFarmDetails();
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

    @GetMapping("/getSeedAndDFLFarmWiseDetails")
    public ResponseEntity<?> getSeedAndDFLFarmWiseDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getSeedAndDFLFarmWiseDetails();
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

    @GetMapping("/getTSCWiseSoldDFLDetails")
    public ResponseEntity<?> getTSCWiseSoldDFLDetails() {
        try {
            List<Map<String, Object>> result = dashboardService.getTSCWiseSoldDFLDetails();
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

    @GetMapping("/getMarketDetails")
    public ResponseEntity<?> getMarketDetails() {

        try {

            List<Map<String, Object>> result =
                    dashboardService.getMarketDetails();

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

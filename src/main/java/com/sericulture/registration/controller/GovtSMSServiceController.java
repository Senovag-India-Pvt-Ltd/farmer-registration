package com.sericulture.registration.controller;

import com.sericulture.registration.model.api.fruitsApi.GetFruitsTokenResponse;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsTokenDTO;
import com.sericulture.registration.model.dto.govtSmsService.GovtSmsServiceDTO;
import com.sericulture.registration.service.FruitsApiService;
import com.sericulture.registration.service.GovtSMSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/govt-sms-service")
public class GovtSMSServiceController {

    @Autowired
    GovtSMSService govtSMSService;

    @Operation(summary = "Send single SMS", description = "Send single sms")
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
    @PostMapping("/send-single-sms")
    public ResponseEntity<?> sendSingleSMS(@RequestBody GovtSmsServiceDTO body) {
        try{
            String result =  govtSMSService.sendSingleSMS(body.getUsername(), body.getPassword(), body.getMessage(), body.getSenderId(), body.getMobileNumber(),body.getSecureKey(), body.getTemplateid());
            return new ResponseEntity<>("Sent single sms successfully" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Send bulk SMS", description = "Send bulk sms")
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
    @PostMapping("/send-bulk-sms")
    public ResponseEntity<?> sendBulkSMS(@RequestBody GovtSmsServiceDTO body) {
        try{
            String result =  govtSMSService.sendBulkSMS(body.getUsername(), body.getPassword(), body.getMessage(), body.getSenderId(), body.getMobileNumber(),body.getSecureKey(), body.getTemplateid());
            return new ResponseEntity<>("Sent bulk sms successfully" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Send unicode SMS", description = "Send unicode sms")
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
    @PostMapping("/send-unicode-sms")
    public ResponseEntity<?> sendUnicodeSMS(@RequestBody GovtSmsServiceDTO body) {
        try{
            String result =  govtSMSService.sendUnicodeSMS(body.getUsername(), body.getPassword(), body.getMessage(), body.getSenderId(), body.getMobileNumber(),body.getSecureKey(), body.getTemplateid());
            return new ResponseEntity<>("Sent unicode sms successfully" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Send otp SMS", description = "Send otp sms")
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
    @PostMapping("/send-otp-sms")
    public ResponseEntity<?> sendOtpSMS(@RequestBody GovtSmsServiceDTO body) {
        try{
            String result =  govtSMSService.sendOtpSMS(body.getUsername(), body.getPassword(), body.getMessage(), body.getSenderId(), body.getMobileNumber(),body.getSecureKey(), body.getTemplateid());
            return new ResponseEntity<>("Sent otp successfully" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Send unicode otp SMS", description = "Send single sms")
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
    @PostMapping("/send-unicode-otp-sms")
    public ResponseEntity<?> sendUnicodeOtpSMS(@RequestBody GovtSmsServiceDTO body) {
        try{
            String result =  govtSMSService.sendSingleSMS(body.getUsername(), body.getPassword(), body.getMessage(), body.getSenderId(), body.getMobileNumber(),body.getSecureKey(), body.getTemplateid());
            return new ResponseEntity<>("Sent unicode otp successfully" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

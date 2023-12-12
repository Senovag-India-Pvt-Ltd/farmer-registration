package com.sericulture.registration.controller;

import com.sericulture.registration.model.dto.otp.OtpDTO;
import com.sericulture.registration.service.OtpService;
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
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    OtpService otpService;

    @Operation(summary = "Store otp", description = "Send single sms")
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
    @PostMapping("/store-otp")
    public ResponseEntity<?> storeOtpService(@RequestBody OtpDTO body) {
        try{
            otpService.storeOtp(body.getUserId());
            return new ResponseEntity<>("Otp stored" , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "get otp", description = "Send single sms")
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
    @PostMapping("/get-otp")
    public ResponseEntity<?> getOtp(@RequestBody OtpDTO body) {
        try{
            String result =  otpService.getOtp(body.getUserId());
            System.out.println(result);
            return new ResponseEntity<>("Otp is " + result , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "get otp", description = "Send single sms")
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
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpDTO body) {
        try{
            Boolean result =  otpService.verifyOtp(body.getUserId(), body.getOtp());
            String verificationText;
            if(result){
                verificationText = "verified";
            }else{
                verificationText = "not verified";
            }
            System.out.println(result);
            return new ResponseEntity<>("Otp " + verificationText , HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

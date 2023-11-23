package com.sericulture.registration.controller;

import com.sericulture.registration.model.dto.FruitsTokenDTO;
import com.sericulture.registration.service.FruitsApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("fuitsapi")
public class FruitsApiController {

    @Autowired
    FruitsApiService fruitsApiService;

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
    @PostMapping("/gettoken")
    public ResponseEntity<?> getToken(@RequestBody FruitsTokenDTO body) {
        try{

            return fruitsApiService.getToken(body);

//            String uri = "https://fruits-services.karnataka.gov.in/FRUITS_Sericulture/Token";
//            logger.info("REQUEST BODY :"+ConvertToJson.setJsonString(body));
//
//            HttpRequest results =   HttpRequest.post(uri)
//                    .header("Content-Type", "application/json")
//                    .send(ConvertToJson.setJsonString(body)) ;
//
//            int status= results.code();
//            logger.info("STATUS CODE :"+status);
//
//            return new ResponseEntity<>( status == 200 ? "Airline created successfully" : "Airline Not created successfully",status == 200 ? HttpStatus.OK: HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsTokenResponse;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.dto.fruitsApi.FruitsTokenDTO;
import com.sericulture.registration.model.dto.fruitsApi.GetFruitsTokenDTO;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.utils.ObjectToUrlEncodedConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FruitsApiService {
    @Autowired
    Mapper mapper;

    public GetFruitsTokenResponse getToken(FruitsTokenDTO body) {
        GetFruitsTokenResponse getFruitsTokenResponse = new GetFruitsTokenResponse();
        try{
            String uri = "https://fruits-services.karnataka.gov.in/FRUITS_Sericulture/Token";
            log.info("FRUITS GET TOKEN REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<FruitsTokenDTO> request = new HttpEntity<>(body, headers);

            ObjectMapper mapper1 = new ObjectMapper();
            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<GetFruitsTokenResponse> result = restTemplate.postForEntity(uri, request, GetFruitsTokenResponse.class);

            return result.getBody();

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
            log.error("FRUITS API ERROR - GET TOKEN: " + e.getMessage());
            getFruitsTokenResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            getFruitsTokenResponse.setError_description(e.getMessage());
            return getFruitsTokenResponse;
            //return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

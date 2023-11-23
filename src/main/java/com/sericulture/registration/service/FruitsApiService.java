package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.model.dto.FruitsTokenDTO;
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

    public ResponseEntity<?> getToken(FruitsTokenDTO body) {
        try{
            String uri = "https://fruits-services.karnataka.gov.in/FRUITS_Sericulture/Token";
            // log.info("REQUEST BODY :" + ConvertToJson.setJsonString(body));
            log.info("FRUITS GET TOKEN REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FruitsTokenDTO> request = new HttpEntity<>(body, headers);

            // restTemplate.getMessageConverters().add(new org.springframework.http.converter.FormHttpMessageConverter.FormHttpMessageConverter());
            ObjectMapper mapper = new ObjectMapper();
            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));

            ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

            return new ResponseEntity<>( result.getStatusCodeValue() == 200 ? "Got Fruits Token Successfully" : "Getting Fruits Token Failed", HttpStatus.OK);


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
            log.error("FRUITS API ERROR - GET TOKEN: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.model.api.fruitsApi.GetCropSurveyResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsTokenResponse;
import com.sericulture.registration.model.dto.fruitsApi.CropSurveyDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsAadharDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsTokenDTO;
import com.sericulture.registration.model.dto.fruitsApi.GetCropSurveyRequest;
import com.sericulture.registration.model.dto.fruitsApi.GetFruitsAadharRequest;
import com.sericulture.registration.model.dto.fruitsApi.GetFruitsFarmerRequest;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.utils.AadharHashUtil;
import com.sericulture.registration.utils.ObjectToUrlEncodedConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FruitsApiService {

    // ===== Centralized FRUITS API configuration (ref: Forest_API_Service_Document) =====
    @Value("${fruits.api.base-url:https://fruits-services.karnataka.gov.in/FRUITS_Sericulture}")
    private String fruitsBaseUrl;

    @Value("${fruits.api.username:Sericulture}")
    private String fruitsUsername;

    @Value("${fruits.api.password:ZIy5S72oUvn4a1Tice9vSA==}")
    private String fruitsPassword;

    private static final String PATH_TOKEN = "/Token";
    private static final String PATH_GET_FARMER_BY_FID = "/GetFRUITSData/GetFarmerByFID";
    private static final String PATH_GET_FARMER_BY_AADHAR_HASH = "/GetFRUITSData/GetFarmerByAadharHash";
    private static final String PATH_GET_CROP_SURVEY = "/GetFRUITSData/GetCropSurveyDataByYear";

    @Autowired
    Mapper mapper;

    // ---------------------------------------------------------------------------------
    // Token
    // ---------------------------------------------------------------------------------
    public GetFruitsTokenResponse getToken(FruitsTokenDTO body) {
        GetFruitsTokenResponse getFruitsTokenResponse = new GetFruitsTokenResponse();
        String uri = fruitsBaseUrl + PATH_TOKEN;
        try {
            log.info("FruitsApiService.getToken - url: {} | username: {} | password: {}",
                    uri, body != null ? body.getUsername() : null, mask(body != null ? body.getPassword() : null));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<FruitsTokenDTO> request = new HttpEntity<>(body, headers);

            ObjectMapper mapper1 = new ObjectMapper();
            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<GetFruitsTokenResponse> result = restTemplate.postForEntity(uri, request, GetFruitsTokenResponse.class);
            log.info("FruitsApiService.getToken - response status: {}", result.getStatusCodeValue());
            return result.getBody();

        } catch (Exception e) {
            log.error("FruitsApiService.getToken FAILED - url: {} | username: {} | error: {}",
                    uri, body != null ? body.getUsername() : null, e.getMessage(), e);
            getFruitsTokenResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            getFruitsTokenResponse.setError_description(e.getMessage());
            return getFruitsTokenResponse;
        }
    }

    /**
     * Fetches a bearer token using the configured FRUITS service credentials.
     * Shared by all data calls to avoid duplicating the token-fetch logic.
     */
    private String fetchAccessToken() {
        FruitsTokenDTO fruitsTokenDTO = new FruitsTokenDTO(fruitsUsername, fruitsPassword, "password");
        GetFruitsTokenResponse tokenResponse = this.getToken(fruitsTokenDTO);
        return tokenResponse != null ? tokenResponse.getAccess_token() : null;
    }

    // ---------------------------------------------------------------------------------
    // GetFarmerByFID
    // ---------------------------------------------------------------------------------
    public ResponseEntity<?> getFarmerByFruitsId(FruitsFarmerDTO body) {
        String uri = fruitsBaseUrl + PATH_GET_FARMER_BY_FID;
        String farmerId = body != null ? body.getFarmerId() : null;
        String access_token = null;
        try {
            access_token = fetchAccessToken();

            // FRUITS GetFarmerByFID requires UserName + UserPassword in the JSON body along with FarmerId
            GetFruitsFarmerRequest fruitsRequest = new GetFruitsFarmerRequest(farmerId, fruitsUsername, fruitsPassword);

            log.info("FruitsApiService.getFarmerByFruitsId - url: {} | username: {} | password: {} | token: {} | FarmerId: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(access_token);

            HttpEntity<GetFruitsFarmerRequest> request = new HttpEntity<>(fruitsRequest, headers);

            ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
            log.info("FruitsApiService.getFarmerByFruitsId - response status: {} | body: {}",
                    result.getStatusCodeValue(), result.getBody());

            return new ResponseEntity<>(result.getStatusCodeValue() == 200 ? result.getBody() : "Getting Fruits Farmer Details Failed", HttpStatus.OK);

        } catch (Exception e) {
            log.error("FruitsApiService.getFarmerByFruitsId FAILED - url: {} | username: {} | password: {} | token: {} | FarmerId: {} | error: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId, e.getMessage(), e);
            return new ResponseEntity<>("Error!, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public GetFruitsResponse getFarmerByFruitsIdWithResponse(FruitsFarmerDTO body) {
        GetFruitsResponse getFruitsResponse = new GetFruitsResponse();
        String uri = fruitsBaseUrl + PATH_GET_FARMER_BY_FID;
        String farmerId = body != null ? body.getFarmerId() : null;
        String access_token = null;
        try {
            access_token = fetchAccessToken();

            GetFruitsFarmerRequest fruitsRequest = new GetFruitsFarmerRequest(farmerId, fruitsUsername, fruitsPassword);

            log.info("FruitsApiService.getFarmerByFruitsIdWithResponse - url: {} | username: {} | password: {} | token: {} | FarmerId: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(access_token);

            HttpEntity<GetFruitsFarmerRequest> request = new HttpEntity<>(fruitsRequest, headers);

            ResponseEntity<GetFruitsResponse> result = restTemplate.postForEntity(uri, request, GetFruitsResponse.class);
            log.info("FruitsApiService.getFarmerByFruitsIdWithResponse - response status: {} | body: {}",
                    result.getStatusCodeValue(), result.getBody());

            return result.getBody();

        } catch (Exception e) {
            log.error("FruitsApiService.getFarmerByFruitsIdWithResponse FAILED - url: {} | username: {} | password: {} | token: {} | FarmerId: {} | error: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId, e.getMessage(), e);
            getFruitsResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            getFruitsResponse.setError_description(e.getMessage());
            return getFruitsResponse;
        }
    }

    // ---------------------------------------------------------------------------------
    // GetFarmerByAadharHash
    // ---------------------------------------------------------------------------------
    public ResponseEntity<?> getFarmerByAadharHash(FruitsAadharDTO body) {
        String uri = fruitsBaseUrl + PATH_GET_FARMER_BY_AADHAR_HASH;
        String aadharNo = body != null ? body.getAadharNo() : null;
        String hashOfAadhaar = AadharHashUtil.hash(aadharNo);
        String access_token = null;
        try {
            access_token = fetchAccessToken();

            GetFruitsAadharRequest fruitsRequest = new GetFruitsAadharRequest(hashOfAadhaar, fruitsUsername, fruitsPassword);

            // Aadhaar number itself is sensitive - log only the (non-reversible) hash, never the raw number
            log.info("FruitsApiService.getFarmerByAadharHash - url: {} | username: {} | password: {} | token: {} | HashofAadhaar: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, hashOfAadhaar);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(access_token);

            HttpEntity<GetFruitsAadharRequest> request = new HttpEntity<>(fruitsRequest, headers);

            ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
            log.info("FruitsApiService.getFarmerByAadharHash - response status: {} | body: {}",
                    result.getStatusCodeValue(), result.getBody());

            return new ResponseEntity<>(result.getStatusCodeValue() == 200 ? result.getBody() : "Getting Fruits Farmer Details Failed", HttpStatus.OK);

        } catch (Exception e) {
            log.error("FruitsApiService.getFarmerByAadharHash FAILED - url: {} | username: {} | password: {} | token: {} | HashofAadhaar: {} | error: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, hashOfAadhaar, e.getMessage(), e);
            return new ResponseEntity<>("Error!, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ---------------------------------------------------------------------------------
    // GetCropSurveyDataByYear
    // ---------------------------------------------------------------------------------
    public GetCropSurveyResponse getCropSurveyDataByYear(CropSurveyDTO body) {
        GetCropSurveyResponse cropSurveyResponse = new GetCropSurveyResponse();
        String uri = fruitsBaseUrl + PATH_GET_CROP_SURVEY;
        String farmerId = body != null ? body.getFarmerId() : null;
        Integer yearCode = body != null ? body.getYearCode() : null;
        Integer seasonCode = body != null ? body.getSeasonCode() : null;
        String access_token = null;
        try {
            access_token = fetchAccessToken();

            GetCropSurveyRequest fruitsRequest = new GetCropSurveyRequest(farmerId, yearCode, seasonCode, fruitsUsername, fruitsPassword);

            log.info("FruitsApiService.getCropSurveyDataByYear - url: {} | username: {} | password: {} | token: {} | FarmerID: {} | YearCode: {} | SeasonCode: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId, yearCode, seasonCode);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(access_token);

            HttpEntity<GetCropSurveyRequest> request = new HttpEntity<>(fruitsRequest, headers);

            ResponseEntity<GetCropSurveyResponse> result = restTemplate.postForEntity(uri, request, GetCropSurveyResponse.class);
            log.info("FruitsApiService.getCropSurveyDataByYear - response status: {} | body: {}",
                    result.getStatusCodeValue(), result.getBody());

            return result.getBody();

        } catch (Exception e) {
            log.error("FruitsApiService.getCropSurveyDataByYear FAILED - url: {} | username: {} | password: {} | token: {} | FarmerID: {} | YearCode: {} | SeasonCode: {} | error: {}",
                    uri, fruitsUsername, mask(fruitsPassword), access_token, farmerId, yearCode, seasonCode, e.getMessage(), e);
            cropSurveyResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            cropSurveyResponse.setError_description(e.getMessage());
            return cropSurveyResponse;
        }
    }

    // ---------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------
    /** Masks a sensitive value for logging (the document does not require logging credentials). */
    private String mask(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return "****";
    }
}

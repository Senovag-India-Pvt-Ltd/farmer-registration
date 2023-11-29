package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetLandDetailsResponse;
import com.sericulture.registration.model.dto.caste.CasteDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.village.VillageDTO;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressResponse;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.model.exceptions.ValidationException;
import com.sericulture.registration.model.mapper.Mapper;
import com.sericulture.registration.repository.*;
import com.sericulture.registration.utils.ObjectToUrlEncodedConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerService {

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    FarmerAddressRepository farmerAddressRepository;

    @Autowired
    FarmerLandDetailsRepository farmerLandDetailsRepository;

    @Autowired
    FarmerFamilyRepository farmerFamilyRepository;

    @Autowired
    FarmerBankAccountRepository farmerBankAccountRepository;

    @Autowired
    FruitsApiService fruitsApiService;

    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Transactional
    public FarmerResponse insertFarmerDetails(FarmerRequest farmerRequest){
        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest,Farmer.class);
        validator.validate(farmer);
        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(!farmerList.isEmpty() && farmerList.stream().filter(Farmer::getActive).findAny().isPresent()){
            throw new ValidationException("Farmer number already exist");
        }
        if(!farmerList.isEmpty() && farmerList.stream().filter(Predicate.not(Farmer::getActive)).findAny().isPresent()){
            throw new ValidationException("Farmer number already exist with inactive farmer");
        }
        return mapper.farmerEntityToObject(farmerRepository.save(farmer),FarmerResponse.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> getPaginatedFarmerDetails(final Pageable pageable){
        return convertToMapResponse(farmerRepository.findByActiveOrderByFarmerIdAsc( true, pageable));
    }

    private Map<String, Object> convertToMapResponse(final Page<Farmer> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerEntityToObject(farmer,FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer",farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());

        return response;
    }

    @Transactional
    public void deleteFarmerDetails(long id) {
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id, true);
        if (Objects.nonNull(farmer)) {
            farmer.setActive(false);
            farmerRepository.save(farmer);
        } else {
            throw new ValidationException("Invalid Id");
        }
    }

    @Transactional
    public FarmerResponse getById(int id){
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id,true);
        if(farmer == null){
            throw new ValidationException("Invalid Id");
        }
        log.info("Entity is ",farmer);
        return mapper.farmerEntityToObject(farmer,FarmerResponse.class);
    }

    @Transactional
    public FarmerResponse updateFarmerDetails(EditFarmerRequest farmerRequest){
        /*List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(farmerList.size()>0){
            throw new ValidationException("farmer already exists with this name, duplicates are not allowed.");
        }
*/
        Farmer farmer = farmerRepository.findByFarmerIdAndActiveIn(farmerRequest.getFarmerId(), Set.of(true,false));
        if(Objects.nonNull(farmer)){
            farmer.setFarmerNumber(farmerRequest.getFarmerNumber());
            farmer.setFruitsId(farmerRequest.getFruitsId());
            farmer.setFirstName(farmerRequest.getFirstName());
            farmer.setMiddleName(farmerRequest.getMiddleName());
            farmer.setLastName(farmerRequest.getLastName());
            farmer.setDob(farmerRequest.getDob());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setGenderId(farmerRequest.getGenderId());
            farmer.setCasteId(farmerRequest.getCasteId());
            farmer.setDifferentlyAbled(farmerRequest.getDifferentlyAbled());
            farmer.setEmail(farmerRequest.getEmail());
            farmer.setMobileNumber(farmerRequest.getMobileNumber());
            farmer.setAadhaarNumber(farmerRequest.getAadhaarNumber());
            farmer.setEpicNumber(farmerRequest.getEpicNumber());
            farmer.setRationCardNumber(farmerRequest.getRationCardNumber());
            farmer.setTotalLandHolding(farmerRequest.getTotalLandHolding());
            farmer.setPassbookNumber(farmerRequest.getPassbookNumber());
            farmer.setLandHoldingCategoryId(farmerRequest.getLandHoldingCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setActive(true);
        }else{
            throw new ValidationException("Error occurred while fetching farmer");
        }
        return mapper.farmerEntityToObject(farmerRepository.save(farmer),FarmerResponse.class);
    }

    @Transactional
    public GetFarmerResponse getFarmerDetails(GetFarmerRequest getFarmerRequest){
        Farmer farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(),true);
        if(farmer == null){
            throw new ValidationException("Invalid fruits id");
        }
        List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerAddressDTO> farmerAddressDTOList = farmerAddressRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer,FarmerResponse.class));
        getFarmerResponse.setFarmerAddressList(farmerAddressList);
        getFarmerResponse.setFarmerAddressDTOList(farmerAddressDTOList);
        getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
        getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
        getFarmerResponse.setFarmerBankAccount(farmerBankAccount);

        return getFarmerResponse;
    }

    @Transactional
    public GetFarmerResponse getFarmerDetailsByFruitsId(GetFarmerRequest getFarmerRequest){
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(),true);
        if(farmer == null){
            FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
            fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

            GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
            Farmer farmer1 = new Farmer();
            farmer1.setFruitsId(getFruitsResponse.getFarmerID());
            farmer1.setFirstName(getFruitsResponse.getName());
            farmer1.setMiddleName(getFruitsResponse.getFatherName());


            log.info("getFruitsResponse: " + getFruitsResponse);
            log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
            log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
            log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );

            if(getFruitsResponse.getGender().equals("Male")){
                farmer1.setGenderId(1L);
            }else if(getFruitsResponse.getGender().equals("Female")){
                farmer1.setGenderId(2L);
            }else{
                farmer1.setGenderId(3L);
            }
            CasteDTO casteDTO = new CasteDTO();
            casteDTO.setCaste(getFruitsResponse.getCaste());
            ResponseWrapper responseWrapper = getCaste(casteDTO);

            farmer1.setCasteId(Long.valueOf(((LinkedHashMap) responseWrapper.getContent()).get("id").toString()));

            if(getFruitsResponse.getPhysicallyChallenged().equals("No")){
                farmer1.setDifferentlyAbled(false);
            }else{
                farmer1.setDifferentlyAbled(true);
            }
            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));

            List<FarmerAddress> farmerAddressList = new ArrayList<>();
            FarmerAddress farmerAddress = new FarmerAddress();
            farmerAddress.setAddressText(getFruitsResponse.getResidentialAddress());
            farmerAddress.setPincode(getFruitsResponse.getPincode());
            farmerAddressList.add(farmerAddress);
            getFarmerResponse.setFarmerAddressList(farmerAddressList);


            List<FarmerLandDetails> farmerLandDetailsList = new ArrayList<>();
            for(GetLandDetailsResponse getLandDetailsResponse: getFruitsResponse.getLanddata()){
                FarmerLandDetails farmerLandDetails = new FarmerLandDetails();
                VillageDTO villageDTO = new VillageDTO();
                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);
                farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));
                farmerLandDetailsList.add(farmerLandDetails);
            }
            getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(1);
        }else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(0);
        }

        return getFarmerResponse;
    }

    @Transactional
    public GetFarmerResponse getFarmerDetailsByFruitsIdTest(GetFarmerRequest getFarmerRequest){
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(),true);
        if(farmer == null){
            getFarmerResponse.setIsFruitService(1);
        }else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(0);
        }

        return getFarmerResponse;
    }

    public ResponseWrapper getCaste(CasteDTO body) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        try{
            String uri = "http://localhost:8001/master-data/v1/" + "caste/get-by-title";
            //String uri = "http://13.200.62.144:8001/master-data/v1/" + "caste/get-by-title";

            log.info("Caste REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper1 = new ObjectMapper();

            HttpEntity<String> request = new HttpEntity<>(mapper1.writeValueAsString(body), headers);

            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<ResponseWrapper> result = restTemplate.postForEntity(uri, request, ResponseWrapper.class);

            return result.getBody();

        }catch (Exception e){
            e.printStackTrace();
            log.error("CASTE ERROR: " + e.getMessage());
            return responseWrapper;
        }
    }

    public ResponseWrapper getVillageDetails(VillageDTO body) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        try{
            String uri = "http://localhost:8001/master-data/v1/" + "village/get-details-by-village-name";
            //String uri = "http://13.200.62.144:8001/master-data/v1/" + "village/get-details-by-village-name";
            log.info("Caste REQUEST BODY :" + body.toString());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper mapper1 = new ObjectMapper();

            HttpEntity<String> request = new HttpEntity<>(mapper1.writeValueAsString(body), headers);

            restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper1));

            ResponseEntity<ResponseWrapper> result = restTemplate.postForEntity(uri, request, ResponseWrapper.class);

            return result.getBody();

        }catch (Exception e){
            e.printStackTrace();
            log.error("VILLAGE ERROR: " + e.getMessage());
            return responseWrapper;
        }
    }

    @Transactional
    public GetFarmerResponse test() throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        String input = "{\n" +
                "    \"StatusCode\": 1,\n" +
                "    \"StatusText\": \"Success\",\n" +
                "    \"FarmerID\": \"FID2806000009439\",\n" +
                "    \"name\": \"MUNIHANUMAPPA\",\n" +
                "    \"NameKan\": \"MUNIHANUMAPPA\",\n" +
                "    \"FatherName\": \"Munivenkatappa\",\n" +
                "    \"FatherNameKan\": \"Munivenkatappa\",\n" +
                "    \"Gender\": \"Male\",\n" +
                "    \"GenderStatus\": \"Declared\",\n" +
                "    \"Caste\": \"SC\",\n" +
                "    \"CasteStatus\": \"Declared\",\n" +
                "    \"RDNumber\": \"\",\n" +
                "    \"PhysicallyChallenged\": \"No\",\n" +
                "    \"Minority\": \"No\",\n" +
                "    \"FarmerType\": \"Margin Farmer\",\n" +
                "    \"ResidentialAddress\": \"Kempadenahlli  Village ambajidurga Hobli Chintamani Taluk\",\n" +
                "    \"Pincode\": \"\",\n" +
                "    \"Landdata\": [\n" +
                "        {\n" +
                "            \"DistrictName\": \"Chikkaballapur\",\n" +
                "            \"TalukName\": \"Chinthamani\",\n" +
                "            \"HobliName\": \"Ambajidurga\",\n" +
                "            \"VillageName\": \"Kempadenahalli\",\n" +
                "            \"OwnerName\": \"ಮುನಿಹನುಮಪ್ಪ\", \n" +
                "            \"NameScore\": 100,\n" +
                "            \"Surveyno\": 37, \n" +
                "            \"Surnoc\": \"*\",\n" +
                "            \"Hissano\": \"*\",\n" +
                "            \"OwnerNo\": 16,\n" +
                "            \"MainOwnerNo\": 16,\n" +
                "            \"Acre\": 0,\n" +
                "            \"Gunta\": 26,\n" +
                "            \"Fgunta\": 10.67\n" +
                "        },\n" +
                "        {\n" +
                "            \"DistrictName\": \"Chikkaballapur\",\n" +
                "            \"TalukName\": \"Chinthamani\",\n" +
                "            \"HobliName\": \"Ambajidurga\",\n" +
                "            \"VillageName\": \"Kempadenahalli\",\n" +
                "            \"OwnerName\": \"ಮುನಿಹನುಮಪ್ಪ\",\n" +
                "            \"NameScore\": 100,\n" +
                "            \"Surveyno\": 37,\n" +
                "            \"Surnoc\": \"*\",\n" +
                "            \"Hissano\": \"*\",\n" +
                "            \"OwnerNo\": 42,\n" +
                "            \"MainOwnerNo\": 42,\n" +
                "            \"Acre\": 1,\n" +
                "            \"Gunta\": 0,\n" +
                "            \"Fgunta\": 0\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        GetFruitsResponse getFruitsResponse = objectMapper.readValue(input, GetFruitsResponse.class);

        String name = getFruitsResponse.getName();

        return getFarmerResponse;
    }
}

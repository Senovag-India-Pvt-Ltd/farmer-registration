package com.sericulture.registration.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.controller.S3Controller;
import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.common.SearchWithSortRequest;
import com.sericulture.registration.model.api.farmer.*;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountResponse;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyResponse;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.api.fruitsApi.GetLandDetailsResponse;
import com.sericulture.registration.model.api.reeler.ReelerResponse;
import com.sericulture.registration.model.dto.caste.CasteDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.farmer.FarmerFamilyDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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
    FarmerTypeRepository farmerTypeRepository;

    @Autowired
    CasteRepository casteRepository;

    @Autowired
    VillageRepository villageRepository;
    @Autowired
    Mapper mapper;

    @Autowired
    CustomValidator validator;

    @Autowired
    S3Controller s3Controller;

    @Transactional
    public FarmerResponse insertFarmerDetails(FarmerRequest farmerRequest){
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = mapper.farmerObjectToEntity(farmerRequest,Farmer.class);
        validator.validate(farmer);
        List<Farmer> farmerList = farmerRepository.findByFarmerNumber(farmerRequest.getFarmerNumber());
        if(!farmerList.isEmpty() && farmerList.stream().filter(Farmer::getActive).findAny().isPresent()){
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist");
        }
        else if(!farmerList.isEmpty() && farmerList.stream().filter(Predicate.not(Farmer::getActive)).findAny().isPresent()){
            //throw new ValidationException("Village name already exist with inactive state");
            farmerResponse.setError(true);
            farmerResponse.setError_description("Farmer number already exist with inactive state");
        }else{
            // Check for duplicate Reeler Number
            List<Farmer> farmerListByNumber = farmerRepository.findByMobileNumber(farmer.getMobileNumber());
            if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Farmer::getActive)) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("Farmer Mobile Number already exists");
            } else if (!farmerListByNumber.isEmpty() && farmerListByNumber.stream().anyMatch(Predicate.not(Farmer::getActive))) {
                farmerResponse.setError(true);
                farmerResponse.setError_description("Farmer Mobile Number already exists with inactive state");
            } else {
                // If no duplicates found, save the reeler
                farmerResponse = mapper.farmerEntityToObject(farmerRepository.save(farmer), FarmerResponse.class);
                farmerResponse.setError(false);
            }
        }
        return farmerResponse;
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
    public FarmerResponse deleteFarmerDetails(long id) {
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id, true);
        if (Objects.nonNull(farmer)) {
            farmer.setActive(false);
            farmerResponse = mapper.farmerEntityToObject(farmerRepository.save(farmer), FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid Id");
            // throw new ValidationException("Invalid Id");
        }
        return farmerResponse;
    }

    @Transactional
    public FarmerResponse getById(int id){
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(id,true);
        if(farmer == null){
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid id");
        }else{
            farmerResponse =  mapper.farmerEntityToObject(farmer,FarmerResponse.class);
            farmerResponse.setError(false);
        }
        log.info("Entity is ",farmer);
        return farmerResponse;
    }

    @Transactional
    public FarmerResponse updateFarmerDetails(EditFarmerRequest farmerRequest){
        FarmerResponse farmerResponse = new FarmerResponse();
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
            farmer.setLandCategoryId(farmerRequest.getLandCategoryId());
            farmer.setEducationId(farmerRequest.getEducationId());
            farmer.setRepresentativeId(farmerRequest.getRepresentativeId());
            farmer.setKhazaneRecipientId(farmerRequest.getKhazaneRecipientId());
            farmer.setPhotoPath(farmerRequest.getPhotoPath());
            farmer.setFarmerTypeId(farmerRequest.getFarmerTypeId());
            farmer.setMinority(farmerRequest.getMinority());
            farmer.setRdNumber(farmerRequest.getRdNumber());
            farmer.setCasteStatus(farmerRequest.getCasteStatus());
            farmer.setGenderStatus(farmerRequest.getGenderStatus());
            farmer.setFatherNameKan(farmerRequest.getFatherNameKan());
            farmer.setFatherName(farmerRequest.getFatherName());
            farmer.setNameKan(farmerRequest.getNameKan());

            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }

        return farmerResponse ;
    }

    @Transactional
    public GetFarmerResponse getFarmerDetails(GetFarmerRequest getFarmerRequest){
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = new Farmer();
        if(getFarmerRequest.getFarmerNumber() == null || getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        }else{
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        }
        if (farmer == null) {
            // Log a message, provide a default value, or take other appropriate action
//            System.out.println("Invalid fruits id");
//
//            // You might want to return an empty response or some default response
//            return new GetFarmerResponse();
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid fruits id");
        }
//        FarmerDTO farmerDTO = farmerRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
//        List<FarmerDTO> farmerDTOList = farmerRepository.getByIdAndActive(farmer.getFarmerId(), true);
        List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerAddressDTO> farmerAddressDTOList = farmerAddressRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerLandDetailsDTO> farmerLandDetailsDTOList = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
        List<FarmerFamilyDTO> farmerFamilyDTOList = farmerFamilyRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
        FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer,FarmerResponse.class));
//        getFarmerResponse.setFarmerDTO(farmerDTO);
//        getFarmerResponse.setFarmerDTOList(farmerDTOList);
        getFarmerResponse.setFarmerAddressList(farmerAddressList);
        getFarmerResponse.setFarmerAddressDTOList(farmerAddressDTOList);
        getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
        getFarmerResponse.setFarmerFamilyDTOList(farmerFamilyDTOList);
        getFarmerResponse.setFarmerLandDetailsList(farmerLandDetailsList);
        getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOList);
        getFarmerResponse.setFarmerBankAccount(farmerBankAccount);

        return getFarmerResponse;
    }

    @Transactional
    public GetFarmerResponse getFarmerDetailsByFruitsId(GetFarmerRequest getFarmerRequest) throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
        if(getFarmerRequest.getFarmerNumber() == null || getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        }else{
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        }
        if(farmer == null){
            FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
            fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

          //  GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
            String inputData = String.valueOf(fruitsApiService.getFarmerByFruitsId(fruitsFarmerDTO).getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            GetFruitsResponse getFruitsResponse = objectMapper.readValue(inputData, GetFruitsResponse.class);

            Farmer farmer1 = new Farmer();
            farmer1.setFruitsId(getFruitsResponse.getFarmerID());
            farmer1.setFirstName(getFruitsResponse.getName());
            farmer1.setMiddleName(getFruitsResponse.getFatherName());

            List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndActive(getFruitsResponse.getFarmerType(), true);
            if(farmerTypeList.size()>0){
                farmer1.setFarmerTypeId(farmerTypeList.get(0).getFarmerTypeId());
            }

            farmer1.setMinority(getFruitsResponse.getMinority());
            farmer1.setRdNumber(getFruitsResponse.getRDNumber());
            farmer1.setCasteStatus(getFruitsResponse.getCasteStatus());
            farmer1.setGenderStatus(getFruitsResponse.getGenderStatus());
            farmer1.setFatherNameKan(getFruitsResponse.getFatherNameKan());
            farmer1.setFatherName(getFruitsResponse.getFatherName());
            farmer1.setNameKan(getFruitsResponse.getNameKan());

            // log.info("getFruitsResponse: " + getFruitsResponse);
            // log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
            // log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
            // log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );

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
            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer1, FarmerResponse.class));

            List<FarmerAddress> farmerAddressList = new ArrayList<>();
            FarmerAddress farmerAddress = new FarmerAddress();
            farmerAddress.setAddressText(getFruitsResponse.getResidentialAddress());
            farmerAddress.setPincode(getFruitsResponse.getPincode());
            farmerAddressList.add(farmerAddress);
            getFarmerResponse.setFarmerAddressList(farmerAddressList);


            List<FarmerLandDetailsDTO> farmerLandDetailsList = new ArrayList<>();
            for(GetLandDetailsResponse getLandDetailsResponse: getFruitsResponse.getLanddata()){
                FarmerLandDetailsDTO farmerLandDetails = new FarmerLandDetailsDTO();
                VillageDTO villageDTO = new VillageDTO();
                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);
               // if(responseWrapper1 != null) {
                if(((LinkedHashMap) responseWrapper1.getContent()).get("error").equals(false)){
                    farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                    farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                    farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                    farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                    farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));

                    farmerLandDetails.setStateName(((LinkedHashMap) responseWrapper1.getContent()).get("stateName").toString());
                    farmerLandDetails.setDistrictName(((LinkedHashMap) responseWrapper1.getContent()).get("districtName").toString());
                    farmerLandDetails.setTalukName(((LinkedHashMap) responseWrapper1.getContent()).get("talukName").toString());
                    farmerLandDetails.setHobliName(((LinkedHashMap) responseWrapper1.getContent()).get("hobliName").toString());
                    farmerLandDetails.setVillageName(((LinkedHashMap) responseWrapper1.getContent()).get("villageName").toString());
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);
                }

                farmerLandDetails.setHissa(getLandDetailsResponse.getHissano());
                farmerLandDetails.setSurveyNumber(String.valueOf(getLandDetailsResponse.getSurveyno()));

                farmerLandDetails.setOwnerName(getLandDetailsResponse.getOwnerName());
                farmerLandDetails.setSurNoc(String.valueOf(getLandDetailsResponse.getSurnoc()));
                farmerLandDetails.setAcre(Long.valueOf(getLandDetailsResponse.getAcre()));
                farmerLandDetails.setNameScore(Long.valueOf(getLandDetailsResponse.getNameScore()));
                farmerLandDetails.setOwnerNo(Long.valueOf(getLandDetailsResponse.getOwnerNo()));
                farmerLandDetails.setMainOwnerNo(Long.valueOf(String.valueOf(getLandDetailsResponse.getMainOwnerNo())));
                farmerLandDetails.setGunta(Long.valueOf(getLandDetailsResponse.getGunta()));
                farmerLandDetails.setFGunta(Double.valueOf(getLandDetailsResponse.getFgunta()));

                farmerLandDetailsList.add(farmerLandDetails);
            }
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsList);
            getFarmerResponse.setIsFruitService(1);
        }else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetailsDTO> farmerLandDetailsDTOS = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOS);
            getFarmerResponse.setIsFruitService(0);
        }

        return getFarmerResponse;
    }
    @Transactional
    public GetFarmerResponse getFarmerDetailsByFruitsIdOrFarmerNumberOrMobileNumber(GetFarmerRequest getFarmerRequest) throws Exception {
        GetFarmerResponse getFarmerResponse = new GetFarmerResponse();
        Farmer farmer = new Farmer();
        if(getFarmerRequest.getFarmerNumber() != null && !getFarmerRequest.getFarmerNumber().equals("")) {
            farmer = farmerRepository.findByFarmerNumberAndActive(getFarmerRequest.getFarmerNumber(), true);
        }else if(getFarmerRequest.getFruitsId() != null && !getFarmerRequest.getFruitsId().equals("")){
            farmer = farmerRepository.findByFruitsIdAndActive(getFarmerRequest.getFruitsId(), true);
        }else{
            farmer = farmerRepository.findByMobileNumberAndActive(getFarmerRequest.getMobileNumber(), true);
        }
        if(farmer == null){
            FruitsFarmerDTO fruitsFarmerDTO = new FruitsFarmerDTO();
            fruitsFarmerDTO.setFarmerId(getFarmerRequest.getFruitsId());

            //  GetFruitsResponse getFruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(fruitsFarmerDTO);
            String inputData = String.valueOf(fruitsApiService.getFarmerByFruitsId(fruitsFarmerDTO).getBody());

            if(inputData.equals("Error!, Please try again")){
                getFarmerResponse.setError(true);
                getFarmerResponse.setError_description("Farmer not found");
            }else {

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                GetFruitsResponse getFruitsResponse = objectMapper.readValue(inputData, GetFruitsResponse.class);

                Farmer farmer1 = new Farmer();
                farmer1.setFruitsId(getFruitsResponse.getFarmerID());
                farmer1.setFirstName(getFruitsResponse.getName());
                farmer1.setMiddleName(getFruitsResponse.getFatherName());

                List<FarmerType> farmerTypeList = farmerTypeRepository.findByFarmerTypeNameAndActive(getFruitsResponse.getFarmerType(), true);
                if (farmerTypeList.size() > 0) {
                    farmer1.setFarmerTypeId(farmerTypeList.get(0).getFarmerTypeId());
                }

                farmer1.setMinority(getFruitsResponse.getMinority());
                farmer1.setRdNumber(getFruitsResponse.getRDNumber());
                farmer1.setCasteStatus(getFruitsResponse.getCasteStatus());
                farmer1.setGenderStatus(getFruitsResponse.getGenderStatus());
                farmer1.setFatherNameKan(getFruitsResponse.getFatherNameKan());
                farmer1.setFatherName(getFruitsResponse.getFatherName());
                farmer1.setNameKan(getFruitsResponse.getNameKan());

                // log.info("getFruitsResponse: " + getFruitsResponse);
                // log.info("ERROR FINDER getFruitsResponse.getGender(): " + getFruitsResponse.getGender());
                // log.info("ERROR FINDER getFruitsResponse.getName(): " + getFruitsResponse.getName());
                // log.info("ERROR FINDER typeOf: " + getFruitsResponse.getGender().getClass().getName() );

                if (getFruitsResponse.getGender().equals("Male")) {
                    farmer1.setGenderId(1L);
                } else if (getFruitsResponse.getGender().equals("Female")) {
                    farmer1.setGenderId(2L);
                } else {
                    farmer1.setGenderId(3L);
                }
            /*CasteDTO casteDTO = new CasteDTO();
            casteDTO.setCaste(getFruitsResponse.getCaste());
            ResponseWrapper responseWrapper = getCaste(casteDTO);

            farmer1.setCasteId(Long.valueOf(((LinkedHashMap) responseWrapper.getContent()).get("id").toString()));
*/
                Caste caste = casteRepository.findByTitleAndActive(getFruitsResponse.getCaste(), true);
                if (caste != null) {
                    farmer1.setCasteId(caste.getCasteId());
                } else {
                    farmer1.setCasteId(0L);
                }

                if (getFruitsResponse.getPhysicallyChallenged().equals("No")) {
                    farmer1.setDifferentlyAbled(false);
                } else {
                    farmer1.setDifferentlyAbled(true);
                }
                getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer1, FarmerResponse.class));

                List<FarmerAddress> farmerAddressList = new ArrayList<>();
                FarmerAddress farmerAddress = new FarmerAddress();
                farmerAddress.setAddressText(getFruitsResponse.getResidentialAddress());
                farmerAddress.setPincode(getFruitsResponse.getPincode());
                farmerAddressList.add(farmerAddress);
                getFarmerResponse.setFarmerAddressList(farmerAddressList);


                List<FarmerLandDetailsDTO> farmerLandDetailsList = new ArrayList<>();
                for (GetLandDetailsResponse getLandDetailsResponse : getFruitsResponse.getLanddata()) {
                    FarmerLandDetailsDTO farmerLandDetails = new FarmerLandDetailsDTO();
//                VillageDTO villageDTO = new VillageDTO();
//                villageDTO.setVillageName(getLandDetailsResponse.getVillageName());
//                ResponseWrapper responseWrapper1 = getVillageDetails(villageDTO);

                    Village village = villageRepository.findByVillageNameAndActive(getLandDetailsResponse.getVillageName(), true);
                    if (village == null) {
                        farmerLandDetails.setVillageId(null);
                        farmerLandDetails.setHobliId(null);
                        farmerLandDetails.setTalukId(null);
                        farmerLandDetails.setDistrictId(null);
                        farmerLandDetails.setStateId(null);
                    } else {
                        VillageDTO villageDTO1 = villageRepository.getByVillageIdAndActive(village.getVillageId(), true);
                        farmerLandDetails.setVillageId(villageDTO1.getVillageId());
                        farmerLandDetails.setHobliId(villageDTO1.getHobliId());
                        farmerLandDetails.setTalukId(villageDTO1.getTalukId());
                        farmerLandDetails.setDistrictId(villageDTO1.getDistrictId());
                        farmerLandDetails.setStateId(villageDTO1.getStateId());

                        farmerLandDetails.setStateName(villageDTO1.getStateName());
                        farmerLandDetails.setDistrictName(villageDTO1.getDistrictName());
                        farmerLandDetails.setTalukName(villageDTO1.getTalukName());
                        farmerLandDetails.setHobliName(villageDTO1.getHobliName());
                        farmerLandDetails.setVillageName(villageDTO1.getVillageName());
                    }

                /*if(responseWrapper1 != null) {
                if(((LinkedHashMap) responseWrapper1.getContent()).get("error").equals(false)){
                    farmerLandDetails.setVillageId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("villageId").toString()));
                    farmerLandDetails.setHobliId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("hobliId").toString()));
                    farmerLandDetails.setTalukId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("talukId").toString()));
                    farmerLandDetails.setDistrictId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("districtId").toString()));
                    farmerLandDetails.setStateId(Long.valueOf(((LinkedHashMap) responseWrapper1.getContent()).get("stateId").toString()));

                    farmerLandDetails.setStateName(((LinkedHashMap) responseWrapper1.getContent()).get("stateName").toString());
                    farmerLandDetails.setDistrictName(((LinkedHashMap) responseWrapper1.getContent()).get("districtName").toString());
                    farmerLandDetails.setTalukName(((LinkedHashMap) responseWrapper1.getContent()).get("talukName").toString());
                    farmerLandDetails.setHobliName(((LinkedHashMap) responseWrapper1.getContent()).get("hobliName").toString());
                    farmerLandDetails.setVillageName(((LinkedHashMap) responseWrapper1.getContent()).get("villageName").toString());
                } else {
                    farmerLandDetails.setVillageId(null);
                    farmerLandDetails.setHobliId(null);
                    farmerLandDetails.setTalukId(null);
                    farmerLandDetails.setDistrictId(null);
                    farmerLandDetails.setStateId(null);
                }*/

                    farmerLandDetails.setHissa(getLandDetailsResponse.getHissano());
                    farmerLandDetails.setSurveyNumber(String.valueOf(getLandDetailsResponse.getSurveyno()));

                    farmerLandDetails.setOwnerName(getLandDetailsResponse.getOwnerName());
                    farmerLandDetails.setSurNoc(String.valueOf(getLandDetailsResponse.getSurnoc()));
                    farmerLandDetails.setAcre(Long.valueOf(getLandDetailsResponse.getAcre()));
                    farmerLandDetails.setNameScore(Long.valueOf(getLandDetailsResponse.getNameScore()));
                    farmerLandDetails.setOwnerNo(Long.valueOf(getLandDetailsResponse.getOwnerNo()));
                    farmerLandDetails.setMainOwnerNo(Long.valueOf(String.valueOf(getLandDetailsResponse.getMainOwnerNo())));
                    farmerLandDetails.setGunta(Long.valueOf(getLandDetailsResponse.getGunta()));
                    farmerLandDetails.setFGunta(Double.valueOf(getLandDetailsResponse.getFgunta()));

                    farmerLandDetailsList.add(farmerLandDetails);
                }
                getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsList);
                getFarmerResponse.setIsFruitService(1);
                getFarmerResponse.setError(false);
            }
        }else {
            List<FarmerAddress> farmerAddressList = farmerAddressRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetails> farmerLandDetailsList = farmerLandDetailsRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerLandDetailsDTO> farmerLandDetailsDTOS = farmerLandDetailsRepository.getByFarmerIdAndActive(farmer.getFarmerId(), true);
            List<FarmerFamily> farmerFamilyList = farmerFamilyRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);
            FarmerBankAccount farmerBankAccount = farmerBankAccountRepository.findByFarmerIdAndActive(farmer.getFarmerId(), true);

            getFarmerResponse.setFarmerResponse(mapper.farmerEntityToObject(farmer, FarmerResponse.class));
            getFarmerResponse.setFarmerAddressList(farmerAddressList);
            getFarmerResponse.setFarmerFamilyList(farmerFamilyList);
            getFarmerResponse.setFarmerLandDetailsDTOList(farmerLandDetailsDTOS);
            getFarmerResponse.setFarmerBankAccount(farmerBankAccount);
            getFarmerResponse.setIsFruitService(0);
            getFarmerResponse.setError(false);
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FarmerResponse getByFarmerIdJoin(int farmerId) {
        FarmerResponse farmerResponse = new FarmerResponse();
        FarmerDTO farmerDTO = farmerRepository.getByFarmerIdAndActive(farmerId, true);
        if (farmerDTO == null) {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Invalid id");
        } else {
            farmerResponse = mapper.farmerDTOToObject(farmerDTO, FarmerResponse.class);
            farmerResponse.setError(false);
        }
        log.info("Entity is ", farmerDTO);
        return farmerResponse;
    }
//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    public Map<String,Object> getByIdJoin(int farmerId){
//        List<FarmerDTO> farmerDTO = farmerRepository.getByIdAndActive(farmerId, true);
//        if(farmerDTO.isEmpty()){
//            throw new ValidationException("Farmer  not found by Farmer Id");
//        }
//        return convertListDTOToMapResponse(farmerDTO);
//    }
//
//    private Map<String, Object> convertListDTOToMapResponse(List<FarmerDTO> farmerDTOList) {
//        Map<String, Object> response = new HashMap<>();
//        List<FarmerResponse> farmerResponse = farmerDTOList.stream()
//                .map(farmerDTO -> mapper.farmerDTOToObject(farmerDTO, FarmerResponse.class)).collect(Collectors.toList());
//        response.put("farmer", farmerResponse);
//        response.put("totalItems", farmerDTOList.size());
//        return response;
//    }
@Transactional(isolation = Isolation.READ_COMMITTED)
public Map<String,Object> getPaginatedFarmerDetailsWithJoin(final Pageable pageable){
    return convertDTOToMapResponse(farmerRepository.getByActiveOrderByFarmerIdAsc( true, pageable));
}


    private Map<String, Object> convertDTOToMapResponse(final Page<FarmerDTO> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerDTOToObject(farmer,FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer",farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());
        return response;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> searchByColumnAndSort(SearchWithSortRequest searchWithSortRequest){
        if(searchWithSortRequest.getSearchText() == null || searchWithSortRequest.getSearchText().equals("")){
            searchWithSortRequest.setSearchText("%%");
        }else{
            searchWithSortRequest.setSearchText("%" + searchWithSortRequest.getSearchText() + "%");
        }
        if(searchWithSortRequest.getSortColumn() == null || searchWithSortRequest.getSortColumn().equals("")){
            searchWithSortRequest.setSortColumn("firstName");
        }
        if(searchWithSortRequest.getSortOrder() == null || searchWithSortRequest.getSortOrder().equals("")){
            searchWithSortRequest.setSortOrder("asc");
        }
        if(searchWithSortRequest.getPageNumber() == null || searchWithSortRequest.getPageNumber().equals("")){
            searchWithSortRequest.setPageNumber("0");
        }
        if(searchWithSortRequest.getPageSize() == null || searchWithSortRequest.getPageSize().equals("")){
            searchWithSortRequest.setPageSize("5");
        }
        Sort sort;
        if(searchWithSortRequest.getSortOrder().equals("asc")){
            sort = Sort.by(Sort.Direction.ASC, searchWithSortRequest.getSortColumn());
        }else{
            sort = Sort.by(Sort.Direction.DESC, searchWithSortRequest.getSortColumn());
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(searchWithSortRequest.getPageNumber()), Integer.parseInt(searchWithSortRequest.getPageSize()), sort);
        Page<FarmerDTO> farmerDTOS = farmerRepository.getSortedFarmers(searchWithSortRequest.getJoinColumn(),searchWithSortRequest.getSearchText(),true, pageable);
        log.info("Entity is ",farmerDTOS);
        return convertPageableDTOToMapResponse(farmerDTOS);
    }

    private Map<String, Object> convertPageableDTOToMapResponse(final Page<FarmerDTO> activeFarmers) {
        Map<String, Object> response = new HashMap<>();

        List<FarmerResponse> farmerResponses = activeFarmers.getContent().stream()
                .map(farmer -> mapper.farmerDTOToObject(farmer,FarmerResponse.class)).collect(Collectors.toList());
        response.put("farmer",farmerResponses);
        response.put("currentPage", activeFarmers.getNumber());
        response.put("totalItems", activeFarmers.getTotalElements());
        response.put("totalPages", activeFarmers.getTotalPages());

        return response;
    }

    @Transactional
    public FarmerResponse updatePhotoPath(MultipartFile multipartFile, String farmerId) throws Exception{
        FarmerResponse farmerResponse = new FarmerResponse();
        Farmer farmer = farmerRepository.findByFarmerIdAndActive(Long.parseLong(farmerId),true);
        if(Objects.nonNull(farmer)){
            UUID uuid = UUID.randomUUID();
            String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            String fileName = "farmer/"+farmerId+"_"+uuid+"_"+extension;
            s3Controller.uploadFile(multipartFile, fileName);
            farmer.setPhotoPath(fileName);
            farmer.setActive(true);
            Farmer farmer1 = farmerRepository.save(farmer);
            farmerResponse = mapper.farmerEntityToObject(farmer1, FarmerResponse.class);
            farmerResponse.setError(false);
        } else {
            farmerResponse.setError(true);
            farmerResponse.setError_description("Error occurred while fetching Farmer");
            // throw new ValidationException("Error occurred while fetching village");
        }
        return farmerResponse ;
    }
}

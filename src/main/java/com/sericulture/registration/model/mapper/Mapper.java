package com.sericulture.registration.model.mapper;

import com.sericulture.registration.model.api.education.EducationRequest;
import com.sericulture.registration.model.api.externalUnitRegistration.ExternalUnitRegistrationRequest;
import com.sericulture.registration.model.api.farmer.FarmerRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyRequest;
import com.sericulture.registration.model.api.farmerLandDetails.FarmerLandDetailsRequest;
import com.sericulture.registration.model.api.farmerType.FarmerTypeRequest;
import com.sericulture.registration.model.api.reeler.ReelerRequest;
import com.sericulture.registration.model.api.reelerLicenseTransaction.ReelerLicenseTransactionRequest;
import com.sericulture.registration.model.api.reelerVirtualBankAccount.ReelerVirtualBankAccountRequest;
import com.sericulture.registration.model.api.traderLicense.TraderLicenseRequest;
import com.sericulture.registration.model.dto.farmer.FarmerAddressDTO;
import com.sericulture.registration.model.dto.farmer.FarmerDTO;
import com.sericulture.registration.model.dto.farmer.FarmerFamilyDTO;
import com.sericulture.registration.model.dto.farmer.FarmerLandDetailsDTO;
import com.sericulture.registration.model.dto.fruitsApi.GetFruitsTokenDTO;
import com.sericulture.registration.model.dto.reeler.ReelerDTO;
import com.sericulture.registration.model.dto.reeler.ReelerVirtualBankAccountDTO;
import com.sericulture.registration.model.entity.*;
import com.sericulture.registration.repository.ReelerLicenseTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Mapper {

    @Autowired
    ModelMapper mapper;

    /**
     * Maps Education Entity to Education Response Object
     * @param educationEntity
     * @param <T>
     */
    public <T> T educationEntityToObject(Education educationEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, educationEntity);
        return (T) mapper.map(educationEntity, claaz);
    }

    /**
     * Maps Education Entity to Education Response Object
     * @param educationRequest
     * @param <T>
     */
    public <T> T educationObjectToEntity(EducationRequest educationRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, educationRequest);
        return (T) mapper.map(educationRequest, claaz);
    }

    /**
     * Maps Farmer Entity to Farmer Response Object
     * @param farmerEntity
     * @param <T>
     */
    public <T> T farmerEntityToObject(Farmer farmerEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerEntity);
        return (T) mapper.map(farmerEntity, claaz);
    }

    /**
     * Maps FarmerAddress DTO to FarmerAddress Response Object
     * @param farmerDTO
     * @param <T>
     */
    public <T> T farmerDTOToObject(FarmerDTO farmerDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerDTO);
        return (T) mapper.map(farmerDTO, claaz);
    }

    /**
     * Maps Farmer Object to Farmer Response Entity
     * @param farmerRequest
     * @param <T>
     */
    public <T> T farmerObjectToEntity(FarmerRequest farmerRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerRequest);
        return (T) mapper.map(farmerRequest, claaz);
    }

    /**
     * Maps FarmerFamily Entity to FarmerFamily Response Object
     * @param farmerFamilyEntity
     * @param <T>
     */
    public <T> T farmerFamilyEntityToObject(FarmerFamily farmerFamilyEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerFamilyEntity);
        return (T) mapper.map(farmerFamilyEntity, claaz);
    }

    /**
     * Maps FarmerFamily DTO to FarmerFamily Response Object
     * @param farmerFamilyDTO
     * @param <T>
     */
    public <T> T farmerFamilyDTOToObject(FarmerFamilyDTO farmerFamilyDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerFamilyDTO);
        return (T) mapper.map(farmerFamilyDTO, claaz);
    }

    /**
     * Maps FarmerFamily Object to FarmerFamily Response Entity
     * @param farmerFamilyRequest
     * @param <T>
     */
    public <T> T farmerFamilyObjectToEntity(FarmerFamilyRequest farmerFamilyRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerFamilyRequest);
        return (T) mapper.map(farmerFamilyRequest, claaz);
    }

    /**
     * Maps FarmerBankAccount Entity to FarmerBankAccount Response Object
     * @param farmerBankAccountEntity
     * @param <T>
     */
    public <T> T farmerBankAccountEntityToObject(FarmerBankAccount farmerBankAccountEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerBankAccountEntity);
        return (T) mapper.map(farmerBankAccountEntity, claaz);
    }

    /**
     * Maps FarmerBankAccount Object to FarmerBankAccount Response Entity
     * @param farmerBankAccountRequest
     * @param <T>
     */
    public <T> T farmerBankAccountObjectToEntity(FarmerBankAccountRequest farmerBankAccountRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerBankAccountRequest);
        return (T) mapper.map(farmerBankAccountRequest, claaz);
    }

    /**
     * Maps FarmerAddress Entity to FarmerAddress Response Object
     * @param farmerAddressEntity
     * @param <T>
     */
    public <T> T farmerAddressEntityToObject(FarmerAddress farmerAddressEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerAddressEntity);
        return (T) mapper.map(farmerAddressEntity, claaz);
    }

    /**
     * Maps FarmerAddress Object to FarmerAddress Response Entity
     * @param farmerAddressRequest
     * @param <T>
     */
    public <T> T farmerAddressObjectToEntity(FarmerAddressRequest farmerAddressRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerAddressRequest);
        return (T) mapper.map(farmerAddressRequest, claaz);
    }

    /**
     * Maps FarmerAddress DTO to FarmerAddress Response Object
     * @param farmerAddressDTO
     * @param <T>
     */
    public <T> T farmerAddressDTOToObject(FarmerAddressDTO farmerAddressDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerAddressDTO);
        return (T) mapper.map(farmerAddressDTO, claaz);
    }

    /**
     * Maps FarmerLandDetails Entity to FarmerLandDetails Response Object
     * @param farmerLandDetailsEntity
     * @param <T>
     */
    public <T> T farmerLandDetailsEntityToObject(FarmerLandDetails farmerLandDetailsEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerLandDetailsEntity);
        return (T) mapper.map(farmerLandDetailsEntity, claaz);
    }

    /**
     * Maps FarmerLandDetails DTO to FarmerLandDetails Response Object
     * @param farmerLandDetailsDTO
     * @param <T>
     */
    public <T> T farmerLandDetailsDTOToObject(FarmerLandDetailsDTO farmerLandDetailsDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerLandDetailsDTO);
        return (T) mapper.map(farmerLandDetailsDTO, claaz);
    }

    /**
     * Maps FarmerAddress Object to FarmerAddress Response Entity
     * @param farmerLandDetailsRequest
     * @param <T>
     */
    public <T> T farmerLandDetailsObjectToEntity(FarmerLandDetailsRequest farmerLandDetailsRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerLandDetailsRequest);
        return (T) mapper.map(farmerLandDetailsRequest, claaz);
    }

    /**
     * Maps Reeler Entity to Reeler Response Object
     * @param reelerEntity
     * @param <T>
     */
    public <T> T reelerEntityToObject(Reeler reelerEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerEntity);
        return (T) mapper.map(reelerEntity, claaz);
    }

    /**
     * Maps Reeler DTO to Reeler Response Object
     * @param reelerDTO
     * @param <T>
     */
    public <T> T reelerDTOToObject(ReelerDTO reelerDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerDTO);
        return (T) mapper.map(reelerDTO, claaz);
    }

    /**
     * Maps Reeler Object to Reeler Response Entity
     * @param reelerRequest
     * @param <T>
     */
    public <T> T reelerObjectToEntity(ReelerRequest reelerRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerRequest);
        return (T) mapper.map(reelerRequest, claaz);
    }

    /**
     * Maps ReelerVirtualBankAccount Entity to ReelerVirtualBankAccount Response Object
     * @param reelerVirtualBankAccountEntity
     * @param <T>
     */
    public <T> T reelerVirtualBankAccountEntityToObject(ReelerVirtualBankAccount reelerVirtualBankAccountEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerVirtualBankAccountEntity);
        return (T) mapper.map(reelerVirtualBankAccountEntity, claaz);
    }

    /**
     * Maps ReelerVirtualBankAccount DTO to ReelerVirtualBankAccount Response Object
     * @param reelerVirtualBankAccountDTO
     * @param <T>
     */
    public <T> T reelerVirtualBankAccountDTOToObject(ReelerVirtualBankAccountDTO reelerVirtualBankAccountDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerVirtualBankAccountDTO);
        return (T) mapper.map(reelerVirtualBankAccountDTO, claaz);
    }

    /**
     * Maps ReelerVirtualBankAccount Object to ReelerVirtualBankAccount Response Entity
     * @param reelerVirtualBankAccountRequest
     * @param <T>
     */
    public <T> T reelerVirtualBankAccountObjectToEntity(ReelerVirtualBankAccountRequest reelerVirtualBankAccountRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerVirtualBankAccountRequest);
        return (T) mapper.map(reelerVirtualBankAccountRequest, claaz);
    }

    /**
     * Maps ReelerLicenseTransaction Entity to ReelerLicenseTransaction Response Object
     * @param reelerLicenseTransactionEntity
     * @param <T>
     */
    public <T> T reelerLicenseTransactionEntityToObject(ReelerLicenseTransaction reelerLicenseTransactionEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerLicenseTransactionEntity);
        return (T) mapper.map(reelerLicenseTransactionEntity, claaz);
    }

    /**
     * Maps ReelerLicenseTransaction Object to ReelerLicenseTransaction Response Entity
     * @param reelerLicenseTransactionEntityRequest
     * @param <T>
     */
    public <T> T reelerLicenseTransactionObjectToEntity(ReelerLicenseTransactionRequest reelerLicenseTransactionEntityRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, reelerLicenseTransactionEntityRequest);
        return (T) mapper.map(reelerLicenseTransactionEntityRequest, claaz);
    }

    /**
     * Maps TraderLicense Entity to TraderLicense Response Object
     * @param traderLicenseTransactionEntity
     * @param <T>
     */
    public <T> T traderLicenseEntityToObject(TraderLicense traderLicenseTransactionEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, traderLicenseTransactionEntity);
        return (T) mapper.map(traderLicenseTransactionEntity, claaz);
    }

    /**
     * Maps TraderLicense Object to TraderLicense Response Entity
     * @param traderLicenseRequest
     * @param <T>
     */
    public <T> T traderLicenseObjectToEntity(TraderLicenseRequest traderLicenseRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, traderLicenseRequest);
        return (T) mapper.map(traderLicenseRequest, claaz);
    }

    /**
     * Maps ExternalUnitRegistration Entity to ExternalUnitRegistration Response Object
     * @param externalUnitRegistrationEntity
     * @param <T>
     */
    public <T> T externalUnitRegistrationEntityToObject(ExternalUnitRegistration externalUnitRegistrationEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, externalUnitRegistrationEntity);
        return (T) mapper.map(externalUnitRegistrationEntity, claaz);
    }

    /**
     * Maps ExternalUnitRegistration Object to ExternalUnitRegistration Response Entity
     * @param externalUnitRegistrationEntityRequest
     * @param <T>
     */
    public <T> T externalUnitRegistrationObjectToEntity(ExternalUnitRegistrationRequest externalUnitRegistrationEntityRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, externalUnitRegistrationEntityRequest);
        return (T) mapper.map(externalUnitRegistrationEntityRequest, claaz);
    }

    /**
     * Maps GetFruitsTokenDTO to GetFruitsToken Response Object
     * @param getFruitsTokenDTO
     * @param <T>
     */
    public <T> T getFruitsTokenEntityToObject(GetFruitsTokenDTO getFruitsTokenDTO, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, getFruitsTokenDTO);
        return (T) mapper.map(getFruitsTokenDTO, claaz);
    }

    /**
     * Maps FarmerType Entity to FarmerType Response Object
     * @param farmerTypeEntity
     * @param <T>
     */
    public <T> T farmerTypeEntityToObject(FarmerType farmerTypeEntity, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerTypeEntity);
        return (T) mapper.map(farmerTypeEntity, claaz);
    }

    /**
     * Maps FarmerType Object to FarmerType Response Entity
     * @param farmerTypeRequest
     * @param <T>
     */
    public <T> T farmerTypeObjectToEntity(FarmerTypeRequest farmerTypeRequest, Class<T> claaz) {
        log.info("Value of mapper is:",mapper, farmerTypeRequest);
        return (T) mapper.map(farmerTypeRequest, claaz);
    }
}

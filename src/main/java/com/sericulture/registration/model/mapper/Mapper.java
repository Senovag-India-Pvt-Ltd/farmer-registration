package com.sericulture.registration.model.mapper;

import com.sericulture.registration.model.api.education.EducationRequest;
import com.sericulture.registration.model.api.farmer.FarmerRequest;
import com.sericulture.registration.model.api.farmerAddress.FarmerAddressRequest;
import com.sericulture.registration.model.api.farmerBankAccount.FarmerBankAccountRequest;
import com.sericulture.registration.model.api.farmerFamily.FarmerFamilyRequest;
import com.sericulture.registration.model.entity.*;
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
}

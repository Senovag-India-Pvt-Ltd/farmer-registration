package com.sericulture.registration.service;

import com.sericulture.registration.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DashboardService {
    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    TraderLicenseRepository traderLicenseRepository;

    @Autowired
    ExternalUnitRegistrationRepository externalUnitRegistrationRepository;

    @Autowired
    ChowkiManagementRepository chowkiManagementRepository;

    @Autowired
    ReelerRepository reelerRepository;

    public List<Map<String, Object>> getFarmerDetails() {
        return farmerRepository.getFullFarmerDetails();
    }

    public List<Map<String, Object>> getReelerDetails() {
        return reelerRepository.getFullReelerDetails();
    }

    public List<Map<String, Object>> getTraderLicenseDetails() {
        return traderLicenseRepository.getFullTraderLicenseDetails();
    }

    public List<Map<String, Object>> getExternalUnitDetails() {
        return externalUnitRegistrationRepository.getFullExternalUnitDetails();
    }

    public List<Map<String, Object>> getChawkiManagementDetails() {
        return chowkiManagementRepository.getChawkiManagementDetails();
    }

    public List<Map<String, Object>> getHelpDeskDetails() {
        return chowkiManagementRepository.getHelpDeskDetails();
    }

    public List<Map<String, Object>> getTrainerDetails() {
        return chowkiManagementRepository.getTrainerDetails();
    }

    public List<Map<String, Object>> getTraineeDetails() {
        return chowkiManagementRepository.getTraineeDetails();
    }

    public List<Map<String, Object>> getDBTDetails() {
        return chowkiManagementRepository.getDBTDetails();
    }

    public List<Map<String, Object>> getSeedMarketDetails() {
        return chowkiManagementRepository.getSeedMarketDetails();
    }

    public List<Map<String, Object>> getMaintenanceAndSaleOfNurseryDetails() {
        return chowkiManagementRepository.getMaintenanceAndSaleOfNurseryDetails();
    }

    public List<Map<String, Object>> getMaintenanceOfMulberryGardenDetails() {
        return chowkiManagementRepository.getMaintenanceOfMulberryGardenDetails();
    }

    public List<Map<String, Object>> getSeedCuttingBankDetails() {
        return chowkiManagementRepository.getSeedCuttingBankDetails();
    }

    public List<Map<String, Object>> getSupplyOfCocoonsDetails() {
        return chowkiManagementRepository.getSupplyOfCocoonsDetails();
    }

    public List<Map<String, Object>> getChawkiDistributionDetails() {
        return chowkiManagementRepository.getChawkiDistributionDetails();
    }

}

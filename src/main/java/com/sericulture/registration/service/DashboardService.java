package com.sericulture.registration.service;

import com.sericulture.registration.repository.ExternalUnitRegistrationRepository;
import com.sericulture.registration.repository.FarmerRepository;
import com.sericulture.registration.repository.ReelerRepository;
import com.sericulture.registration.repository.TraderLicenseRepository;
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

}

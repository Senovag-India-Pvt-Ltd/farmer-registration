package com.sericulture.registration.sevasindhu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sericulture.registration.model.api.fruitsApi.GetFruitsResponse;
import com.sericulture.registration.model.dto.fruitsApi.FruitsFarmerDTO;
import com.sericulture.registration.model.entity.Farmer;
import com.sericulture.registration.repository.FarmerRepository;
import com.sericulture.registration.service.FruitsApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Internal, TEST-ONLY simulation of the Seva Sindhu "application initiate" /
// "farmer validate" entry point (source doc sections 4.1/4.2). It does NOT
// create a farmer or an application by itself -- it only determines, exactly
// the way the real integration would, whether the citizen should be routed to
// Farmer Registration or straight to a Scheme Application, and logs the call.
// There is no real Seva Sindhu caller yet (auth mechanism, exact contract and
// status codes are still open points per the source requirement document), so
// this endpoint is deliberately NOT public -- it stays behind the normal
// Sericulture login, for an admin/QA user to drive by hand.
@Service
@Slf4j
public class SevaSindhuTestHarnessService {

    private static final String ENDPOINT_NAME = "sevasindhu-test/simulate-redirect";

    @Autowired
    FarmerRepository farmerRepository;

    @Autowired
    FruitsApiService fruitsApiService;

    @Autowired
    SevaSindhuIntegrationLogRepository logRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public SimulateRedirectResponse simulateRedirect(SimulateRedirectRequest request) {
        SimulateRedirectResponse response;

        if (isBlank(request.getMobileNumber()) && isBlank(request.getFruitsId())) {
            response = SimulateRedirectResponse.builder()
                    .sevaSindhuReference(request.getSevaSindhuReference())
                    .error(true)
                    .error_description("Mobile number or FRUITS ID is required.")
                    .build();
            log(request, response, "REJECTED");
            return response;
        }

        // Registration check: does this farmer already exist in Sericulture?
        // FRUITS ID is the reliable key when supplied (mirrors FarmerService's own
        // "already registered" check); fall back to mobile number when it isn't.
        Farmer farmer = null;
        if (!isBlank(request.getFruitsId())) {
            farmer = farmerRepository.findByFruitsIdAndActive(request.getFruitsId(), true);
        }
        if (farmer == null && !isBlank(request.getMobileNumber())) {
            java.util.List<Farmer> byMobile = farmerRepository.findByMobileNumber(request.getMobileNumber());
            farmer = byMobile.stream().filter(Farmer::getActive).findFirst().orElse(null);
        }

        SimulateRedirectResponse.SimulateRedirectResponseBuilder builder = SimulateRedirectResponse.builder()
                .sevaSindhuReference(request.getSevaSindhuReference())
                .mobileNumber(request.getMobileNumber())
                .fruitsId(request.getFruitsId())
                .error(false);

        if (farmer != null) {
            // Already registered -- the real flow would proceed straight to the
            // scheme application, tagging application_source = SEVA_SINDHU (Phase 2).
            builder.farmerRegistered(true)
                    .nextAction("SCHEME_APPLICATION")
                    .farmerId(farmer.getFarmerId())
                    .farmerName(joinName(farmer.getFirstName(), farmer.getLastName()));
        } else {
            // Not registered -- the real flow routes to Farmer Registration, tagging
            // registration_source = SEVA_SINDHU (Phase 2). Attempt a live FRUITS
            // lookup so a tester can see what bio-data would pre-fill the
            // registration form, but never let that external call's availability
            // affect the branching decision itself.
            builder.farmerRegistered(false).nextAction("FARMER_REGISTRATION");

            if (!isBlank(request.getFruitsId())) {
                builder.fruitsLookupAttempted(true);
                try {
                    GetFruitsResponse fruitsResponse = fruitsApiService.getFarmerByFruitsIdWithResponse(
                            new FruitsFarmerDTO(request.getFruitsId()));
                    if (fruitsResponse != null && isBlank(fruitsResponse.getError())
                            && !isBlank(fruitsResponse.getName())) {
                        builder.fruitsLookupSuccess(true).fruitsLookupFarmerName(fruitsResponse.getName());
                    } else {
                        String message = fruitsResponse != null ? fruitsResponse.getError_description() : "No response from FRUITS";
                        builder.fruitsLookupSuccess(false).fruitsLookupMessage(message);
                    }
                } catch (Exception e) {
                    log.warn("FRUITS lookup failed during Seva Sindhu test harness simulation", e);
                    builder.fruitsLookupSuccess(false).fruitsLookupMessage(e.getMessage());
                }
            } else {
                builder.fruitsLookupAttempted(false);
            }
        }

        response = builder.build();
        log(request, response, "SUCCESS");
        return response;
    }

    private void log(SimulateRedirectRequest request, SimulateRedirectResponse response, String status) {
        try {
            SevaSindhuIntegrationLog logRow = new SevaSindhuIntegrationLog();
            logRow.setSevaSindhuReference(request.getSevaSindhuReference());
            logRow.setEndpointName(ENDPOINT_NAME);
            logRow.setRequestPayload(objectMapper.writeValueAsString(request));
            logRow.setResponsePayload(objectMapper.writeValueAsString(response));
            logRow.setStatus(status);
            logRepository.save(logRow);
        } catch (Exception e) {
            // Logging must never break the harness itself.
            log.error("Failed to write sevasindhu_integration_log row", e);
        }
    }

    private String joinName(String firstName, String lastName) {
        StringBuilder sb = new StringBuilder();
        if (!isBlank(firstName)) sb.append(firstName.trim());
        if (!isBlank(lastName)) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(lastName.trim());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

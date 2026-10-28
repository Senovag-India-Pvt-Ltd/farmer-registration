package com.sericulture.registration.sevasindhu;

import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.CheckRequest;
import com.sericulture.registration.sevasindhu.SevaSindhuFarmerDtos.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Seva Sindhu citizen farmer check and self-registration.
 * Under /v1/dashboard/** (left open by the shared JWT layer); every call requires the citizen session
 * header that dbt issued after URL 1.
 */
@RestController
@RequestMapping("/v1/dashboard/sevasindhu/farmer")
@Slf4j
public class SevaSindhuFarmerController {

    private final SevaSindhuFarmerService service;
    private final SevaSindhuCitizenRequestRepository repository;
    private final int perMinute;
    private final boolean trustForwardedFor;
    private final Map<String, long[]> windows = new ConcurrentHashMap<>();

    public SevaSindhuFarmerController(SevaSindhuFarmerService service,
                                      SevaSindhuCitizenRequestRepository repository,
                                      @Value("${SEVASINDHU_CITIZEN_RATE_LIMIT_PER_MINUTE:60}") int perMinute,
                                      @Value("${SEVASINDHU_TRUST_FORWARDED_FOR:false}") boolean trustForwardedFor) {
        this.service = service;
        this.repository = repository;
        this.perMinute = perMinute;
        this.trustForwardedFor = trustForwardedFor;
    }

    /** FRUITS ID check: links an existing farmer, or returns FRUITS data for self-registration. */
    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                   @RequestBody(required = false) CheckRequest body, HttpServletRequest http) {
        return run(http, () -> service.check(service.requireSession(session), body == null ? null : body.getFruitsId()));
    }

    /** Self-registration of a farmer not yet in e-Reshme. */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                      @RequestBody(required = false) RegisterRequest body, HttpServletRequest http) {
        return run(http, () -> {
            SevaSindhuCitizenRequest request = service.requireSession(session);
            if (body != null) {
                String mobile = request.getMobileNumber() != null ? request.getMobileNumber() : body.getMobileNumber();
                SevaSindhuFarmerService.validate(body, mobile == null ? null : mobile.trim());
                service.requireValidLocation(body);
            }
            return service.register(request, body);
        });
    }

    @GetMapping("/locations/districts")
    public ResponseEntity<?> districts(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                       HttpServletRequest http) {
        return run(http, () -> {
            service.requireSession(session);
            return service.options(repository.districts(SevaSindhuFarmerService.KARNATAKA_STATE_ID));
        });
    }

    @GetMapping("/locations/taluks")
    public ResponseEntity<?> taluks(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                    @RequestParam Long districtId, HttpServletRequest http) {
        return run(http, () -> {
            service.requireSession(session);
            return service.options(repository.taluks(districtId));
        });
    }

    @GetMapping("/locations/hoblis")
    public ResponseEntity<?> hoblis(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                    @RequestParam Long talukId, HttpServletRequest http) {
        return run(http, () -> {
            service.requireSession(session);
            return service.options(repository.hoblis(talukId));
        });
    }

    @GetMapping("/locations/villages")
    public ResponseEntity<?> villages(@RequestHeader(value = SevaSindhuFarmerService.SESSION_HEADER, required = false) String session,
                                      @RequestParam Long hobliId, HttpServletRequest http) {
        return run(http, () -> {
            service.requireSession(session);
            return service.options(repository.villages(hobliId));
        });
    }

    private ResponseEntity<?> run(HttpServletRequest http, Supplier<Object> action) {
        if (!allow(clientIp(http))) {
            return error(429, "TOO_MANY_REQUESTS", "Too many requests. Please wait a minute and try again.");
        }
        try {
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(action.get());
        } catch (SevaSindhuFarmerService.CitizenException e) {
            return error(e.getStatus(), e.getCode(), e.getMessage());
        } catch (DataAccessException e) {
            log.error("Seva Sindhu farmer endpoint failed on the database", e);
            return error(503, "SERVICE_UNAVAILABLE", "The service is temporarily unavailable. Please try again later.");
        }
    }

    private static ResponseEntity<Map<String, String>> error(int status, String code, String message) {
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore()).body(Map.of("code", code, "message", message));
    }

    private String clientIp(HttpServletRequest http) {
        if (trustForwardedFor) {
            String xff = http.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        }
        return http.getRemoteAddr();
    }

    /** Fixed one-minute window per client IP (same approach as dbt's SimpleRateLimiter). */
    private boolean allow(String key) {
        if (perMinute <= 0) return true;
        long now = System.currentTimeMillis();
        if (windows.size() > 20_000) windows.entrySet().removeIf(e -> now - e.getValue()[0] >= 60_000L);
        long[] w = windows.compute(key, (k, v) -> v == null || now - v[0] >= 60_000L ? new long[]{now, 1} : new long[]{v[0], v[1] + 1});
        return w[1] <= perMinute;
    }
}

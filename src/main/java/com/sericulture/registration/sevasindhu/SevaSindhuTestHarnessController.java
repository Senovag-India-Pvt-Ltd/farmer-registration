package com.sericulture.registration.sevasindhu;

import com.sericulture.registration.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Internal, TEST-ONLY harness for the future Seva Sindhu integration.
// Deliberately NOT added to the SecurityConfig permitAll list -- there is no
// real external caller yet (auth mechanism/contract are still open points per
// the source requirement document), so this stays behind the normal
// Sericulture login like every other endpoint in this module. Frontend access
// is further restricted to a dedicated admin menu permission.
@RestController
@RequestMapping("/v1/internal/sevasindhu-test")
public class SevaSindhuTestHarnessController {

    @Autowired
    SevaSindhuTestHarnessService sevaSindhuTestHarnessService;

    @PostMapping("/simulate-redirect")
    public ResponseEntity<?> simulateRedirect(@RequestBody SimulateRedirectRequest request) {
        ResponseWrapper rw = ResponseWrapper.createWrapper(SimulateRedirectResponse.class);
        rw.setContent(sevaSindhuTestHarnessService.simulateRedirect(request));
        return ResponseEntity.ok(rw);
    }
}

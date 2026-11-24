package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmerRepresentative.FarmerRepresentativeRequest;
import com.sericulture.registration.service.FarmerRepresentativeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/** Authorised agents / representatives of a farmer (SR-005), max per farmer = FARMER_MAX_REPRESENTATIVES. */
@RestController
@RequestMapping("/v1/farmer-representative")
public class FarmerRepresentativeController {

    private final FarmerRepresentativeService service;

    public FarmerRepresentativeController(FarmerRepresentativeService service) {
        this.service = service;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        response.put("validationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Operation(summary = "Maximum agents allowed per farmer")
    @GetMapping("/config")
    public ResponseEntity<ResponseWrapper<Object>> config() {
        return ok(service.config());
    }

    @Operation(summary = "Active agents of a farmer")
    @GetMapping("/get-by-farmer-id/{farmerId}")
    public ResponseEntity<ResponseWrapper<Object>> getByFarmerId(@PathVariable Long farmerId) {
        return ok(service.getByFarmerId(farmerId));
    }

    @Operation(summary = "Find agents by mobile number or name, with the farmer each one is linked to")
    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Object>> search(@RequestParam("q") String q) {
        return ok(service.search(q));
    }

    @Operation(summary = "Add an agent (refused when the farmer already has the maximum)")
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Object>> add(@Valid @RequestBody FarmerRepresentativeRequest request) {
        return ok(service.add(request));
    }

    @Operation(summary = "Edit an agent")
    @PostMapping("/edit")
    public ResponseEntity<ResponseWrapper<Object>> edit(@Valid @RequestBody FarmerRepresentativeRequest request) {
        return ok(service.edit(request));
    }

    @Operation(summary = "Remove (deactivate) an agent")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseWrapper<Object>> delete(@PathVariable Long id) {
        return ok(service.delete(id));
    }

    private static ResponseEntity<ResponseWrapper<Object>> ok(Object content) {
        ResponseWrapper<Object> rw = new ResponseWrapper<>();
        rw.setContent(content);
        return ResponseEntity.ok(rw);
    }
}

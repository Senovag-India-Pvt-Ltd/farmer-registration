package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.reelerRepresentative.ReelerRepresentativeRequest;
import com.sericulture.registration.service.ReelerRepresentativeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/** Reeler representatives / agents (SR-013), max per reeler = REELER_MAX_REPRESENTATIVES. */
@RestController
@RequestMapping("/v1/reeler-representative")
public class ReelerRepresentativeController {

    private final ReelerRepresentativeService service;

    public ReelerRepresentativeController(ReelerRepresentativeService service) {
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

    @Operation(summary = "Maximum representatives allowed per reeler")
    @GetMapping("/config")
    public ResponseEntity<ResponseWrapper<Object>> config() {
        return ok(service.config());
    }

    @Operation(summary = "Active representatives of a reeler")
    @GetMapping("/get-by-reeler-id/{reelerId}")
    public ResponseEntity<ResponseWrapper<Object>> getByReelerId(@PathVariable Long reelerId) {
        return ok(service.getByReelerId(reelerId));
    }

    @Operation(summary = "Add a representative (refused when the reeler already has the maximum)")
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Object>> add(@Valid @RequestBody ReelerRepresentativeRequest request) {
        return ok(service.add(request));
    }

    @Operation(summary = "Edit a representative")
    @PostMapping("/edit")
    public ResponseEntity<ResponseWrapper<Object>> edit(@Valid @RequestBody ReelerRepresentativeRequest request) {
        return ok(service.edit(request));
    }

    @Operation(summary = "Remove (deactivate) a representative")
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

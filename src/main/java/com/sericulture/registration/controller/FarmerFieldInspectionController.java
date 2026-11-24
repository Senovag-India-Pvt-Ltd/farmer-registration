package com.sericulture.registration.controller;

import com.sericulture.registration.model.ResponseWrapper;
import com.sericulture.registration.model.api.farmerFieldInspection.FarmerFieldInspectionRequest;
import com.sericulture.registration.service.FarmerFieldInspectionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/** SR-007: GPS-tagged field inspections of a farmer's farm / rearing house. */
@RestController
@RequestMapping("/v1/farmer-field-inspection")
public class FarmerFieldInspectionController {

    private final FarmerFieldInspectionService service;

    public FarmerFieldInspectionController(FarmerFieldInspectionService service) {
        this.service = service;
    }

    /** Multipart form binding errors (MethodArgumentNotValidException is a BindException). */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(BindException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            String msg = error instanceof FieldError fe && fe.isBindingFailure() ? "Invalid value for " + fe.getField() : error.getDefaultMessage();
            errors.put(field, msg);
        });
        response.put("validationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Operation(summary = "Attach a GPS-tagged field inspection (optional photo) to a farmer's farm / rearing house")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<Object>> add(@Valid @ModelAttribute FarmerFieldInspectionRequest request,
                                                       @RequestParam(value = "photo", required = false) MultipartFile photo) {
        return ok(service.add(request, photo));
    }

    @Operation(summary = "The farmer's land records with their recorded GPS and field inspections")
    @GetMapping("/get-by-farmer-id/{farmerId}")
    public ResponseEntity<ResponseWrapper<Object>> getByFarmer(@PathVariable Long farmerId) {
        return ok(service.getByFarmer(farmerId));
    }

    @Operation(summary = "Photo of a field inspection")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> photo(@PathVariable Long id) throws Exception {
        Object[] p = service.photo(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType((String) p[1])).body((byte[]) p[0]);
    }

    @Operation(summary = "Remove (deactivate) a field inspection")
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

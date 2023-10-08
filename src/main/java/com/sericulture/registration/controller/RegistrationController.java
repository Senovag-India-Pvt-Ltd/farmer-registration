package com.sericulture.registration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/registration")
public class RegistrationController {

    @PostMapping("/get-details")
    public ResponseEntity<?> getFarmerDetails(){
        return null;
    }
}


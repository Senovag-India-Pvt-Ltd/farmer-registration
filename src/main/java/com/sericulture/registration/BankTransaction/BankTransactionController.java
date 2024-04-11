package com.sericulture.registration.BankTransaction;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class BankTransactionController {
    @Autowired
    BankTransactionService bankTransactionService;

    public ResponseEntity<?> creditTransaction(@RequestHeader HttpHeaders headers,
                                               @org.springframework.web.bind.annotation.RequestBody JsonNode jsonNode){
        try {
            log.debug("Received request for credit.");
            Long id = bankTransactionService.saveAudit(headers.toString(), jsonNode.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            GenericBankTransactionRequest genericBankTransactionRequest =
                    objectMapper.convertValue(jsonNode, GenericBankTransactionRequest.class);
            BankResponse response = bankTransactionService.saveTransaction(genericBankTransactionRequest);
            bankTransactionService.updateAudit(response.toString(), id); // make this async
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            JsonNode value = jsonNode.findValue("Alert Sequence No");
            String alertSequenceNo = value == null ? null : value.asText();
            GenericCorporateAlertResponse gen = new GenericCorporateAlertResponse("1",
                    BankResponseMessageEnum.REJECT.getLabel(),
                    alertSequenceNo);
            BankResponse res = new BankResponse(gen);
            return ResponseEntity.ok(gen);
        }

    }
}
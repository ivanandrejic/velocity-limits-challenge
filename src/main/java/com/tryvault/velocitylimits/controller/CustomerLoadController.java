package com.tryvault.velocitylimits.controller;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.service.CustomerLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomerLoadController {
    @Autowired
    private CustomerLoadService service;

    @PostMapping("/load")
    public ResponseEntity<CustomerLoad> loadFunds(@RequestBody LoadRequest request) {
        try {
            CustomerLoad result = service.processLoadRequest(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing load request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

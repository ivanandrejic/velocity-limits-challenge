package com.tryvault.velocitylimits.controller;

import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.dto.LoadResponse;
import com.tryvault.velocitylimits.service.CustomerLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<LoadResponse> loadFunds(@RequestBody LoadRequest request) {
        var result = service.processLoadRequest(request);
        return ResponseEntity.ok(result);
    }
}

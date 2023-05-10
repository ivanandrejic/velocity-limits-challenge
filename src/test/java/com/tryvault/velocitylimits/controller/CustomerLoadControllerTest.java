package com.tryvault.velocitylimits.controller;

import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.dto.LoadResponse;
import com.tryvault.velocitylimits.exception.ErrorResponse;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerLoadControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CustomerLoadRepository repository;

    @LocalServerPort
    private int port;

    private String getUrl() {
        return "http://localhost:" + port + "/load";
    }

    @Test
    public void testLoadFunds_validRequest() {
        var request = new LoadRequest();
        request.setId("1");
        request.setCustomerId("1");
        request.setLoadAmount("$1000");
        request.setTime("2023-05-09T12:00:00Z");
        var response = restTemplate.postForEntity(getUrl(), request, LoadResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("1", response.getBody().getCustomerId());
        assertTrue(response.getBody().isAccepted());
    }

    @Test
    public void testLoadFunds_invalidInput() {
        var request = new LoadRequest();
        request.setId("");
        request.setCustomerId("");
        request.setLoadAmount("invalid_amount");
        request.setTime("invalid_time");
        var response = restTemplate.postForEntity(getUrl(), request, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Invalid input"));
    }


}


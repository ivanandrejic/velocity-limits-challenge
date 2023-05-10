package com.tryvault.velocitylimits.controller;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        LoadRequest request = new LoadRequest();
        request.setId("1");
        request.setCustomerId("1");
        request.setLoadAmount("$1000");
        request.setTime("2023-05-09T12:00:00Z");

        ResponseEntity<CustomerLoad> response = restTemplate.postForEntity(getUrl(), request, CustomerLoad.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("1", response.getBody().getCustomerId());
        assertEquals(0, new BigDecimal("1000").compareTo(response.getBody().getLoadAmount()));
        assertEquals(LocalDateTime.parse("2023-05-09T12:00:00Z", DateTimeFormatter.ISO_DATE_TIME), response.getBody().getTime());
        assertTrue(response.getBody().isAccepted());
    }

}


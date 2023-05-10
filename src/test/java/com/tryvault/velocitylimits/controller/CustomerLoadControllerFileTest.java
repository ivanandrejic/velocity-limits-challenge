package com.tryvault.velocitylimits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest
public class CustomerLoadControllerFileTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Value("classpath:input.txt")
    private Resource inputFile;

    @Value("classpath:output.txt")
    private Resource outputFile;

    private String getUrl() {
        return "http://localhost:" + "8080" + "/load";
    }

    @Test
    public void testLoadFunds_fromInputFile() throws IOException {
        List<String> inputLines = Files.readAllLines(inputFile.getFile().toPath());
        List<String> expectedOutputLines = Files.readAllLines(outputFile.getFile().toPath());

        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < inputLines.size(); i++) {
            LoadRequest request = objectMapper.readValue(inputLines.get(i), LoadRequest.class);
            CustomerLoad expectedOutput = objectMapper.readValue(expectedOutputLines.get(i), CustomerLoad.class);

            ResponseEntity<CustomerLoad> response = restTemplate.postForEntity(getUrl(), request, CustomerLoad.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            log.debug(String.valueOf(i));
            log.debug("request: {}", request);
            log.debug("response: {}", response.getBody());
            log.debug("expected: {}", expectedOutput);
            assertNotNull(response.getBody());
            assertEquals(expectedOutput.getId(), response.getBody().getId());
            assertEquals(expectedOutput.getCustomerId(), response.getBody().getCustomerId());
//            assertEquals(0, expectedOutput.getLoadAmount().compareTo(response.getBody().getLoadAmount()));
//            assertEquals(expectedOutput.getTime(), response.getBody().getTime());
            assertEquals(expectedOutput.isAccepted(), response.getBody().isAccepted());
        }
    }
}


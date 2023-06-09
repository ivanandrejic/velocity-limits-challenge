package com.tryvault.velocitylimits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.dto.LoadResponse;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        return "http://localhost:" + port + "/load";
    }

    @Test
    public void testLoadFunds_fromInputFile() throws IOException {
        var inputLines = Files.readAllLines(inputFile.getFile().toPath());
        var expectedOutputLines = Files.readAllLines(outputFile.getFile().toPath());
        var objectMapper = new ObjectMapper();
        for (int i = 0; i < inputLines.size(); i++) {
            if (!StringUtils.hasLength(inputLines.get(i))) {
                log.debug("Empty input line;");
                continue;
            }
            var request = objectMapper.readValue(inputLines.get(i), LoadRequest.class);
            var expectedOutput = new LoadResponse();
            if (StringUtils.hasLength(expectedOutputLines.get(i))) {
                expectedOutput = objectMapper.readValue(expectedOutputLines.get(i), LoadResponse.class);
            } else {
                expectedOutput = null;
            }
            var response = restTemplate.postForEntity(getUrl(), request, LoadResponse.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            log.debug("line: {}", i);
            log.debug("request: {}", request);
            log.debug("response: {}", response.getBody());
            log.debug("expected: {}", expectedOutput);
            if (expectedOutput == null) {
                assertNull(response.getBody());
            } else {
                assertNotNull(response.getBody());
                assertEquals(expectedOutput.getId(), response.getBody().getId());
                assertEquals(expectedOutput.getCustomerId(), response.getBody().getCustomerId());
                assertEquals(expectedOutput.isAccepted(), response.getBody().isAccepted());
            }
        }
    }
}


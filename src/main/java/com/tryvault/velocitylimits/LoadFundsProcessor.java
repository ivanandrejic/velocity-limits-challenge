package com.tryvault.velocitylimits;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class LoadFundsProcessor {

    private static final String API_URL = "http://localhost:8080/load";
    private static final String INPUT_FILE = "input (1) (1) (1).txt";
    private static final String OUTPUT_FILE = "output.txt";

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));
             FileWriter writer = new FileWriter(OUTPUT_FILE)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String jsonResponse = sendLoadRequest(line);
                if (jsonResponse != null) {
                    writer.write(jsonResponse);
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            log.error("Error while processing the input file: {}", e.getMessage());
        }
    }

    private static String sendLoadRequest(String jsonRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error while sending load request: {}", e.getMessage());
        }
        return null;
    }
}

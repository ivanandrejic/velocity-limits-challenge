package com.tryvault.velocitylimits.service;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CustomerLoadServiceTest {

    @Autowired
    private CustomerLoadService service;

    @Autowired
    private CustomerLoadRepository repository;

    @Test
    public void testProcessLoadRequest_acceptsValidLoad() {
        LoadRequest request = new LoadRequest();
        request.setId("1");
        request.setCustomerId("1");
        request.setLoadAmount("$1000");
        request.setTime("2023-05-09T12:00:00Z");

        CustomerLoad result = service.processLoadRequest(request);
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("1", result.getCustomerId());
        assertEquals(0, new BigDecimal("1000").compareTo(result.getLoadAmount()));
        assertEquals(LocalDateTime.parse("2023-05-09T12:00:00Z", DateTimeFormatter.ISO_DATE_TIME), result.getTime());
        assertTrue(result.isAccepted());
    }

}


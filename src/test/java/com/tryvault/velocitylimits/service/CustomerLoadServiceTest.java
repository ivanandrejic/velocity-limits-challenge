package com.tryvault.velocitylimits.service;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.exception.InvalidInputException;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CustomerLoadServiceTest {

    @InjectMocks
    private CustomerLoadService service;

    @Mock
    private CustomerLoadRepository repository;

    @Test
    public void testProcessLoadRequest_acceptsValidLoad() {

        String time = "2023-05-09T12:00:00Z";
        String id = "1";
        LoadRequest request = new LoadRequest();
        request.setId(id);
        request.setCustomerId(id);
        request.setLoadAmount("$1000");
        request.setTime(time);

        CustomerLoad customerLoad = new CustomerLoad();
        customerLoad.setAccepted(true);
        customerLoad.setCustomerId(id);
        customerLoad.setId(id);
        doReturn(customerLoad).when(repository).save(any());

        var result = service.processLoadRequest(request);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(id, result.getCustomerId());
        assertTrue(result.isAccepted());
    }

    @Test
    public void testProcessLoadRequest_invalidInput() {
        var request = new LoadRequest();
        request.setId("");
        request.setCustomerId("");
        request.setLoadAmount("invalid_amount");
        request.setTime("invalid_time");

        assertThrows(InvalidInputException.class, () -> service.processLoadRequest(request));
    }

}


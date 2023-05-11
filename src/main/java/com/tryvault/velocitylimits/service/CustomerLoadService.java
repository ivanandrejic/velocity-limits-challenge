package com.tryvault.velocitylimits.service;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.dto.LoadResponse;
import com.tryvault.velocitylimits.exception.InvalidInputException;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Slf4j
@Service
public class CustomerLoadService {

    public static final int DAILY_TOTAL_LIMIT = 5000;
    public static final int WEEKLY_TOTAL_LIMIT = 20000;
    public static final int DAILY_LOADS_LIMIT = 3;
    public static final String CURRENT_SYMBOL = "$";
    @Autowired
    private CustomerLoadRepository repository;

    public LoadResponse processLoadRequest(LoadRequest request) {
        log.debug("Processing load request: {}", request);

        if (!StringUtils.hasLength(request.getId())) {
            log.warn("Invalid input: Missing ID");
            throw new InvalidInputException("Invalid input: Missing ID");
        }
        if (!StringUtils.hasLength(request.getCustomerId())) {
            log.warn("Invalid input: Missing customer ID");
            throw new InvalidInputException("Invalid input: Missing customer ID");
        }
        Optional<CustomerLoad> byId = repository.findById(request.getId());
        if (byId.isPresent() && byId.get().getCustomerId().equals(request.getCustomerId())) {
            log.info("User ID already loaded, return empty response.");
            return null;
        }
        var dateTime = LocalDateTime.parse(request.getTime(), DateTimeFormatter.ISO_DATE_TIME);
        var date = dateTime.toLocalDate();
        var startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        var dailyLoads = repository.findByCustomerIdAndTimeBetweenAndAcceptedTrue(
                request.getCustomerId(),
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX));

        var dailyTotal = dailyLoads.stream()
                .map(CustomerLoad::getLoadAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var weeklyLoads = repository.findByCustomerIdAndTimeBetweenAndAcceptedTrue(
                request.getCustomerId(),
                startOfWeek.atStartOfDay(),
                startOfWeek.plusDays(6).atTime(LocalTime.MAX));

        var weeklyTotal = weeklyLoads.stream()
                .map(CustomerLoad::getLoadAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var loadAmount = new BigDecimal(request.getLoadAmount().replace(CURRENT_SYMBOL, ""));

        var accepted = dailyLoads.size() < DAILY_LOADS_LIMIT &&
                dailyTotal.add(loadAmount).compareTo(new BigDecimal(DAILY_TOTAL_LIMIT)) <= 0 &&
                weeklyTotal.add(loadAmount).compareTo(new BigDecimal(WEEKLY_TOTAL_LIMIT)) <= 0;

        var customerLoad = new CustomerLoad();
        customerLoad.setId(request.getId());
        customerLoad.setCustomerId(request.getCustomerId());
        customerLoad.setLoadAmount(loadAmount);
        customerLoad.setTime(dateTime);
        customerLoad.setAccepted(accepted);

        var save = repository.save(customerLoad);
        var response = new LoadResponse();
        response.setAccepted(save.isAccepted());
        response.setCustomerId(save.getCustomerId());
        response.setId(save.getId());
        log.debug("Return response: {}", response);
        return response;
    }
}

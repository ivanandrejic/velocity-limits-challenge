package com.tryvault.velocitylimits.service;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.dto.LoadResponse;
import com.tryvault.velocitylimits.exception.InvalidInputException;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

@Service
public class CustomerLoadService {

    @Autowired
    private CustomerLoadRepository repository;

    public LoadResponse processLoadRequest(LoadRequest request) {

        if (!StringUtils.hasLength(request.getId())) {
            throw new InvalidInputException("Invalid input: Missing ID");
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

        var loadAmount = new BigDecimal(request.getLoadAmount().replace("$", ""));

        var accepted = dailyLoads.size() < 3 &&
                dailyTotal.add(loadAmount).compareTo(new BigDecimal(5000)) <= 0 &&
                weeklyTotal.add(loadAmount).compareTo(new BigDecimal(20000)) <= 0;

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
        return response;
    }
}

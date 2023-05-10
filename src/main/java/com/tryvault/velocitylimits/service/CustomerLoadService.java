package com.tryvault.velocitylimits.service;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import com.tryvault.velocitylimits.dto.LoadRequest;
import com.tryvault.velocitylimits.repository.CustomerLoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class CustomerLoadService {

    @Autowired
    private CustomerLoadRepository repository;

    public CustomerLoad processLoadRequest(LoadRequest request) {
        LocalDateTime dateTime = LocalDateTime.parse(request.getTime(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDate date = dateTime.toLocalDate();
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<CustomerLoad> dailyLoads = repository.findByCustomerIdAndTimeBetweenAndAcceptedTrue(
                request.getCustomerId(),
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX));

        BigDecimal dailyTotal = dailyLoads.stream()
                .map(CustomerLoad::getLoadAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CustomerLoad> weeklyLoads = repository.findByCustomerIdAndTimeBetweenAndAcceptedTrue(
                request.getCustomerId(),
                startOfWeek.atStartOfDay(),
                startOfWeek.plusDays(6).atTime(LocalTime.MAX));

        BigDecimal weeklyTotal = weeklyLoads.stream()
                .map(CustomerLoad::getLoadAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal loadAmount = new BigDecimal(request.getLoadAmount().replace("$", ""));

        boolean accepted = dailyLoads.size() < 3 &&
                dailyTotal.add(loadAmount).compareTo(new BigDecimal(5000)) <= 0 &&
                weeklyTotal.add(loadAmount).compareTo(new BigDecimal(20000)) <= 0;

        CustomerLoad customerLoad = new CustomerLoad();
        customerLoad.setId(request.getId());
        customerLoad.setCustomerId(request.getCustomerId());
        customerLoad.setLoadAmount(loadAmount);
        customerLoad.setTime(dateTime);
        customerLoad.setAccepted(accepted);

        return repository.save(customerLoad);
    }
}

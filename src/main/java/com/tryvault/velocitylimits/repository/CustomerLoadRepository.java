package com.tryvault.velocitylimits.repository;

import com.tryvault.velocitylimits.domain.CustomerLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerLoadRepository extends JpaRepository<CustomerLoad, String> {
    List<CustomerLoad> findByCustomerIdAndTimeBetweenAndAcceptedTrue(String customerId, LocalDateTime start, LocalDateTime end);
}

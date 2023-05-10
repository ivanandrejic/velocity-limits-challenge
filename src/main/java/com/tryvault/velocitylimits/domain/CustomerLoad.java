package com.tryvault.velocitylimits.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class CustomerLoad {

    @Id
    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("load_amount")
    private BigDecimal loadAmount;
    private LocalDateTime time;
    private boolean accepted;
}

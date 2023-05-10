package com.tryvault.velocitylimits.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoadResponse {

    @Id
    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    private boolean accepted;
}

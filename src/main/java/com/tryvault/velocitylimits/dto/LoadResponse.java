package com.tryvault.velocitylimits.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonPropertyOrder({ "id", "customerId", "accepted" })
public class LoadResponse {

    @Id
    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    private boolean accepted;
}

package com.tryvault.velocitylimits.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoadRequest {

    private String id;
    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("load_amount")
    private String loadAmount;
    private String time;
}

package com.learn.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashInRequest {

    @JsonProperty("amount")
    private BigDecimal amount;
}

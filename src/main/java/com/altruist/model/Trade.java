package com.altruist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

@Data
public class Trade {

    @JsonIgnore
    private UUID uuid;
    @JsonIgnore
    private UUID accountUuid;
    @NotBlank
    private String symbol;
    @NotNull
    @Size(min = 1)
    private Integer quantity;
    @NotNull
    private TradeSide side;
    @NotNull
    @Size(min = 1)
    private BigDecimal price;
    @NotNull
    private TradeStatus status;
}
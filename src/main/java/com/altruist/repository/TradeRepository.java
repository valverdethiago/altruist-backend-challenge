package com.altruist.repository;

import com.altruist.model.Trade;

import java.util.*;

public interface TradeRepository {

    Trade save(Trade trade);
    void update(Trade trade);
    Trade findById(UUID uuid);


}

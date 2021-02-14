package com.altruist.service.impl;

import com.altruist.exceptions.EntityNotFoundException;
import com.altruist.exceptions.InvalidOperationException;
import com.altruist.model.Trade;
import com.altruist.model.TradeStatus;
import com.altruist.repository.TradeRepository;
import com.altruist.service.TradeService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository repository;

    public TradeServiceImpl(TradeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Trade create(Trade trade) {
       return repository.save(trade);
    }

    @Override
    public List<Trade> list(UUID accountUuid) {
        throw new UnsupportedOperationException("Not implemented Yet");
    }

    @Override
    public void cancelTrade(UUID tradeUuid) {
        Trade trade = repository.findById(tradeUuid);
        if(trade == null) {
            throw new EntityNotFoundException();
        }
        if(trade.getStatus() != TradeStatus.SUBMITTED) {
            throw new InvalidOperationException(
                "It's not allowed to cancel trades that are not on SUBMITTED state"
            );
        }
        trade.setStatus(TradeStatus.CANCELLED);
        repository.update(trade);
    }
}

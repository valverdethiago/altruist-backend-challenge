package com.altruist.service.impl;

import com.altruist.exceptions.EntityNotFoundException;
import com.altruist.exceptions.InvalidOperationException;
import com.altruist.model.Account;
import com.altruist.model.Trade;
import com.altruist.model.TradeStatus;
import com.altruist.repository.AccountRepository;
import com.altruist.repository.TradeRepository;
import com.altruist.service.TradeService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeRepository repository;
    private final AccountRepository accountRepository;

    public TradeServiceImpl(TradeRepository repository, AccountRepository accountRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
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
    public void cancelTrade(UUID accountUuid, UUID tradeUuid) {
        Account account = accountRepository.findById(accountUuid);
        if(account == null) {
            throw new EntityNotFoundException(String.format("Invalid id for account [%s]", accountUuid));
        }
        Trade trade = repository.findById(tradeUuid);
        if(trade == null) {
            throw new EntityNotFoundException(String.format("Invalid id for trade [%s]", tradeUuid));
        }
        if(trade.getAccountUuid() != account.getUuid()) {
            throw new InvalidOperationException(
                "The trade you are trying to cancel doesn't belong to the informed account"
            );
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

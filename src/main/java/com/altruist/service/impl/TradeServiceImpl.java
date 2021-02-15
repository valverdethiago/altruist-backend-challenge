package com.altruist.service.impl;

import com.altruist.exceptions.EntityNotFoundException;
import com.altruist.exceptions.InvalidOperationException;
import com.altruist.model.Account;
import com.altruist.model.Trade;
import com.altruist.model.TradeStatus;
import com.altruist.repository.AccountRepository;
import com.altruist.repository.TradeRepository;
import com.altruist.service.TradeService;
import org.jetbrains.annotations.NotNull;
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
        this.assertAccountExists(trade.getAccountUuid());
        return repository.save(trade);
    }

    @Override
    public List<Trade> list(UUID accountUuid) {
        this.assertAccountExists(accountUuid);
        return this.repository.findByAccount(accountUuid);
    }

    @Override
    public void cancelTrade(UUID accountUuid, UUID tradeUuid) {
        this.assertAccountExists(accountUuid);
        Trade trade = assertThatTradeExistsAndBelongsToAccount(accountUuid, tradeUuid);
        if (trade.getStatus() != TradeStatus.SUBMITTED) {
            throw new InvalidOperationException(
                String.format("It's not allowed to cancel trades that are not on %s state", TradeStatus.SUBMITTED)
            );
        }
        trade.setStatus(TradeStatus.CANCELLED);
        repository.update(trade);
    }

    @NotNull
    private Trade assertThatTradeExistsAndBelongsToAccount(UUID accountUuid, UUID tradeUuid) {
        Trade trade = this.assertTradeExists(tradeUuid);
        if (trade.getAccountUuid() != accountUuid) {
            throw new InvalidOperationException(
                "The trade you are trying to cancel doesn't belong to the informed account"
            );
        }
        return trade;
    }

    private Account assertAccountExists(UUID accountUuid) {
        Optional<Account> account = this.accountRepository.findById(accountUuid);
        account
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Invalid id for account [%s]", accountUuid))
            );
        return account.get();
    }

    private Trade assertTradeExists(UUID tradeUuid) {
        Optional<Trade> trade = repository.findById(tradeUuid);
        trade.orElseThrow(() ->
            new EntityNotFoundException(String.format("Invalid id for trade [%s]", tradeUuid))
        );
        return trade.get();
    }
}

package com.altruist.repository.impl;

import com.altruist.model.Trade;
import com.altruist.model.TradeStatus;
import com.altruist.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;

@Repository
@Slf4j
public class TradeRepositoryImpl implements TradeRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public TradeRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Trade save(Trade trade) {
        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(trade);
        params.registerSqlType("side", Types.VARCHAR);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("Saving trade [{}].", trade);
        String sql = "INSERT INTO trade.trade (account_uuid, symbol, quantity, side, price) " +
            "VALUES (:accountUuid, :symbol, :quantity, :side::trade.trade_side, :price)";
        jdbcOperations.update(sql, params, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (null != keys) {
            UUID id = (UUID) keys.get("trade_uuid");
            TradeStatus status = TradeStatus.valueOf(keys.get("status").toString());
            log.info("Inserted trade record with id {} and status {}.", id, status);
            trade.setUuid(id);
            trade.setStatus(status);
        } else {
            log.warn("Insert of trade record failed. {}", trade);
            throw new RuntimeException("Insert failed for trade");
        }
        return trade;
    }
}

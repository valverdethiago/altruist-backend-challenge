package com.altruist.repository;

import com.altruist.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class AccountRepository {

  private final NamedParameterJdbcOperations jdbcOperations;

  public AccountRepository(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  public Account save(Account account) {
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(account);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    log.info("Saving account [{}].", account);
    String sql = "INSERT INTO trade.account (username,email,address_uuid) VALUES (:username, :email, :addressUuid)";
    jdbcOperations.update(sql, params, keyHolder);
    Map<String, Object> keys = keyHolder.getKeys();
    if (null != keys) {
      UUID id = (UUID) keys.get("account_uuid");
      log.info("Inserted account record {}.", id);
      account.setUuid(id);
    } else {
      log.warn("Insert of account record failed. {}", account);
      throw new RuntimeException("Insert failed for account");
    }
    return account;
  }

}

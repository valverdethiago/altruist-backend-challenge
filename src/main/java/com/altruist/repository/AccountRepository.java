package com.altruist.repository;

import com.altruist.model.Account;
import com.altruist.model.Trade;
import com.altruist.model.TradeSide;
import com.altruist.model.TradeStatus;
import com.altruist.repository.impl.TradeRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
public class AccountRepository {

  private final NamedParameterJdbcOperations jdbcOperations;
  private final JdbcTemplate jdbcTemplate;

  public AccountRepository(NamedParameterJdbcOperations jdbcOperations,
                           JdbcTemplate jdbcTemplate) {
    this.jdbcOperations = jdbcOperations;
    this.jdbcTemplate = jdbcTemplate;
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

  public Account findById(UUID accountUuId) {
    return this.jdbcTemplate.queryForObject(
        "select * " +
            "from trade.account " +
            "where account_uuid = ? ",
        new Object[] {accountUuId},
        new AccountMapper());
  }
  private class AccountMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
      String addressUuid = rs.getString("account_uuid");
      return Account.builder()
          .uuid(UUID.fromString(rs.getString("account_uuid")))
          .addressUuid(addressUuid == null ? null : UUID.fromString(addressUuid))
          .username(rs.getString("username"))
          .email(rs.getString("email"))
          .build();
    }
  }
}

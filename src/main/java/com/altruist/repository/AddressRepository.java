package com.altruist.repository;

import com.altruist.model.Account;
import com.altruist.model.Address;
import com.altruist.model.State;
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
import java.sql.Types;
import java.util.*;

@Repository
@Slf4j
public class AddressRepository {

  private final NamedParameterJdbcOperations jdbcOperations;
  private final JdbcTemplate jdbcTemplate;

  public AddressRepository(NamedParameterJdbcOperations jdbcOperations,
                           JdbcTemplate jdbcTemplate) {
    this.jdbcOperations = jdbcOperations;
    this.jdbcTemplate = jdbcTemplate;
  }

  public Address save(Address address) {
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(address);
    params.registerSqlType("state", Types.VARCHAR);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    log.info("Saving address [{}].", address);
    String sql = "INSERT INTO trade.address (name, street, city, state, zipcode) " +
        "VALUES (:name, :street, :city, :state::trade.state, :zipcode)";
    jdbcOperations.update(sql, params, keyHolder);
    Map<String, Object> keys = keyHolder.getKeys();
    if (null != keys) {
      UUID id = (UUID) keys.get("address_uuid");
      log.info("Inserted address record {}.", id);
      address.setUuid(id);
    } else {
      log.warn("Insert of address record failed. {}", address);
      throw new RuntimeException("Insert failed for address");
    }
    return address;
  }

  public void update(Address address) {
    BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(address);
    params.registerSqlType("state", Types.VARCHAR);
    log.info("Updating address [{}].", address);
    String sql = "UPDATE trade.address SET " +
        "  name = :name, " +
        "  street = :street," +
        "  city = :city," +
        "  state = :state::trade.state," +
        "  zipcode = :zipcode " +
        " WHERE address_uuid = :uuid ";
    try {
      jdbcOperations.update(sql, params);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      log.warn("Update of address record failed. {}", address);
      throw new RuntimeException("Update failed for address", ex);
    }
  }

  public Address findById(UUID addressUuId) {
    return this.jdbcTemplate.queryForObject(
        "select * " +
            "from trade.address " +
            "where address_uuid = ? ",
        new Object[] {addressUuId},
        new AddressMapper());
  }

  public Address findByAccountId(UUID accountUuid) {
    return this.jdbcTemplate.queryForObject(
    "select address.* " +
        "from trade.account as account " +
        "join trade.address as address" +
        "  on account.address_uuid = address.address_uuid " +
        "where account_uuid = ? ",
        new Object[] {accountUuid},
        new AddressMapper());
  }

  public void deleteAddressFromAccount(UUID accountUuid) {
    this.jdbcTemplate.update(
        "delete from trade.address " +
            " where address_uuid = (" +
            "    select address_uuid from trade.account where account_uuid = ? " +
            ") ",
        accountUuid);
  }

  private class AddressMapper implements RowMapper<Address> {

    @Override
    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Address.builder()
          .uuid(UUID.fromString(rs.getString("address_uuid")))
          .name(rs.getString("name"))
          .street(rs.getString("street"))
          .city(rs.getString("city"))
          .state(State.valueOf(rs.getString("state")))
          .zipcode(rs.getInt("zipcode"))
          .build();
    }
  }
}

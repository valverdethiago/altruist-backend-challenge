package com.altruist.repository;

import com.altruist.model.Address;
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
public class AddressRepository {

  private final NamedParameterJdbcOperations jdbcOperations;

  public AddressRepository(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
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
}

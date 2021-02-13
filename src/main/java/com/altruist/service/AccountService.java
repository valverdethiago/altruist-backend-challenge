package com.altruist.service;

import java.util.Objects;
import java.util.UUID;

import com.altruist.repository.AccountRepository;
import com.altruist.model.Account;
import com.altruist.model.AccountDto;
import org.springframework.stereotype.Component;

@Component
public class AccountService {
  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public UUID createAccount(AccountDto accountDto) {
    Objects.requireNonNull(accountDto.username);
    Objects.requireNonNull(accountDto.email);
    Account account = new Account();
    account.username = accountDto.username;
    account.email = accountDto.email;
    account.name = accountDto.name;
    account.street = accountDto.street;
    account.city = accountDto.city;
    account.state = accountDto.state;
    account.zipcode = Integer.parseInt(accountDto.zipcode);

    if (null != accountDto.name ||
        null != accountDto.street ||
        null != accountDto.city ||
        null != accountDto.state ||
        null != accountDto.zipcode
    ) {
      Objects.requireNonNull(accountDto.name);
      Objects.requireNonNull(accountDto.street);
      Objects.requireNonNull(accountDto.city);
      Objects.requireNonNull(accountDto.state);
      Objects.requireNonNull(accountDto.zipcode);
      account = accountRepository.saveAddress(account);
    }

    return accountRepository.save(account)
        .account_uuid;
  }
}

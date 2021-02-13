package com.altruist.service;

import com.altruist.model.Account;
import com.altruist.repository.AccountRepository;
import com.altruist.repository.AddressRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

@Component
public class AccountService {
  private final AccountRepository accountRepository;
  private final AddressService addressService;

  public AccountService(AccountRepository accountRepository,
                        AddressService addressService) {
    this.accountRepository = accountRepository;
    this.addressService = addressService;
  }

  @Validated
  public UUID create(@Valid Account account) {
    if (account.getAddress() != null) {
      account.setAddressUuid(addressService.create(account.getAddress()));
    }
    return accountRepository.save(account).getUuid();
  }
}

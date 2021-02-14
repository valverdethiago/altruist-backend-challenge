package com.altruist.service;

import com.altruist.model.Account;
import com.altruist.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final AddressService addressService;

  public AccountService(AccountRepository accountRepository,
                        AddressService addressService) {
    this.accountRepository = accountRepository;
    this.addressService = addressService;
  }

  public UUID create(Account account) {
    if (account.getAddress() != null) {
      account.setAddressUuid(addressService.create(account.getAddress()));
    }
    return accountRepository.save(account).getUuid();
  }
}

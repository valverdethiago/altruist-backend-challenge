package com.altruist.service;

import com.altruist.exceptions.InvalidOperationException;
import com.altruist.model.Account;
import com.altruist.model.Address;
import com.altruist.repository.AccountRepository;
import org.springframework.stereotype.Service;

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

  public void update(Account account) {
    assertUuidIsInformed(account);
    if (account.getAddress() != null) {
      this.persistAddressFromAccount(account);
    }
    else {
      this.addressService.deleteAddressFromAccount(account.getUuid());
    }
    accountRepository.update(account);
  }

  public Account findById(UUID accountUuid) {
    return accountRepository.findById(accountUuid);
  }

  public List<Account> listAll() {
    return accountRepository.listAll();
  }

  private void persistAddressFromAccount(Account account) {
    Address address = this.addressService.findByAccountUuid(account.getUuid());
    account.getAddress().setUuid(address.getUuid());
    this.addressService.update(address);
  }

  private void assertUuidIsInformed(Account account) {
    if(account.getUuid() == null) {
      throw new InvalidOperationException("In order to update an account you should provide its uuid");
    }
  }
}

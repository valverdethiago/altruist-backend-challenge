package com.altruist.service.impl;

import com.altruist.exceptions.EntityNotFoundException;
import com.altruist.exceptions.InvalidOperationException;
import com.altruist.model.Account;
import com.altruist.model.Address;
import com.altruist.repository.AccountRepository;
import com.altruist.repository.AddressRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddressServiceImpl implements com.altruist.service.AddressService {
  private final AddressRepository addressRepository;
  private final AccountRepository accountRepository;

  public AddressServiceImpl(AddressRepository addressRepository,
                            AccountRepository accountRepository) {
    this.addressRepository = addressRepository;
    this.accountRepository = accountRepository;
  }

  @Override
  public UUID create(Address address) {
    return addressRepository.save(address).getUuid();
  }

  @Override
  public UUID create(@NonNull UUID accountUuid, Address address) {
    Account account = assertAccountExists(accountUuid);
    return this.create(account, address);
  }

  @Override
  public void update(Address address) {
    if(address.getUuid() == null) {
      throw new InvalidOperationException(
          "In order to update the address you must provide the uuid"
      );
    }
    addressRepository.update(address);
  }

  @Override
  public void update(@NonNull UUID accountUuid, Address address) {
    assertAccountExists(accountUuid);
    Address currentAddress = this.findByAccountUuid(accountUuid);
    if(currentAddress == null) {
      throw new InvalidOperationException("Address doesn't exist yet");
    }
    address.setUuid(currentAddress.getUuid());
    this.update(address);
  }

  @Override
  public Address findByAccountUuid(@NonNull UUID accountUuid) {
    return addressRepository.findByAccountId(accountUuid);
  }

  @Override
  public void deleteAddressFromAccount(@NonNull UUID accountUuid) {
    addressRepository.deleteAddressFromAccount(accountUuid);
  }

  private UUID create(@NonNull Account account, Address address) {
    UUID uuid = addressRepository.save(address).getUuid();
    account.setAddressUuid(uuid);
    this.accountRepository.update(account);
    return uuid;
  }

  private Account assertAccountExists(UUID accountUuid) {
    Account account = this.accountRepository.findById(accountUuid);
    if(account == null) {
        throw new EntityNotFoundException(String.format("Invalid id for account [%s]", accountUuid));
    }
    return account;
  }
}

package com.altruist.service;

import com.altruist.model.Address;
import com.altruist.repository.AddressRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

@Component
public class AddressService {
  private final AddressRepository addressRepository;

  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  @Validated
  public UUID create(@Valid Address address) {
    return addressRepository.save(address).getUuid();
  }
}

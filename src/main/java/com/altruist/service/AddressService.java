package com.altruist.service;

import com.altruist.model.Address;
import com.altruist.repository.AddressRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;

@Service
public class AddressService {
  private final AddressRepository addressRepository;

  public AddressService(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }


  public UUID create(Address address) {
    return addressRepository.save(address).getUuid();
  }
}

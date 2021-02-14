package com.altruist.repository;

import com.altruist.model.Address;

import java.util.*;

public interface AddressRepository {
    Address save(Address address);

    void update(Address address);

    Address findById(UUID addressUuId);

    Address findByAccountId(UUID accountUuid);

    void deleteAddressFromAccount(UUID accountUuid);
}

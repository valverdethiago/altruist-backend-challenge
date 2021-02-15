package com.altruist.resources;

import com.altruist.IdDto;
import com.altruist.model.Address;
import com.altruist.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/accounts/{accountId}/address")
@Slf4j
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    @PostMapping(
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> create(@PathVariable("accountId") UUID accountId,
                                        @RequestBody @Valid Address address,
                                        HttpServletRequest httpServletRequest)
        throws URISyntaxException {
        log.info("Received Address creation request [{}].", address);
        UUID uuid = addressService.create(accountId, address);
        URI entityURI =  new URI(httpServletRequest.getRequestURL().toString());
        return ResponseEntity.created(entityURI)
            .body(new IdDto(uuid));
    }

    @PutMapping(
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> update(@PathVariable("accountId") UUID accountId,
                                        @RequestBody @Valid Address address)
        throws URISyntaxException {
        log.info("Received Address creation request [{}].", address);
        addressService.update(accountId, address);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> get(@PathVariable("accountId") UUID accountId) {
        log.info("Listing trades for account [{}].", accountId);
        AtomicReference<ResponseEntity<Address>> result = new AtomicReference<>();
        addressService.findByAccountUuid(accountId).ifPresentOrElse(
            (value) -> result.set(ResponseEntity.ok().body(value)),
            () -> result.set(ResponseEntity.noContent().build())
        );
        return result.get();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable("accountId") UUID accountId) {
        log.info("Deleting address from account {}", accountId);
        addressService.deleteAddressFromAccount(accountId);
    }

}

package com.altruist.resources;

import com.altruist.IdDto;
import com.altruist.model.Account;
import com.altruist.service.AccountService;
import com.altruist.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.*;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> create(@RequestBody @Valid Account account,
                                        HttpServletRequest httpServletRequest) {
        log.info("Received Account creation request [{}].", account);
        UUID accountId = accountService.create(account);
        URI entityURI = HttpUtils.buildEntityUrl(httpServletRequest, accountId);
        return ResponseEntity.created(entityURI)
            .body(new IdDto(accountId));
    }

}

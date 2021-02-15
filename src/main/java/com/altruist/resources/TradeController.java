package com.altruist.resources;

import com.altruist.IdDto;
import com.altruist.model.Account;
import com.altruist.model.Trade;
import com.altruist.service.TradeService;
import com.altruist.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/accounts/{accountUuid}/trades")
@Slf4j
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }


    @PostMapping(
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> create(@PathVariable("accountUuid") UUID accountUuid,
                                        @RequestBody @Valid Trade trade,
                                        HttpServletRequest httpServletRequest) {
        log.info("Received Trade creation request [{}].", trade);
        trade.setAccountUuid(accountUuid);
        Trade dbTrade = tradeService.create(trade);
        URI entityURI = HttpUtils.buildEntityUrl(httpServletRequest, dbTrade.getUuid());
        return ResponseEntity.created(entityURI)
            .body(new IdDto(dbTrade.getUuid()));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Trade>> list(@PathVariable("accountUuid") UUID accountUuid) {
        log.info("Listing trades for account [{}].", accountUuid);
        List<Trade> trades = tradeService.list(accountUuid);
        if(trades == null || trades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
            .body(trades);
    }


    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "/{tradeUuid}")
    public ResponseEntity<Trade> get(@PathVariable("accountUuid") UUID accountUuid,
                                       @PathVariable("tradeUuid") UUID tradeUuid) {
        log.info("Fetching trade {} from account {}.", tradeUuid, accountUuid);
        AtomicReference<ResponseEntity<Trade>> result = new AtomicReference<>();
        tradeService.findByIdAndAccountId(tradeUuid, accountUuid).ifPresentOrElse(
            (trade) -> result.set(ResponseEntity.ok(trade)),
            () -> result.set(ResponseEntity.notFound().build())
        );
        return result.get();
    }

    @DeleteMapping(value = "/{tradeUuid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cancel(@PathVariable("accountUuid") UUID accountId,
                       @PathVariable("tradeUuid") UUID tradeId) {
        log.info("Canceling trade {} of account {}", tradeId, accountId);
        tradeService.cancelTrade(accountId, tradeId);
    }

}

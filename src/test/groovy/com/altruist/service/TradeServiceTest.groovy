package com.altruist.service

import com.altruist.exceptions.EntityNotFoundException
import com.altruist.exceptions.InvalidOperationException
import com.altruist.model.Account
import com.altruist.model.Trade
import com.altruist.model.TradeSide
import com.altruist.model.TradeStatus
import com.altruist.repository.AccountRepository
import com.altruist.repository.TradeRepository
import com.altruist.service.impl.TradeServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [TestConfig])
class TradeServiceTest extends Specification {
    @Autowired
    TradeRepository mockTradeRepository

    @Autowired
    AccountRepository mockAccountRepository

    @Autowired
    TradeService service

    @Shared
    Trade trade

    @Shared
    Account account

    def setup() {
        account = new Account(
                uuid: UUID.randomUUID(),
                username: "someusername",
                email: "somemail@email.com"
        )
        trade = new Trade(
                accountUuid: account.uuid,
                symbol: "SYMBOL",
                quantity : 100,
                side: TradeSide.BUY,
                price : BigDecimal.valueOf(150.00)
        )

    }


    def "Should save trade"() {
        given: "an trade"
        UUID expectedUuid = UUID.randomUUID()

        when:
        trade = service.create(trade)

        then: "the trade is saved"
        1 * mockTradeRepository.save(_) >>  { Trade arg ->
            with(arg){
                accountUuid: trade.accountUuid
                symbol: trade.symbol
                quantity: trade.quantity
                side: trade.side
                price: trade.price
            }

            arg.uuid = expectedUuid
            arg.status = TradeStatus.SUBMITTED
            arg
        }

        and: "the status is saved"
        trade.status == TradeStatus.SUBMITTED

        and: "the uuid is saved"
        trade.uuid == expectedUuid
    }


    def "Should cancel trade"() {
        given: "an trade uuid"
        UUID expectedUuid = UUID.randomUUID()

        and: "a trade with that uuid and submitted status"
        trade.uuid = expectedUuid
        trade.status = TradeStatus.SUBMITTED

        and: "a repository that finds the trade by id"
        1 * mockTradeRepository.findById(expectedUuid) >> trade

        and: "a repository that finds the account by id"
        1 * mockAccountRepository.findById(account.uuid) >> account

        when:
        trade = service.cancelTrade(account.uuid, trade.uuid)

        then: "the trade is cancelled"
        1 * mockTradeRepository.update(_) >>  { Trade arg ->
            with(arg){
                uuid: expectedUuid
                accountUuid: trade.accountUuid
                symbol: trade.symbol
                quantity: trade.quantity
                side: trade.side
                price: trade.price
                status: TradeStatus.CANCELLED
            }

            arg
        }
    }


    def "Should not allow cancel trade in state other than SUBMITTED"() {
        given: "an trade uuid"
        UUID expectedUuid = UUID.randomUUID()

        and: "a trade with that uuid and submitted status"
        trade.uuid = expectedUuid
        trade.status = TradeStatus.COMPLETED

        and: "a repository that find the entity by id"
        1 * mockTradeRepository.findById(expectedUuid) >> trade

        and: "a repository that finds the account by id"
        1 * mockAccountRepository.findById(account.uuid) >> account

        when:
        trade = service.cancelTrade(account.uuid, trade.uuid)

        then: "the trade is cancelled"
        thrown(InvalidOperationException)
    }

    def "Should throw an error trying to cancel a not found trade"() {
        given: "an uuid"
        UUID expectedUuid = UUID.randomUUID()

        and: "a repository that can't find the entity by id"
        1 * mockTradeRepository.findById(expectedUuid) >> null

        and: "a repository that finds the account by id"
        1 * mockAccountRepository.findById(account.uuid) >> account

        when:
        trade = service.cancelTrade(account.uuid, expectedUuid)

        then: "an exception is thrown"
        thrown(EntityNotFoundException)
    }

    def "Should throw an error trying to cancel a trade that doesn't belong to account"() {
        given: "an uuid"
        UUID expectedUuid = UUID.randomUUID()

        and: "a repository that can't find the entity by id"
        1 * mockTradeRepository.findById(expectedUuid) >> trade

        and: "a repository that finds another different account"
        1 * mockAccountRepository.findById(trade.accountUuid) >> new Account(
                uuid: UUID.randomUUID(),
                username: "anotherusername",
                email: "anothermail@email.com"
        )

        when:
        trade = service.cancelTrade(trade.accountUuid, expectedUuid)

        then: "an exception is thrown"
        thrown(InvalidOperationException)
    }


    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        TradeRepository tradeRepository() {
            factory.Mock(TradeRepository)
        }
        @Bean
        AccountRepository accountRepository() {
            factory.Mock(AccountRepository)
        }

        @Bean
        TradeService tradeService(TradeRepository tradeRepository, AccountRepository accountRepository) {
            return new TradeServiceImpl(tradeRepository, accountRepository)
        }
    }
}

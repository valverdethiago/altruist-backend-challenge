package com.altruist.repository

import com.altruist.config.DatabaseConfiguration
import com.altruist.config.RepositoryConfiguration
import com.altruist.model.Account
import com.altruist.model.Address
import com.altruist.model.Trade
import com.altruist.model.TradeSide
import com.altruist.model.TradeStatus
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@ActiveProfiles("test")
@DataJdbcTest(includeFilters = [@ComponentScan.Filter(type = FilterType.ANNOTATION, value = [Repository])])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [DatabaseConfiguration, RepositoryConfiguration])
@Stepwise
@Rollback(true)
class TradeRepositoryTest extends Specification {
    @Autowired
    TradeRepository repository
    @Autowired
    AccountRepository accountRepository;

    @Shared
    static Account account

    def setup() {
        account = new Account(
                username: "someusername",
                email: "somemail@email.com"
        )
        account = accountRepository.save(account)
    }

    def "Inserts a trade"() {
        given: "an trade"
        Trade trade = new Trade(
                accountUuid: account.uuid,
                symbol: "APPL",
                quantity: 100,
                side: TradeSide.BUY,
                price: BigDecimal.valueOf(100.50)
        )

        when:
        trade = repository.save(trade)

        then: "the trade id is returned"
        trade.uuid

        and: "the trade status is the default one"
        trade.status == TradeStatus.SUBMITTED
    }

}

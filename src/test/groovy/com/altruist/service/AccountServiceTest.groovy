package com.altruist.service

import com.altruist.model.Account
import com.altruist.model.AccountDto
import com.altruist.repository.AccountRepository
import com.altruist.service.AccountService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

@ContextConfiguration(classes = [TestConfig])
class AccountServiceTest extends Specification {
    @Autowired
    AccountRepository mockAccountRepo
    @Autowired
    AccountService srv

    @Unroll
    def "Should validate for missing account field #field"() {
        given: "an account missing fields"
        AccountDto account = new AccountDto(
                username: "username123",
                email: "email@example.com",
        )
        account[field] = null

        when:
        srv.createAccount(account)

        then:
        thrown(NullPointerException)

        where:
        field << ["username", "email"]
    }

    def "Should validate for missing address field #field"() {
        given: "an address missing fields"
        AccountDto account = new AccountDto(
                username: "username123",
                email: "email@example.com",
                name: "Some Name",
                street: "Some street",
                city: "Some city",
                state: "CA",
                zipcode: 99999
        )
        account[field] = null

        when:
        srv.createAccount(account)

        then:
        thrown(NullPointerException)

        where:
        field << ["name", "street", "city", "state"]
    }

    def "Should validate for missing address field zipcode"() {
        given: "an address missing zipcode"
        AccountDto account = new AccountDto(
                username: "username123",
                email: "email@example.com",
                name: "Some Name",
                street: "Some street",
                city: "Some city",
                state: "CA"
        )

        when:
        srv.createAccount(account)

        then:
        thrown(NumberFormatException)
    }

    def "Should save account and address"() {
        given: "an account"
        AccountDto account = new AccountDto(
                username: "username123",
                email: "email@example.com",
                name: "Some Name",
                street: "Some street",
                city: "Some city",
                state: "CA",
                zipcode: 99999
        )
        UUID expectedAddressId = UUID.randomUUID()
        UUID expectedAccountId = UUID.randomUUID()

        when:
        srv.createAccount(account)

        then: "the address is saved"
        1 * mockAccountRepo.saveAddress(_) >> { Account arg ->
            with(arg){
                name == account.name
                street == account.street
                city == account.city
                state == account.state
                zipcode == account.zipcode as Integer
            }

            arg.address_uuid = expectedAddressId
            arg
        }

        and: "the account is saved"
        1 * mockAccountRepo.save(_) >> { Account arg ->
            with(arg){
                username == account.username
                email == account.email
                address_uuid == expectedAddressId
            }

            arg.account_uuid = expectedAccountId
            arg
        }
    }


    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        AccountRepository accountRepository() {
            factory.Mock(AccountRepository)
        }

        @Bean
        AccountService accountService(AccountRepository accountRepository) {
            return new AccountService(accountRepository);
        }
    }
}

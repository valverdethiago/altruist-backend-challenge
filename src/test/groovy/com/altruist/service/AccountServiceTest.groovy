package com.altruist.service

import com.altruist.model.Account
import com.altruist.model.Address
import com.altruist.model.State
import com.altruist.repository.AccountRepository
import com.altruist.repository.AddressRepository
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
    AccountRepository mockAccountRepository
    @Autowired
    AddressService mockAddressService
    @Autowired
    AccountService service

    @Unroll
    def "Should validate for missing account field #field"() {
        given: "an account missing fields"
        Account account = new Account(
                username: "username123",
                email: "email@example.com",
        )
        account[field] = null

        when:
        service.create(account)

        then:
        thrown(NullPointerException)

        where:
        field << ["username", "email"]
    }

    def "Should validate for missing address field #field"() {
        given: "an address missing fields"
        Account account = new Account(
                username: "username123",
                email: "email@example.com",
                address: new Address(
                    name: "Some Name",
                    street: "Some street",
                    city: "Some city",
                    state: State.CA,
                    zipcode: 99999
                )
        )
        account.address[field] = null

        when:
        service.create(account)

        then:
        thrown(NullPointerException)

        where:
        field << ["name", "street", "city", "state"]
    }

    def "Should validate for missing address field zipcode"() {
        given: "an address missing zipcode"
        Account account = new Account(
                username: "username123",
                email: "email@example.com",
                address: new Address(
                    name: "Some Name",
                    street: "Some street",
                    city: "Some city",
                    state: State.CA
                )
        )

        when:
        service.create(account)

        then:
        thrown(NullPointerException)
    }

    def "Should save account and address"() {
        given: "an account"
        Account account = new Account(
                username: "username123",
                email: "email@example.com",
                address : new Address(
                    name: "Some Name",
                    street: "Some street",
                    city: "Some city",
                    state: State.CA,
                    zipcode: 99999
                )
        )
        UUID expectedAddressId = UUID.randomUUID()
        UUID expectedAccountId = UUID.randomUUID()

        when:
        service.create(account)

        then: "the address is saved"
        1 * mockAddressService.create(_) >> { Address arg ->
            with(arg){
                name == account.address.name
                street == account.address.street
                city == account.address.city
                state == account.address.state
                zipcode == account.address.zipcode
            }

            arg.uuid = expectedAddressId
            arg
        }

        and: "the account is saved"
        1 * mockAccountRepository.save(_) >> { Account arg ->
            with(arg){
                username == account.username
                email == account.email
                addressUuid == expectedAddressId
            }

            arg.uuid = expectedAccountId
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
        AddressService addressService() {
            factory.Mock(AddressService)
        }

        @Bean
        AccountService accountService(AccountRepository accountRepository, AddressService addressService) {
            return new AccountService(accountRepository, addressService);
        }
    }
}
